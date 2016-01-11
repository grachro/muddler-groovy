def tbl = muddler.loadTable("sqlite3", """
        select title,url
        from pages
        order by title
    """)

"""
${muddler.tableToHtml(tbl)}
"""

