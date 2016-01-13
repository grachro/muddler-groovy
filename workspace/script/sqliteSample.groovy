def tbl = md.loadTable("sqlite3", """
        select title,url
        from pages
        order by title
    """)

def localDb = md.openLocalDb()


tbl.toSqlite3(localDb,"page_loaded")

viewParams["tbl"] = tbl


viewParams.foo = md.loadScript('Foo.groovy',[a:1, b:2,])

md.loadHtml("sqliteSample.html")
