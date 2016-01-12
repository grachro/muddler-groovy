import groovy.sql.Sql

databases["sqlite3"] = {
    Sql.newInstance("jdbc:sqlite:workspace/sample.sqlite3", null, null, "org.sqlite.JDBC")
}
