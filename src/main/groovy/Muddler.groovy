import static spark.Spark.get

/**
 * Created by grachro on 2016/01/10.
 */
class Muddler {
    public static void main(String[] args) {
        get "/hello", {req, res ->

            def f = new File("script/SomethingString.groovy")
            def groovyString = f.getText()

            def binding = [
                    b1:"foo",
                    b2:["bar","baz",],
            ] as Binding
            def shell = new GroovyShell(binding)
            def result = shell.evaluate(groovyString)


        }
    }
}
