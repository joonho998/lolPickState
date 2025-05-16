package com.example.lolTest.handler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class DraftHandler extends TextWebSocketHandler
{

    // 클라이언트 세션을 저장할 Map
    //private final Map<String, WebSocketSession> sessions          = new ConcurrentHashMap<>();
    //private final Set<String>                   selectedChampions = new HashSet<>();
    // 방 ID → 방에 속한 세션들
    private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();





    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception
    {
        String roomId = getRoomId(session);
        this.roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
        System.out.println("Client connected: " + session.getId() + " (room: " + roomId + ")");
    }





    @Override
    public void handleTextMessage(final WebSocketSession session, final TextMessage message) throws Exception
    {
        String messageContent = message.getPayload();
        String roomId = getRoomId(session);

        System.out.println("Message in room [" + roomId + "]: " + messageContent);

        JSONObject msg = new JSONObject(messageContent);
        // "start" 타입 메시지인 경우: 같은 방의 모든 세션에게 전송
        if ( "start".equals(msg.getString("type")) )
        {
            Set<WebSocketSession> sessionsInRoom = this.roomSessions.get(roomId);
            if ( sessionsInRoom != null )
            {
                for ( WebSocketSession s : sessionsInRoom )
                {
                    if ( s.isOpen() )
                    {
                        s.sendMessage(new TextMessage(msg.toString()));
                    }
                }
            }
            return; // 여기서 더 이상 아래 코드 실행할 필요 없음
        }

        // 같은 방의 다른 세션에게만 메시지 전달
        Set<WebSocketSession> sessionsInRoom = this.roomSessions.get(roomId);
        if ( sessionsInRoom != null )
        {
            for ( WebSocketSession s : sessionsInRoom )
            {
                if ( s.isOpen() && !s.getId().equals(session.getId()) )
                {
                    s.sendMessage(new TextMessage(messageContent));
                }
            }
        }
    }





    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status)
    {
        String roomId = getRoomId(session);
        Set<WebSocketSession> sessionsInRoom = this.roomSessions.get(roomId);

        if ( sessionsInRoom != null )
        {
            sessionsInRoom.remove(session);
            if ( sessionsInRoom.isEmpty() )
            {
                this.roomSessions.remove(roomId); // 방 비었으면 정리
            }
        }

        System.out.println("Client disconnected: " + session.getId() + " (room: " + roomId + ")");
    }





    private String getRoomId(final WebSocketSession session)
    {
        // 쿼리 파라미터에서 roomId 추출
        String uri = session.getUri().toString(); // ex: /ws/draft?roomId=abc
        String[] parts = uri.split("\\?");
        if ( parts.length > 1 )
        {
            String[] queryParams = parts[1].split("&");
            for ( String param : queryParams )
            {
                String[] keyVal = param.split("=");
                if ( keyVal.length == 2 && keyVal[0].equals("roomId") )
                {
                    return keyVal[1];
                }
            }
        }
        return "default"; // 못 찾으면 default 방
    }





    public boolean roomExists(final String roomId)
    {
        return this.roomSessions.containsKey(roomId);
    }





    public void createRoom(final String roomId)
    {
        this.roomSessions.putIfAbsent(roomId, ConcurrentHashMap.newKeySet());
    }

}
