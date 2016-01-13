package com.grachro.muddler

public class TableRecord extends HashMap<String, Object> {

	public static class TableEnptyRecord extends TableRecord {
	}

	@Override
	public Object get(Object key) {

		Object o = super.get(key);
		if (o != null) {
			return o;
		}

		if (key instanceof String) {
			String u = ((String) key).toUpperCase();
			return super.get(u);
		}

		return null;
	}

	public String insertSqlForSqliet3(String tableName, List<String> fieldNames) {
		def result = """
			insert into ${tableName} (${fieldNames.join(',')})
			values (
		"""

		boolean first = true;
		for (String fieldName : fieldNames) {
			if (first) {
				first = false;
			} else {
				result += ","
			}

			Object value = this.get(fieldName);
			if (value instanceof Integer) {
				result += this.get(fieldName)
			} else if (value instanceof Double) {
				result += this.get(fieldName)
			} else if (value instanceof Float) {
				result += this.get(fieldName)
			} else if (value instanceof BigDecimal) {
				result += this.get(fieldName)
			} else if (value instanceof String) {
				String s = value.replaceAll("'", "''");
				result += "'${s}'"
			} else {
				result += "'${this.get(fieldName)}'"
			}

		}
		result += ")"

		return result
	}

}