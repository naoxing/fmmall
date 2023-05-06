package com.xiaojiang.fmmall.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @Author xiaojiang
 * @Date 2023-04-14 21:29
 * @Description
 **/
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter getExporter(){
        return new ServerEndpointExporter();
    }
}
