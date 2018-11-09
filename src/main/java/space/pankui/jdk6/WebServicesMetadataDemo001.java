package space.pankui.jdk6;

//import javax.jws.WebMethod;
//import javax.jws.WebService;
//import javax.xml.ws.Endpoint;

/**
 * @author pankui
 * @date 30/06/2018
 * <pre>
 *
 * </pre>
 */
//@WebService
public class WebServicesMetadataDemo001 {

/*    *//**
     * sayHello.
     * @param name 名称
     * @return 结果
     *//*
    @WebMethod
    public String sayHello(String name) {
        return "Hello ".concat(name);
    }

    *//**
     * @param args
     * 浏览器请求 http://localhost:8080/helloService?wsdl
     * 注意 问好后面一定是wsdl
     *//*
    public static void main(String[] args) {
        WebServicesMetadataDemo001 hello = new WebServicesMetadataDemo001();
        Endpoint endPoint = Endpoint.publish("http://localhost:8080/helloService", hello);
        System.out.println("调用成功!");
    }*/

}
