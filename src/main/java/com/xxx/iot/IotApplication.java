package com.xxx.iot;

import com.xxx.iot.server.ServerFrame;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class IotApplication implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    @Value("${httpserver.port}")
    private int port;

    public static void main(String[] args) {
        /* 启用测试界面的话，注解掉
        ConfigurableApplicationContext context = SpringApplication.run(IotApplication.class, args);
        TcpServer tcpServer = context.getBean(TcpServer.class);
        tcpServer.startTcp();
        */
        SpringApplicationBuilder builder = new SpringApplicationBuilder(IotApplication.class);
        ApplicationContext context = builder.headless(false).run(args);
        ServerFrame swing = context.getBean(ServerFrame.class);
        swing.launchFrame();
    }

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        factory.setPort(port);
    }
}
