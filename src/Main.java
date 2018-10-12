
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws URISyntaxException, InterruptedException, IOException {
        HttpServer s = HttpServer.create(new InetSocketAddress(1234),4);
        s.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = "OK!";
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
                Scanner in = new Scanner(exchange.getRequestBody());
                System.out.println(exchange.getRequestURI());
                if(in.hasNext()){
                    System.out.println(in.next());
                }else {
                    System.out.println("nothing");
                }
                exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });
        s.start();
    }
}
