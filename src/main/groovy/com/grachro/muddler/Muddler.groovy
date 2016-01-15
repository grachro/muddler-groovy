package com.grachro.muddler

import groovy.sql.Sql
import groovy.text.GStringTemplateEngine
import spark.Request

import static spark.Spark.get
import static spark.Spark.post
import static spark.Spark.externalStaticFileLocation

/**
 * Created by grachro on 2016/01/10.
 */
class Muddler {

    def static workspace
    def static scriptRoot
    def static staticFileRoot
    def static databases = [:]

    public static void main(String[] args) {
        workspace = System.properties.get("workspace") ?: "workspace"
        scriptRoot = "${workspace}/script"
        staticFileRoot = "${workspace}/web"

        databases.localDb = {
            Sql.newInstance("jdbc:sqlite:${workspace}/muddlerLocal.sqlite3", null, null, "org.sqlite.JDBC")
        }

        initDb()

        externalStaticFileLocation staticFileRoot

        get "/md/:path1", {req, res ->
            def path1 = req.params(":path1")
            def path = "${scriptRoot}/${path1}.groovy"
            evaluateShell(req, res, path)
        }

        get "/md/:path1/:path2", {req, res ->
            def path1 = req.params(":path1")
            def path2 = req.params(":path2")
            def path = "${scriptRoot}/${path1}/${path2}.groovy"
            evaluateShell(req, res, path)
        }

        get "/md/:path1/:path2/:path3", {req, res ->
            def path1 = req.params(":path1")
            def path2 = req.params(":path2")
            def path3 = req.params(":path3")
            def path = "${scriptRoot}/${path1}/${path2}/${path3}.groovy"
            evaluateShell(req, res, path)
        }

        post "/md/:path1", {req, res ->
            def path1 = req.params(":path1")
            def path = "${scriptRoot}/${path1}.groovy"
            evaluateShell(req, res, path)
        }

        post "/md/:path1/:path2", {req, res ->
            def path1 = req.params(":path1")
            def path2 = req.params(":path2")
            def path = "${scriptRoot}/${path1}/${path2}.groovy"
            evaluateShell(req, res, path)
        }

        post "/md/:path1/:path2/:path3", {req, res ->
            def path1 = req.params(":path1")
            def path2 = req.params(":path2")
            def path3 = req.params(":path3")
            def path = "${scriptRoot}/${path1}/${path2}/${path3}.groovy"
            evaluateShell(req, res, path)
        }

        //DB登録画面 表示
        get "/system/database", { req, res ->
            showDatabaseEditHtml(getClass(),loadDatabaseScript())
        }

        //DB登録画面 登録
        post "/system/database", { req, res ->
            def dbscript = req.queryParams("databases")
            println dbscript

            try {
                refreshDb(dbscript)
                saveDatabaseScript dbscript
                showDatabaseEditHtml(getClass(),loadDatabaseScript())
            } catch(e) {
                e.printStackTrace()
                showDatabaseEditHtml(getClass(),dbscript)
            }

        }
    }

    private static showDatabaseEditHtml(clazz,dbScript) {
        def input = clazz.getClassLoader().getResourceAsStream("database.html")
        def htmlTemplege = input.getText("UTF-8")

        def binding = [
                script:dbScript,
        ]

        def template = new GStringTemplateEngine().createTemplate(htmlTemplege).make(binding)
        return template.toString()
    }

    private static evaluateShell(req, res, path) {
        def f = new File(path)
        def groovyString = f.getText("UTF-8")

        def muddler = Muddler.newInstance(req)
        def binding = [
                muddler: muddler,
                md: muddler,
                viewParams:muddler.viewParams,
                workspace:workspace,
                scriptRoot:scriptRoot,
        ] as Binding
        def shell = new GroovyShell(binding)
        return shell.evaluate(groovyString)
    }

    private static loadDatabaseScript() {
        def f = new File("${workspace}/conf/database.groovy")
        f.getText()
    }

    private static saveDatabaseScript(text) {
        def f = new File("${workspace}/conf/database.groovy")
        f.setText(text,"UTF-8")
    }

    private static void initDb() {
        refreshDb(loadDatabaseScript())
    }

    private static void refreshDb(groovyString) {
        def database = {Map params ->
            databases[params.name] = {
                Sql.newInstance(params.url, params.user, params.password, params.driverClassName)
            }
            println "add database ${params.name} -> ${params.url}"
        }

        def binding = [
                database:database,
        ] as Binding
        def shell = new GroovyShell(binding)
        shell.evaluate(groovyString)
    }


    def viewParams = [:]

    public Muddler(Request request) {

        for (String queryParam : request.queryParams()) {
            String[] ss = request.queryParamsValues(queryParam);
            if (ss == null) {
                continue;
            }

            if (ss.length == 1) {
                viewParams.put(queryParam, ss[0]);
            } else {
                viewParams.put(queryParam, ss);
            }

        }

    }

    def loadScript(fileName) {
        loadScript fileName, [:]
    }

    def loadScript(fileName, Map params) {
        params.muddler = this
        params.md = this
        params.viewParams = viewParams
        params.workspace = workspace
        params.scriptRoot = scriptRoot
        new GroovyShell(params as Binding).parse(new File("${scriptRoot}/${fileName}"))
    }

    public Sql openDb(databaseName) {
        databases[databaseName].call()
    }

    public Sql openLocalDb() {
        openDb("localDb")
    }

    public Table loadTable(databaseName,sql) {

        if (databases[databaseName] == null) {
            throw new IllegalArgumentException("databaseName \"${databaseName}\" is unregistered. See [workspace]/conf/database.groovy")
        }

        def db = databases[databaseName].call()

        def tbl = Table.newInstance()
        tbl.load(db, sql)
    }

    public String loadHtml(fileName) {
        def f = new File("${Muddler.scriptRoot}/${fileName}")
        def engine = new GStringTemplateEngine()

        def binding = [
                muddler: this,
                md: this,
        ]
        binding += this.viewParams

        def template = engine.createTemplate(f).make(binding)
        return template.toString()
    }

    public String importTemplete(String templete, Map binding) {
        def f = new File("${workspace}/templete/${templete}")
        def engine = new GStringTemplateEngine()
        def template = engine.createTemplate(f).make(binding)
        return template.toString()
    }

    public String tableToHtml(Table table) {
        if (table.fieldGroups == null) {
            importTemplete "simpleTable.groovy_templete", ["table":table]
        } else {
            importTemplete "groupTable.groovy_templete", ["table":table]
        }
    }
}
