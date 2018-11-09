package space.pankui.jdk6;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
/**
 * @author pankui
 * @date 30/06/2018
 * <pre>
 *
 * </pre>
 */
public class MyHttpServer {

    /**
     * 启动服务，监听来自客户端的请求.
     * 浏览器请求 http://127.0.0.1:8888/mytest
     * @throws IOException IO异常
     */
    private static void httpServerService() throws IOException {
        HttpServerProvider provider = HttpServerProvider.provider();
        // 监听端口8888,能同时接受100个请求
        HttpServer httpserver = provider.createHttpServer(new InetSocketAddress(8888), 200);
        httpserver.createContext("/mytest", new MyHttpHandler());
        httpserver.setExecutor(null);
        httpserver.start();
        System.out.println("server started");
    }

    /**
     * Http请求处理类.
     */
    private static class MyHttpHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            //响应信息
            String responseMsg = "ok";
            //获得输入流
            InputStream in = httpExchange.getRequestBody();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String temp = null;
            while((temp = reader.readLine()) != null) {
                System.out.println("client request:" + temp);
            }
            //设置响应头属性及响应信息的长度
            httpExchange.sendResponseHeaders(200, responseMsg.length());
            //获得输出流
            OutputStream out = httpExchange.getResponseBody();
            out.write(responseMsg.getBytes());
            out.flush();
            httpExchange.close();
        }

    }

    public static void main(String[] args) throws IOException {
        httpServerService();
    }

}
