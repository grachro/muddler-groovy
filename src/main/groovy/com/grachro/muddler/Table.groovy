package com.grachro.muddler

import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function

public class Table {


	private List<String> fieldNames = new ArrayList<String>();
	private List<TableRecord> records = new ArrayList<TableRecord>();
	private FieldGroups fieldGroups;
	private String query;

	private Map<String, TableRecord> indexMap;

	public Table() {
	}


	public String getQuery() {
		return this.query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Table load(groovy.sql.Sql db, String sql) {

		if (db == null) {
			throw new NullPointerException("db is null");
		}

		if (sql == null) {
			throw new NullPointerException("sql is null");
		}

		println "sql::${sql}"

		this.fieldNames = new ArrayList<String>();
		this.records = new ArrayList<TableRecord>();

		def printColNames = { meta ->
			(1..meta.columnCount).each {
				fieldNames += meta.getColumnLabel(it)
			}
		}

		db.eachRow(sql,printColNames) { row ->

			TableRecord m = new TableRecord();
			for (String fieldName : this.fieldNames) {
				m[fieldName] = row[fieldName]
			}

			this.records.add(m);

		}


		return this;
	}

	public Table setFieldNames(Collection<String> fieldNames) {
		this.fieldNames = new ArrayList<String>(fieldNames);
		return this;
	}

	public List<String> getFieldNames() {
		return Collections.unmodifiableList(this.fieldNames);
	}

	public List<TableRecord> getRecords() {
		return this.records;
	}

	public TableRecord getFirst() {
		return this.records.get(0);
	}

	public Table eachRecord(Consumer<TableRecord> executer) {
		for (TableRecord line : this.records) {
			executer.accept(line);
		}
		return this;
	}

	public Table eachRecordWithIndex(BiConsumer<TableRecord, Integer> executer) {
		int index = 0;
		for (TableRecord line : this.records) {
			executer.accept(line, index++);
		}
		return this;
	}

	public Table addIndex(Function<TableRecord, String> key) {

		if (this.indexMap == null) {
			this.indexMap = new HashMap<String, TableRecord>();
		}
		for (TableRecord line : this.records) {
			String k = key.apply(line);
			this.indexMap.put(k, line);
		}
		return this;
	}

	public void addIndex(TableRecord record, String key) {
		if (this.indexMap == null) {
			this.indexMap = new HashMap<String, TableRecord>();
		}
		this.indexMap.put(key, record);
	}

	public TableRecord seek(String key) {
		if (this.indexMap == null) {
			return new TableRecord.TableEnptyRecord();
		}

		TableRecord line = this.indexMap.get(key);
		if (line == null) {
			return new TableRecord.TableEnptyRecord();
		}
		return line;
	}

	public TableRecord seekOrNull(String key) {
		if (this.indexMap == null) {
			return null;
		}
		return this.indexMap.get(key);
	}

	public TableRecord createNewRecord() {
		TableRecord line = new TableRecord();

		for (String fieldName : this.fieldNames) {
			line.put(fieldName, null);
		}

		this.records.add(line);

		return line;
	}

	public TableRecord createNewRecord(Collection<Object> values) {
		TableRecord line = this.createNewRecord();

		Iterator<Object> itrIterator = values.iterator();

		for (String fieldName : this.fieldNames) {
			line.put(fieldName, itrIterator.next());
		}

		return line;
	}

	public Table sort(Comparator<TableRecord> comparator) {
		this.records.sort(comparator);
		return this;
	}

	public String createTableSqlForSqliet3(String tableName) {
		StringBuilder sb = new StringBuilder();

		sb.append("create table ").append(tableName).append(" (\n");
		boolean first = true;
		for (String fieldName : fieldNames) {
			if (first) {
				first = false;
			} else {
				sb.append(",\n");
			}
			sb.append(fieldName);
		}
		sb.append("\n)");

		return sb.toString();
	}

	public void insertSqlsForSqliet3(String tableName, Closure cl) {
		for (TableRecord line : this.records) {
			String sql = line.insertSqlForSqliet3(tableName, this.fieldNames);
            cl.call(sql);
		}
	}

	public void toSqlite3(groovy.sql.Sql db, String tableName) {
		String dropSql = "drop table if exists ${tableName}"
		db.execute dropSql

		String crateSql = this.createTableSqlForSqliet3(tableName);
        db.execute crateSql

		this.insertSqlsForSqliet3(tableName){insertSql ->

            println "insertSql=${insertSql}"
            db.execute insertSql
        }
	}
//
//	public void mergeSqlite3(String tableName) {
//
//		if (this.em == null) {
//			throw new NullPointerException("em is null");
//		}
//
//		Table t = new Table(rm);
//		t.em = this.em;
//		String existSql = "select count(*) cnt from sqlite_master where type='table' and name='" + tableName + "'";
//		TableRecord r = t.loadFirst(existSql);
//		int cnt = (Integer) r.get("cnt");
//
//		if (cnt == 0) {
//			String crateSql = this.createTableSqlForSqliet3(tableName);
//			this.executeSqlWithTransaction(crateSql);
//		}
//
//		EntityTransaction tx = this.em.getTransaction();
//		tx.begin();
//		this.insertSqlsForSqliet3(tableName, (insertSql) -> {
//			this.executeSql(insertSql);
//		});
//		tx.commit();
//	}

	public FieldGroups getFieldGroups() {
		return fieldGroups;
	}

	public void setFieldGroups(FieldGroups fieldGroups) {
		this.fieldGroups = fieldGroups;
	}

	public Table doSomething(Closure cl) {
        cl.call(this);
		return this;
	}

	public String toTsv() {
		StringBuilder sb = new StringBuilder();
		for (TableRecord record : this.records) {
			boolean first = true;
			for (String fieldName : this.fieldNames) {
				if (first) {
					first = false;
				} else {
					sb.append("\t");
				}
				sb.append(record.get(fieldName));
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
