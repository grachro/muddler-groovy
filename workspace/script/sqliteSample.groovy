def tbl = md.loadTable("sqlite3", """
        select title,url
        from pages
        order by title
    """)


viewParams["tbl"] = tbl


viewParams.foo = md.loadScript('Foo.groovy',[a:1, b:2,])

md.loadHtml("sqliteSample.html")
