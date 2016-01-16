//1. Edit SQL
def sql = """
  select * 
  from COCKTAIL
  order by COCKTAIL_NAME
"""

//2. Query Sql
def tbl = loadTable "sqlite3", sql

//3. Show result
showTable tbl