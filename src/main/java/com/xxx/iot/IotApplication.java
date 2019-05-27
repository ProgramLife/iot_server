package com.xxx.iot;

import com.xxx.iot.tcp.TcpServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class IotApplication implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    @Value("${httpserver.port}")
    private int port;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(IotApplication.class, args);
        TcpServer tcpServer = context.getBean(TcpServer.class);
        tcpServer.startTcp();
    }

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        factory.setPort(port);
    }
}
