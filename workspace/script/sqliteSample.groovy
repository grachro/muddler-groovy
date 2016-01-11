def tbl = muddler.loadTable("sqlite3", """
        select title,url
        from pages
        order by title
    """)


viewParams["tbl"] = tbl

muddler.loadHtml("sqliteSample.html", viewParams)
