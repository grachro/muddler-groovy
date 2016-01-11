package com.grachro.muddler

import static spark.Spark.get

/**
 * Created by grachro on 2016/01/10.
 */
class Muddler {

    def workspace
    def scriptRoot
    def databases = [:]

    public static void main(String[] args) {
        new Muddler()
    }

    public Muddler() {
        this.workspace = System.properties.get("workspace") ?: "workspace"
        this.scriptRoot = "${this.workspace}/script"
        initDb()

        get "/:path1", {req, res ->

            def path1 = req.params(":path1")
            def f = new File("${this.scriptRoot}/${path1}.groovy")
            def groovyString = f.getText()

            def binding = [
                    databases: this.databases,
                    muddler: this,
            ] as Binding
            def shell = new GroovyShell(binding)
            return shell.evaluate(groovyString)
        }
    }

    private void initDb() {
        def f = new File("${this.workspace}/conf/database.groovy")
        def groovyString = f.getText()

        def binding = [
                databases: this.databases,
        ] as Binding
        def shell = new GroovyShell(binding)
        shell.evaluate(groovyString)
    }

    public Table loadTable(databaseName,sql) {
        def db = this.databases[databaseName].call()

        def tbl = Table.newInstance()
        tbl.load(db, sql)
    }

    public String importTemplete(String templete, Map binding) {
        def f = new File("${this.workspace}/templete/${templete}")
        def engine = new groovy.text.SimpleTemplateEngine()
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
