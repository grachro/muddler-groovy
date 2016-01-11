import com.grachro.muddler.Table

def db = databases["sqlite3"].call()

def tbl = Table.newInstance()
tbl.load(db, """
        select title,url
        from pages
        order by title
    """)

"""
${md.tableToHtml(tbl)}
"""

