package com.farm.dolores.farmacia.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class WebSocketEventInterceptor implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(WebSocketEventInterceptor.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null) {
            StompCommand command = accessor.getCommand();
            
            if (command != null) {
                switch (command) {
                    case CONNECT:
                        log.info("🔌 CONNECT recibido - Session: {}", accessor.getSessionId());
                        log.info("   Headers: {}", accessor.toNativeHeaderMap());
                        break;
                        
                    case CONNECTED:
                        log.info("✅ CONNECTED - Session: {}", accessor.getSessionId());
                        break;
                        
                    case SUBSCRIBE:
                        log.info("📡 SUBSCRIBE - Destination: {}, Session: {}", 
                                accessor.getDestination(), accessor.getSessionId());
                        break;
                        
                    case UNSUBSCRIBE:
                        log.info("📡 UNSUBSCRIBE - Subscription: {}, Session: {}", 
                                accessor.getSubscriptionId(), accessor.getSessionId());
                        break;
                        
                    case SEND:
                        log.info("📨 SEND recibido - Destination: {}, Session: {}", 
                                accessor.getDestination(), accessor.getSessionId());
                        log.info("   Payload: {}", new String((byte[]) message.getPayload()));
                        break;
                        
                    case MESSAGE:
                        log.info("📬 MESSAGE - Destination: {}", accessor.getDestination());
                        break;
                        
                    case DISCONNECT:
                        log.info("🔌 DISCONNECT - Session: {}", accessor.getSessionId());
                        break;
                        
                    default:
                        log.debug("📋 Comando STOMP: {} - Session: {}", command, accessor.getSessionId());
                }
            }
        }
        
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        if (!sent) {
            log.warn("⚠️ Mensaje NO enviado correctamente");
        }
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        if (ex != null) {
            log.error("❌ Error al enviar mensaje: {}", ex.getMessage(), ex);
        }
    }
}
