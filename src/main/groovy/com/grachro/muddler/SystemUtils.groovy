package com.grachro.muddler

import spark.Request

class SystemUtils {
    static String loadResourceFile(String filePath) {
        def input = SystemDatabaseEdit.class.getClassLoader().getResourceAsStream(filePath)
        input.getText("UTF-8")
    }

    static String trimQueryPath(Request request, String queryParam) {
        String s = request.queryParams("scriptPath")
        if (s == null){
            return null
        }
        return s.trim()
    }
}
