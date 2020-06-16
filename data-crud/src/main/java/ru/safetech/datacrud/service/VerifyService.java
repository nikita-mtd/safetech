package ru.safetech.datacrud.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.safetech.datacrud.domain.RandomData;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class VerifyService {

    private static final String HASH_ALGO = "SHA256withECDSA";

    @Value("{}")
    private String publicKey;

    public final Map<String, Mono<Object>> registeredWaiters = new HashMap<>();

    public Mono<Object> pushAndVerify(Mono<Object> data) {
        data.publishOn(Schedulers.newElastic("verify-thread-pool"));
    }

    @EventListener
    public void signedData() {
        registeredWaiters.get(/**/)
    }

    @SneakyThrows
    private boolean isValidSignature(RandomData data) {
        var verification = Signature.getInstance(HASH_ALGO);
        var spec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
        var keyFactory = KeyFactory.getInstance("EC");
        var publicKey = keyFactory.generatePublic(spec);
        verification.initVerify(publicKey);
        verification.update(data.getMessage());
        return verification.verify(data.getSignature());
    }


}
