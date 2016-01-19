package com.grachro.muddler

import spark.Request

class MuddlerUtils {

    static InputStream loadResourceFile(String filePath) {
        def input = MuddlerUtils.class.getClassLoader().getResourceAsStream(filePath)
    }

    static String loadResourceFileText(String filePath) {
        def input = MuddlerUtils.class.getClassLoader().getResourceAsStream(filePath)
        try {
            input.getText("UTF-8")
        } finally {
            input.close()
        }
    }

    static String trimQueryPath(Request request, String queryParam) {
        String s = request.queryParams("scriptPath")
        if (s == null){
            return null
        }
        return s.trim()
    }
}
