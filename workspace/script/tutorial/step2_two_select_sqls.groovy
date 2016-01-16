//1. Query Sql
def tbl1 = loadTable "sqlite3", """
  select * 
  from COCKTAIL
  order by COCKTAIL_NAME
"""

//2. Query Another Sql
def tbl2 = loadTable "sqlite3", """
  select * 
  from RECIPE
  order by COCKTAIL_NAME,RECIPE_INDEX
"""


//3. Show result
showTable(tbl1,tbl2)