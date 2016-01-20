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

    private static SystemDatabaseEdit systemConf

    public static void main(String[] args) {
        start()
    }

    public static void start(String[] args) {

        def ws = System.properties.get("workspace") ?: "workspace"
        println "workspace args=${ws}"

        workspace = new java.io.File(ws).getAbsolutePath()
        println "workspace=${workspace}"


        scriptRoot = "${workspace}/script"
        staticFileRoot = "${workspace}/web"

        externalStaticFileLocation staticFileRoot

        //make default workspace
        if (!new File(workspace).exists()){
            println "create default workspace"
            java.io.File parent = new File(workspace).parentFile
            parent.mkdirs()

            def input = MuddlerUtils.loadResourceFile("workspace.zip")
            try {
                def file = new File(parent,"workspace.zip")
                file.append(input)
                new AntBuilder().unzip(src:file.absolutePath, dest:workspace)
            } finally {
                input.close()
            }
        }

        get "/", { req, res ->
            res.redirect("/index.html")
        }

        get "/system/menu", { req, res ->
            def htmlTemplete = MuddlerUtils.loadResourceFileText("systemMenu.html")
            def binding = [
                    scriptList: SystemScriptEdit.loadAllScripitNames(),
                    message   : null,
            ]

            def template = new GStringTemplateEngine().createTemplate(htmlTemplete).make(binding)
            return template.toString()
        }

        SystemDatabaseEdit.init()
        SystemScriptEdit.init()

        get "/md/:path1", { req, res ->
            def path1 = req.params(":path1")
            def path = "${scriptRoot}/${path1}.groovy"
            evaluateShell(req, res, path)
        }

        get "/md/:path1/:path2", { req, res ->
            def path1 = req.params(":path1")
            def path2 = req.params(":path2")
            def path = "${scriptRoot}/${path1}/${path2}.groovy"
            evaluateShell(req, res, path)
        }

        get "/md/:path1/:path2/:path3", { req, res ->
            def path1 = req.params(":path1")
            def path2 = req.params(":path2")
            def path3 = req.params(":path3")
            def path = "${scriptRoot}/${path1}/${path2}/${path3}.groovy"
            evaluateShell(req, res, path)
        }

        post "/md/:path1", { req, res ->
            def path1 = req.params(":path1")
            def path = "${scriptRoot}/${path1}.groovy"
            evaluateShell(req, res, path)
        }

        post "/md/:path1/:path2", { req, res ->
            def path1 = req.params(":path1")
            def path2 = req.params(":path2")
            def path = "${scriptRoot}/${path1}/${path2}.groovy"
            evaluateShell(req, res, path)
        }

        post "/md/:path1/:path2/:path3", { req, res ->
            def path1 = req.params(":path1")
            def path2 = req.params(":path2")
            def path3 = req.params(":path3")
            def path = "${scriptRoot}/${path1}/${path2}/${path3}.groovy"
            evaluateShell(req, res, path)
        }

    }


    private static evaluateShell(req, res, path) {
        try {
            def f = new File(path)
            def groovyString = f.getText("UTF-8")

            def muddler = Muddler.newInstance(req)
            def binding = [
                    muddler    : muddler,
                    md         : muddler,
                    loadTable  : muddler.loadTableCl,
                    loadHtml   : muddler.loadHtmlCl,
                    tableToHtml: muddler.tableToHtmlCl,
                    showTable  : muddler.showTableCl,
                    viewParams : muddler.viewParams,
                    workspace  : workspace,
                    scriptRoot : scriptRoot,
            ] as Binding
            def shell = new GroovyShell(binding)
            return shell.evaluate(groovyString)
        } catch (e) {
            e.printStackTrace()

            StringWriter sw = new StringWriter()
            PrintWriter pw = new PrintWriter(sw)
            e.printStackTrace(pw)
            "<pre>" + sw.toString() + "</pre>"
        }

    }

    def viewParams = [:]

    def loadTableCl = { databaseName, sql ->
        if (databases[databaseName] == null) {
            throw new IllegalArgumentException("databaseName \"${databaseName}\" is unregistered. See [workspace]/conf/database.groovy")
        }

        def db = databases[databaseName].call()

        def tbl = Table.newInstance()
        tbl.load(db, sql)
    }

    def loadHtmlCl = { fileName ->
        def f = new File("${Muddler.scriptRoot}/${fileName}")
        def engine = new GStringTemplateEngine()

        def binding = [
                muddler   : this,
                md        : this,
                workspace : workspace,
                scriptRoot: scriptRoot,
        ]
        binding += this.viewParams

        def template = engine.createTemplate(f).make(binding)
        return template.toString()
    }

    def tableToHtmlCl = { Table table ->
        if (table.fieldGroups == null) {
            importTemplete "simpleTable.groovy_templete", ["table": table]
        } else {
            importTemplete "groupTable.groovy_templete", ["table": table]
        }
    }

    def showTableCl = { Table... tables ->
        this.viewParams.tables = tables
        loadHtmlCl("defaultTable.html")
    }

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

    public Table loadTable(databaseName, sql) {
        loadTableCl(databaseName, sql)
    }

    public String loadHtml(fileName) {
        loadHtmlCl(fileName)
    }

    public String importTemplete(String templete, Map binding) {
        def f = new File("${workspace}/templete/${templete}")
        def engine = new GStringTemplateEngine()
        def template = engine.createTemplate(f).make(binding)
        return template.toString()
    }

    public String tableToHtml(Table table) {
        tableToHtmlCl(table)
    }

    //tables is Table or [Table]
    public String showTable(Table... tables) {
        showTableCl(tables)
    }
}
