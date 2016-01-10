import groovy.sql.Sql

def sql = Sql.newInstance("jdbc:sqlite:script/sample.sqlite3", null, null, "org.sqlite.JDBC")

def pages = [:]
sql.eachRow("""
        select title,url
        from pages
        order by title
    """) { row ->
        pages[row.title] = row.url
    }

"""
${pages}
"""