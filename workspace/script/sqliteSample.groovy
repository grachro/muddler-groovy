import groovy.sql.Sql
import com.grachro.muddler.Table

def db = Sql.newInstance("jdbc:sqlite:workspace/sample.sqlite3", null, null, "org.sqlite.JDBC")

def tbl = Table.newInstance()
tbl.load(db, """
        select title,url
        from pages
        order by title
    """)

def viewUtil = new com.grachro.muddler.MuddlerViewUtils()


"""
${viewUtil.tableToHtml(tbl)}
"""

