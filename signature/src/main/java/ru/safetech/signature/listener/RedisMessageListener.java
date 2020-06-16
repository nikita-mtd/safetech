package ru.safetech.signature.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import ru.safetech.signature.domain.RandomData;
import ru.safetech.signature.event.RedisChanged;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMessageListener implements MessageListener {

    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper mapper;

    public void onMessage(final Message message, final byte[] pattern) {
        log.info("Message received: " + new String(message.getBody()));
        try {
            var model = mapper.readValue(message.getBody(), RandomData.class);
            eventPublisher.publishEvent(new RedisChanged(model));
        } catch (IOException e) {
            log.error("Error while handling new message from redis", e);
        }
    }
}
