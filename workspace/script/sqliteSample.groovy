def tbl = md.loadTable("sqlite3", """
        select title,url
        from pages
        order by title
    """)

def localDb = md.openLocalDb()


tbl.toSqlite3(localDb,"page_loaded")

viewParams["tbl"] = tbl


viewParams.foo = md.loadScript('Foo.groovy',[a:1, b:2,])

def jointed = md.loadTable("sqlite3", """
            select category_name,title
            from category_page
            order by category_name,title
        """)
        .eachRecodWithAnother(tbl,
            {thisRecord -> thisRecord.title},
            {anotherRecord -> anotherRecord.title},
            {thisRecord,anotherRecord ->
                thisRecord.url = anotherRecord.url
            }
        )
        .setFieldNames(["category_name","title","url"])
viewParams.jointed = jointed


md.loadHtml("sqliteSample.html")
