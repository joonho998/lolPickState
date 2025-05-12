package com.example.lolTest.handler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class DraftHandler extends TextWebSocketHandler
{

    // 클라이언트 세션을 저장할 Map
    private final Map<String, WebSocketSession> sessions          = new ConcurrentHashMap<>();
    private final Set<String>                   selectedChampions = new HashSet<>();





    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception
    {
        String sessionId = session.getId(); // 세션 아이디 (각 클라이언트마다 유니크)
        this.sessions.put(sessionId, session); // 세션을 저장
        System.out.println("Client connected: " + session.getId());
    }





    @Override
    public void handleTextMessage(final WebSocketSession session, final TextMessage message) throws Exception
    {
        String messageContent = message.getPayload();
        System.out.println("Message content: " + message.getPayload());
        // 받은 메시지를 모든 다른 클라이언트에게 전송
        for ( Map.Entry<String, WebSocketSession> entry : this.sessions.entrySet() )
        {
            WebSocketSession otherSession = entry.getValue();
            if ( !otherSession.getId().equals(session.getId()) )
            {
                // 나와 다른 세션에게 메시지 전달
                otherSession.sendMessage(new TextMessage(messageContent));
            }
        }
    }





    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status)
    {
        this.sessions.remove(session.getId());
        System.out.println("Client disconnected: " + session.getId());
    }

}
