package ru.nurmukhametovalexey.crocodile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
public class CrocodileApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrocodileApplication.class, args);
    }

}
