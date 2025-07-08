package user.notify.websocket;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class NotifyWebSocketHandler extends TextWebSocketHandler {
	private static final Logger logger = LogManager.getLogger(NotifyWebSocketHandler.class);
	/*private Set<WebSocketSession> connectedSessionSet = Collections.synchronizedSet(new HashSet<>());*/

	// 用來保存每個 memberId 對應的 WebSocketSession
	private ConcurrentHashMap<Integer, WebSocketSession> sessionMap = new ConcurrentHashMap<>(); /*Collections.synchronizedMap(new HashMap<>());*/

	// 用來存儲待發送的通知消息
    private final BlockingQueue<TextMessage> messageQueue = new LinkedBlockingQueue<>();
    private WebSocketSession currentSession;
	
    
    private boolean isSending = false;
    
	/* private Map<Integer, WebSocketSession> sessionMap = new HashMap<>(); */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		/* System.out.println(session); */
		String memberIdParam = session.getUri().getQuery(); // 假設 memberId 是從查詢參數中獲取的
		Integer memberId = Integer.parseInt(memberIdParam.split("=")[1]); // 例如 /ws?memberId=123
		/*
		 * System.out.println(memberIdParam); System.out.println(memberId);
		 */
		sessionMap.put(memberId, session);
		
		logger.info("sessionMap contents: " + sessionMap);  // 打印 sessionMap
		logger.info("New connection established: " + memberId);
		
		currentSession = session;
		// 啟動一個線程來處理消息隊列
		new Thread(this::processMessageQueue).start();
	}
	
	
	// 處理隊列中的消息
    private void processMessageQueue() {
        try {
        	 while (true) {
                 TextMessage message = messageQueue.take();  // 阻塞直到有消息
                 synchronized (this) {
                     if (!isSending) {
                         isSending = true;
                         sendMessageToClient(message);  // 發送消息
                     }
                 }
             }
            
        } catch (InterruptedException e) {
            logger.error("Message processing interrupted", e);
        }
    }

    // 實際發送消息
    private void sendMessageToClient(TextMessage message) {
        try {
        	if (currentSession != null && currentSession.isOpen()) {
                currentSession.sendMessage(message);
                logger.info("Message sent: " + message.getPayload());
                // 完成消息發送後，標記為未發送
                synchronized (this) {
                    isSending = false;
                }

            } else {
                logger.warn("Session is not open, unable to send message.");
            }
        } catch (Exception e) {
            logger.error("Error sending message", e);
        }
    }

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

	}

	// 根據 memberId 發送通知
	public void sendNotificationToMember(Integer memberId, String notificationMessage) {
		logger.info("sessionMap contents: " + sessionMap);  // 打印 sessionMap
		WebSocketSession session = sessionMap.get(memberId);
		
		if (session != null && session.isOpen()) {
			/*try {
				session.sendMessage(new TextMessage(notificationMessage)); // 發送通知消息給特定的 memberId
			} catch (Exception e) {
				logger.error("Error sending message to member " + memberId, e);
			}*/
			TextMessage message = new TextMessage(notificationMessage);
            try {
                messageQueue.put(message);  // 把消息放入隊列中
                logger.info("Message added to queue for memberId: " + memberId);
            } catch (InterruptedException e) {
                logger.error("Failed to add message to queue", e);
            }
		} else {
			logger.warn("Session for member " + memberId + " is not available.");
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// 假設 memberId 存儲在 session 的 URI 查詢參數中
		String memberIdParam = session.getUri().getQuery();
		Integer memberId = Integer.parseInt(memberIdParam.split("=")[1]);

		// 移除該 memberId 的會話
		sessionMap.remove(memberId);
		logger.info("Connection closed: " + memberId);
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

		logger.error(exception.getMessage(), exception);
		// 假設 memberId 存儲在 session 的 URI 查詢參數中
		String memberIdParam = session.getUri().getQuery();
		Integer memberId = Integer.parseInt(memberIdParam.split("=")[1]);

		sessionMap.remove(memberId); // 移除錯誤會話
	}
}

