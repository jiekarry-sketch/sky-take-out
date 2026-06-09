package com.sky.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务，先建立链接，后就可全双工通信
 * 当前只是一个组件，需要一个配置类进行注册
 *
 * 优化点：
 * 1. @OnError 异常处理，防止未捕获异常导致连接泄漏
 * 2. 心跳检测：定时向客户端发送 ping，检测死连接
 * 3. 死连接清理：定时扫描并移除不活跃的会话
 * 4. 发送失败时主动移除失效会话
 */
@Slf4j
@Component
@ServerEndpoint("/ws/{sid}")
public class WebSocketServer {

    /** 存放会话对象 */
    private static final ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    /** 会话最后活跃时间（用于死连接检测） */
    private static final ConcurrentHashMap<String, Long> lastActiveTime = new ConcurrentHashMap<>();

    /** 死连接超时时间：5分钟无活跃则视为死连接 */
    private static final long SESSION_TIMEOUT_MS = 5 * 60 * 1000L;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        log.info("WebSocket客户端建立连接: {}", sid);
        sessionMap.put(sid, session);
        lastActiveTime.put(sid, System.currentTimeMillis());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        log.debug("收到来自客户端 {} 的消息: {}", sid, message);
        lastActiveTime.put(sid, System.currentTimeMillis());
        // 收到客户端 pong 响应时更新活跃时间，不做额外处理
    }

    /**
     * 连接关闭调用的方法
     * @param sid
     */
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        log.info("WebSocket连接断开: {}", sid);
        sessionMap.remove(sid);
        lastActiveTime.remove(sid);
    }

    /**
     * 连接异常调用的方法
     * 捕获未处理的异常，清理资源，防止连接泄漏
     */
    @OnError
    public void onError(Session session, @PathParam("sid") String sid, Throwable error) {
        log.error("WebSocket连接异常, sid={}, error={}", sid, error.getMessage(), error);
        // 异常时主动关闭并清理
        try {
            session.close();
        } catch (IOException e) {
            log.warn("关闭异常会话失败: {}", sid);
        }
        sessionMap.remove(sid);
        lastActiveTime.remove(sid);
    }

    /**
     * 群发消息
     * @param message
     */
    public void sendToAllClient(String message) {
        // 记录发送失败的会话，发送完毕后统一清理
        ConcurrentHashMap.KeySetView<String, Session> sessions = sessionMap.keySet();
        for (String sid : sessions) {
            Session session = sessionMap.get(sid);
            if (session != null && session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                    lastActiveTime.put(sid, System.currentTimeMillis());
                } catch (Exception e) {
                    log.warn("向客户端 {} 发送消息失败，将移除该会话: {}", sid, e.getMessage());
                    // 发送失败，主动移除失效会话
                    sessionMap.remove(sid);
                    lastActiveTime.remove(sid);
                    try {
                        session.close();
                    } catch (IOException ignored) {
                    }
                }
            } else {
                // 会话已关闭，清理
                sessionMap.remove(sid);
                lastActiveTime.remove(sid);
            }
        }
    }

    /**
     * 心跳检测：每30秒向所有客户端发送 ping
     * 客户端收到 ping 后会自动回复 pong（WebSocket 协议层）
     * pong 回复会触发 @OnMessage，从而更新 lastActiveTime
     */
    @Scheduled(fixedRate = 30000)
    public void heartbeat() {
        if (sessionMap.isEmpty()) {
            return;
        }
        for (String sid : sessionMap.keySet()) {
            Session session = sessionMap.get(sid);
            if (session != null && session.isOpen()) {
                try {
                    session.getBasicRemote().sendPing(java.nio.ByteBuffer.wrap("ping".getBytes()));
                } catch (Exception e) {
                    log.warn("心跳 ping 发送失败，移除会话 {}: {}", sid, e.getMessage());
                    sessionMap.remove(sid);
                    lastActiveTime.remove(sid);
                    try {
                        session.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }

    /**
     * 死连接清理：每60秒扫描一次，移除超时未活跃的会话
     * 兜底机制，防止 @OnClose 未被触发的僵尸连接占用资源
     */
    @Scheduled(fixedRate = 60000)
    public void cleanStaleSessions() {
        if (sessionMap.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        for (String sid : sessionMap.keySet()) {
            Long lastActive = lastActiveTime.get(sid);
            if (lastActive != null && (now - lastActive) > SESSION_TIMEOUT_MS) {
                log.info("清理超时 WebSocket 会话: {}, 超时时间: {}ms", sid, now - lastActive);
                Session session = sessionMap.remove(sid);
                lastActiveTime.remove(sid);
                if (session != null && session.isOpen()) {
                    try {
                        session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "会话超时"));
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }

    /**
     * 获取当前在线客户端数量
     */
    public int getOnlineCount() {
        return sessionMap.size();
    }
}
