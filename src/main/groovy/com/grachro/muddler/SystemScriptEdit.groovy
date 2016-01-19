package com.grachro.muddler

import groovy.text.GStringTemplateEngine

import static spark.Spark.get
import static spark.Spark.post
import static MuddlerUtils.trimQueryPath

public class SystemScriptEdit {


    public static init() {
        //スクリプト一覧画面 表示
        get "/system/scriptList", { req, res ->
            def htmlTemplete = MuddlerUtils.loadResourceFileText("scriptList.html")
            def binding = [
                    scriptList:loadAllScripitNames(),
                    message:null,
            ]

            def template = new GStringTemplateEngine().createTemplate(htmlTemplete).make(binding)
            return template.toString()

        }

        post "/system/scriptShow", { req, res ->
            def scriptPath = trimQueryPath(req,"scriptPath")
            def scriptText = loadScriptText(scriptPath)
            getEditHtml(scriptPath,scriptText,null)
        }

        post "/system/scriptSave", { req, res ->
            def scriptPath = trimQueryPath(req,"scriptPath")
            def scriptText = req.queryParams("scriptText")

            println scriptText

            try {
                saveScript scriptPath, scriptText
                getEditHtml(scriptPath, scriptText, "success.")
            } catch (e) {
                e.printStackTrace()
                getEditHtml(scriptPath, scriptText, e.getStackTrace())
            }
        }

        post "/system/scriptAdd", { req, res ->
            def scriptPath = trimQueryPath(req,"scriptPath")
            def scriptText = ""
            try {
                saveScript scriptPath, scriptText
                getEditHtml(scriptPath, scriptText, "success.")
            } catch (e) {
                e.printStackTrace()
                getEditHtml(scriptPath, scriptText, e.getStackTrace())
            }
        }

        post "/system/scriptDelete", { req, res ->
            def scriptPath = trimQueryPath(req,"scriptPath")
            def binding = [:]

            try {
                deleteScript scriptPath
                binding.message = null
            } catch (e) {
                e.printStackTrace()
                binding.message = e.getStackTrace()
            }
            binding.scriptList = loadAllScripitNames()

            def htmlTemplete = MuddlerUtils.loadResourceFileText("scriptList.html")
            def template = new GStringTemplateEngine().createTemplate(htmlTemplete).make(binding)
            return template.toString()
        }
    }

    private static loadScriptText(scriptPath) {
        def f = new File("${Muddler.scriptRoot}/${scriptPath}.groovy")
        f.getText()
    }

    private static saveScript(scriptPath, scriptText) {
        def f = new File("${Muddler.scriptRoot}/${scriptPath}.groovy")
        def dir = f.getParentFile()
        if (!dir.exists()) {
            dir.mkdirs()
        }
        f.setText(scriptText,"UTF-8")
    }

    private static deleteScript(scriptPath) {
        def f = new File("${Muddler.scriptRoot}/${scriptPath}.groovy")
        f.delete()
    }

    static String[] loadAllScripitNames() {
        def names = []

        def prefixLength = Muddler.scriptRoot.length() + 1
        def suffixLength = ".groovy".length()

        new File(Muddler.scriptRoot).eachFileRecurse(groovy.io.FileType.FILES) {
            if (it.name =~ /\.groovy$/) {
                names += it.path.substring(prefixLength,it.path.length() - suffixLength)
            }
        }
        return names
    }

    private static String getEditHtml(scriptPath, scriptText, message) {
        def htmlTemplete = MuddlerUtils.loadResourceFileText("scriptEdit.html")
        def binding = [
                scriptPath:scriptPath,
                scriptText:scriptText,
                message:message,
        ]

        def template = new GStringTemplateEngine().createTemplate(htmlTemplete).make(binding)
        return template.toString()
    }
}
