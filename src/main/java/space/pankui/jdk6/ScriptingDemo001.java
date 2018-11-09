package space.pankui.jdk6;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author pankui
 * @date 30/06/2018
 * <pre>
 *
 * </pre>
 */
public class ScriptingDemo001 {

    /**
     * main方法.
     * @param args 数组参数
     * @throws ScriptException 脚本异常
     * @throws NoSuchMethodException 无方法异常
     */
    public static void main(String[] args) throws ScriptException, NoSuchMethodException {

        ScriptEngineManager enjineManager = new ScriptEngineManager();
        ScriptEngine engine = enjineManager.getEngineByName("JavaScript");

        String script="function hello(name){return 'Hello ' + name}";
        engine.eval(script);
        Invocable inv=(Invocable) engine;
        // 调用方法及其参数赋值
        String result = (String) inv.invokeFunction("hello", "blinkfox");
        System.out.println("脚本执行结果:"+ result);
    }
}
