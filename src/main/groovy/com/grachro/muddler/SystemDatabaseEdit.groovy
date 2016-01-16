package com.grachro.muddler

import groovy.sql.Sql;

import static spark.Spark.get;
import static spark.Spark.post;

import groovy.text.GStringTemplateEngine;

public class SystemDatabaseEdit {


    public static init() {

        //DB登録画面 表示
        get "/system/database", { req, res ->
            editHtml(loadDatabaseScript(), "")
        }

        //DB登録画面 登録
        post "/system/database", { req, res ->
            def dbscript = req.queryParams("databases")
            println dbscript

            try {
                refreshDb(dbscript)
                saveDatabaseScript dbscript
                editHtml(loadDatabaseScript(), "success.")
            } catch (e) {
                e.printStackTrace()
                editHtml(dbscript, e.getStackTrace())
            }
        }

        refreshDb(loadDatabaseScript())
    }

    private static editHtml(dbScript,String message) {
        def htmlTemplete = SystemUtils.loadResourceFile("database.html")

        def binding = [
            script:dbScript,
            message:message,
        ]

        def template = new GStringTemplateEngine().createTemplate(htmlTemplete).make(binding)
        return template.toString()
    }

    private static loadDatabaseScript() {
        def f = new File("${Muddler.workspace}/conf/database.groovy")
        f.getText()
    }

    private static saveDatabaseScript(text) {
        def f = new File("${Muddler.workspace}/conf/database.groovy")
        f.setText(text,"UTF-8")
    }

    private static void refreshDb(groovyString) {

        //ディフォルト
        Muddler.databases = [:]
        Muddler.databases.localDb = {
            Sql.newInstance("jdbc:sqlite:${Muddler.workspace}/muddlerLocal.sqlite3", null, null, "org.sqlite.JDBC")
        }

        //スクリプト
        def databaseCl = {Map params ->
            Muddler.databases[params.name] = {
                Sql.newInstance(params.url, params.user, params.password, params.driverClassName)
            }
            println "add database ${params.name} -> ${params.url}"
        }

        def binding = [
                database:databaseCl,
        ] as Binding
        def shell = new GroovyShell(binding)
        shell.evaluate(groovyString)
    }

}
