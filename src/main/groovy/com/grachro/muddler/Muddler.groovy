package com.grachro.muddler

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

            def binding = [
                    databases: databases,
                    md: new MuddlerViewUtils(),
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
}
