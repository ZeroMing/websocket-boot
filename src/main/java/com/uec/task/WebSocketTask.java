package com.uec.task;

import com.uec.websocket.SystemWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class WebSocketTask {

    private static int count = 0;

    @Scheduled(cron="0/10 * * * * ?")
    public void executeAlarmCheckTask() throws IOException {
        SystemWebSocketHandler.sendMsgToAll("哈哈哈哈");
    }
}
