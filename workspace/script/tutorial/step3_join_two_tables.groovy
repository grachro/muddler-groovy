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

//3. find cocktails recipe list.
cocktails.eachRecodWithAnother( recipes,
    {cocktail -> cocktail.COCKTAIL_NAME},
    {recipe -> recipe.COCKTAIL_NAME},
    {cocktail, findedRecipes ->
        def ingredients = findedRecipes.inject([]){ list , recipe -> list += recipe.INGREDIENT }
        cocktail.INGREDIENT = ingredients
    }
)

//4. Reset display Fields
cocktails.setFieldNames(["COCKTAIL_NAME","INGREDIENT"])

//5. Show result
showTable(cocktails)