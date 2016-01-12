package com.grachro.muddler

import spark.Request

import static spark.Spark.get

/**
 * Created by grachro on 2016/01/10.
 */
class Muddler {

    def static workspace
    def static scriptRoot
    def static databases = [:]

    public static void main(String[] args) {
        workspace = System.properties.get("workspace") ?: "workspace"
        scriptRoot = "${workspace}/script"
        initDb()

        get "/:path1", {req, res ->

            def path1 = req.params(":path1")
            def f = new File("${scriptRoot}/${path1}.groovy")
            def groovyString = f.getText()

            def muddler = Muddler.newInstance(req)
            def binding = [
                    muddler: muddler,
                    viewParams:muddler.viewParams
            ] as Binding
            def shell = new GroovyShell(binding)
            return shell.evaluate(groovyString)
        }
    }

    private static void initDb() {
        def f = new File("${workspace}/conf/database.groovy")
        def groovyString = f.getText()

        def binding = [
                databases: databases,
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

    public Table loadTable(databaseName,sql) {
        def db = databases[databaseName].call()

        def tbl = Table.newInstance()
        tbl.load(db, sql)
    }

    public String loadHtml(fileName) {
        def f = new File("${Muddler.scriptRoot}/${fileName}")
        def engine = new groovy.text.GStringTemplateEngine()

        def binding = [
                muddler: this,
        ]
        binding += this.viewParams

        def template = engine.createTemplate(f).make(binding)
        return template.toString()
    }

    public String importTemplete(String templete, Map binding) {
        def f = new File("${workspace}/templete/${templete}")
        def engine = new groovy.text.GStringTemplateEngine()
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
