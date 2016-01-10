import static spark.Spark.get

/**
 * Created by grachro on 2016/01/10.
 */
class Muddler {

    static String SCRIPT_ROOT = "script"

    public static void main(String[] args) {
        get "/:path1", {req, res ->

            def path1 = req.params(":path1")
            def f = new File("${Muddler.SCRIPT_ROOT}/${path1}.groovy")
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
