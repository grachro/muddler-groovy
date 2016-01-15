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
		"""insert into ${tableName} (${fieldNames.join(',')})
			values (${fieldValueToSqlValues(fieldNames)})
		"""
	}

    private String fieldValueToSqlValues(List<String> fieldNames) {
        def result = ""
        boolean first = true;
        fieldNames.each {fieldName ->
            if (first) {
                first = false;
            } else {
                result += ","
            }
            result += fieldValueToSqlValue(fieldName)
        }
        return result
    }



    private Object fieldValueToSqlValue(String fieldName) {
		Object value = this.get(fieldName);

		if (value == null) {
			return null
		} else if (value instanceof Integer) {
			return value
		} else if (value instanceof Double) {
			return value
		} else if (value instanceof Float) {
			return value
		} else if (value instanceof BigDecimal) {
			return value
		} else if (value instanceof String) {
			String s = value.replaceAll("'", "''");
			return "'${s}'"
		}

        return "'${value}'"

	}

}