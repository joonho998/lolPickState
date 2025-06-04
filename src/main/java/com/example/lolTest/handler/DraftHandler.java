package com.example.lolTest.handler;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
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
    // private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    // private final Set<String> selectedChampions = new HashSet<>();
    // 방 ID → 방에 속한 세션들
    private final Map<String, Set<WebSocketSession>> roomSessions    = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, String>      userTeamMap     = new ConcurrentHashMap<>();
    private WebSocketSession                         blueSession     = null;
    private WebSocketSession                         redSession      = null;
    private final List<String>                       banOrder        = Arrays.asList("blue",
                                                                                     "red",
                                                                                     "blue",
                                                                                     "red",
                                                                                     "blue",
                                                                                     "red",
                                                                                     "blue",
                                                                                     "red",
                                                                                     "red",
                                                                                     "blue",
                                                                                     "blue",
                                                                                     "red",
                                                                                     "red",
                                                                                     "blue",
                                                                                     "red",
                                                                                     "blue",
                                                                                     "red",
                                                                                     "blue",
                                                                                     "blue",
                                                                                     "red",
                                                                                     "red");
    private int                                      currentBanIndex = 0;
    private boolean                                  start           = false;

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception
    {
        String team = getQueryParam(session, "team");
        if ( "blue".equals(team) )
        {
            this.blueSession = session;
            this.userTeamMap.put(session, "blue");
            session.sendMessage(new TextMessage("{\"type\": \"team\", \"team\": \"blue\"}"));
        }
        else if ( "red".equals(team) )
        {
            this.redSession = session;
            this.userTeamMap.put(session, "red");
            session.sendMessage(new TextMessage("{\"type\": \"team\", \"team\": \"red\"}"));
        }
        else
        {
            // 👀 관전자 처리
            this.userTeamMap.put(session, "observer");
            session.sendMessage(new TextMessage("{\"type\": \"team\", \"team\": \"observer\"}"));
        }
        String roomId = getQueryParam(session, "roomId");
        this.roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet())
                         .add(session);
        System.out.println("Client connected: " + session.getId() + " (room: " + roomId + ")");
    }





    @Override
    public void handleTextMessage(final WebSocketSession session, final TextMessage message) throws Exception
    {
        String messageContent = message.getPayload();
        String roomId = getQueryParam(session, "roomId");
        String team = getQueryParam(session, "team");

        JSONObject msg = new JSONObject(messageContent);
        // "start" 타입 메시지인 경우: 같은 방의 모든 세션에게 전송
        if ( "start".equals(msg.getString("type")) )
        {
            msg.put("banOrder", this.banOrder.get(this.currentBanIndex));
            this.currentBanIndex++;
            this.start = true;
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
        if ( sessionsInRoom != null && this.start )
        {
            msg.put("banOrder", this.banOrder.get(this.currentBanIndex));
            this.currentBanIndex++;
            for ( WebSocketSession s : sessionsInRoom )
            {
                // if ( s.isOpen() && !s.getId()
                // .equals(session.getId()) )
                // {
                System.out.println(new TextMessage(msg.toString()));
                s.sendMessage(new TextMessage(msg.toString()));
                // }
            }
        }
    }





    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status)
    {
        String roomId = getQueryParam(session, "roomId");
        String team = getQueryParam(session, "team");
        Set<WebSocketSession> sessionsInRoom = this.roomSessions.get(roomId);
        if ( sessionsInRoom != null )
        {
            if ( sessionsInRoom.isEmpty() )
            {
                this.redSession = null;
                this.blueSession = null;
                this.currentBanIndex = 0;
                this.start = false;
                this.roomSessions.remove(roomId); // 방 비었으면 정리
            }
        }

        System.out.println("Client disconnected: " + session.getId() + " (room: " + roomId + ")");
    }





    private String getQueryParam(final WebSocketSession session, final String key)
    {
        // 쿼리 파라미터에서 roomId 추출
        // String uri = session.getUri()
        // .toString(); // ex: /ws/draft?roomId=abc
        // String[] parts = uri.split("\\?");
        // String[] queryParams = parts[1].split("&");
        // for ( String param : queryParams )
        // {
        // String[] keyVal = param.split("=");
        // System.out.println(keyVal[0] + "=" + keyVal[1]);
        // if ( keyVal.length == 2 && keyVal[0].equals("roomId") )
        // {
        // return keyVal[1];
        // }
        // }
        URI uri = session.getUri();
        String query = uri.getQuery();
        for ( String param : query.split("&") )
        {
            String[] pair = param.split("=");
            if ( pair.length == 2 && pair[0].equals(key) )
            {
                return pair[1];
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
