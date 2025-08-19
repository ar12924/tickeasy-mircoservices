package microservices.notify.websocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class NotifyWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LogManager.getLogger(NotifyWebSocketHandler.class);
    private ConcurrentHashMap<Integer, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private final BlockingQueue<TextMessage> messageQueue = new LinkedBlockingQueue<>();
    private WebSocketSession currentSession;
    private boolean isSending = false;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String memberIdParam = session.getUri().getQuery();
        Integer memberId = Integer.parseInt(memberIdParam.split("=")[1]);
        sessionMap.put(memberId, session);
        currentSession = session;
        new Thread(this::processMessageQueue).start();
    }

    private void processMessageQueue() {
        try {
            while (true) {
                TextMessage message = messageQueue.take();
                synchronized (this) {
                    if (!isSending) {
                        isSending = true;
                        sendMessageToClient(message);
                    }
                }
            }
        } catch (InterruptedException e) {
            logger.error("Message processing interrupted", e);
        }
    }

    private void sendMessageToClient(TextMessage message) {
        try {
            if (currentSession != null && currentSession.isOpen()) {
                currentSession.sendMessage(message);
                synchronized (this) { isSending = false; }
            }
        } catch (Exception e) {
            logger.error("Error sending message", e);
        }
    }

    public void sendNotificationToMember(Integer memberId, String notificationMessage) {
        WebSocketSession session = sessionMap.get(memberId);
        if (session != null && session.isOpen()) {
            try { messageQueue.put(new TextMessage(notificationMessage)); } catch (InterruptedException ignored) {}
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String memberIdParam = session.getUri().getQuery();
        Integer memberId = Integer.parseInt(memberIdParam.split("=")[1]);
        sessionMap.remove(memberId);
    }
}



