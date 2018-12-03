package space.pankui.javascript;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;

/**
 * @author pankui
 * @date 2018/11/29
 * <pre>
 *
 * </pre>
 */
public class RunJavaScriptDemo {

    public static void main(String[] args) throws NoSuchMethodException, ScriptException, IOException {
        runJavaScript();
    }
    public static void runJavaScript() throws ScriptException, IOException, NoSuchMethodException {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine scriptEngine = factory.getEngineByName("JavaScript");
        scriptEngine.eval("var x = 10;");
        scriptEngine.eval("var y = 20;");
        scriptEngine.eval("var z = x + y;");
        scriptEngine.eval("print (z);");


        var result = scriptEngine.get("x");
        System.out.println(result);

    }
}
