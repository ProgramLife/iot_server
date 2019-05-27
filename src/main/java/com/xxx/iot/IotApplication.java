package com.xxx.iot;

import com.xxx.iot.tcp.TcpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class IotApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(IotApplication.class, args);
        TcpServer tcpServer = context.getBean(TcpServer.class);
        tcpServer.startTcp();
    }

}
