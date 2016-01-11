package com.grachro.muddler

class MuddlerViewUtils {

	public String importTemplete(String templete, Map binding) {
		def f = new File("script/${templete}")
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