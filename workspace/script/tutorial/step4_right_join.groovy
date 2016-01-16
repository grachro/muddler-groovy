//1. Get cocktails
def cocktails = loadTable "sqlite3", """
  select COCKTAIL_NAME, COCKTAIL_TYPE 
  from COCKTAIL
  order by COCKTAIL_NAME
"""

//2. Get recipes
def recipes = loadTable "sqlite3", """
  select COCKTAIL_NAME, INGREDIENT, RECIPE_INDEX
  from RECIPE
  order by COCKTAIL_NAME, RECIPE_INDEX
"""

//3. recipes right join cocktails
recipes.rightJoin( cocktails,
    {cocktailRecord -> cocktailRecord.COCKTAIL_NAME},
    {recipeRecord -> recipeRecord.COCKTAIL_NAME},
    {recipeRecords, cocktailRecord ->
        ingredients = []
        recipeRecords.each{recipeRecord ->
            ingredients += recipeRecord["INGREDIENT"] ?: ""
        }
        cocktailRecord.INGREDIENT = ingredients
    }
)

//4. Reset display Fields
cocktails.setFieldNames(["COCKTAIL_NAME","INGREDIENT"])

//5. Show result
showTable(cocktails)