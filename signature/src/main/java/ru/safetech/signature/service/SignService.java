package ru.safetech.signature.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import ru.safetech.signature.event.RedisChanged;

import javax.annotation.PostConstruct;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class SignService {

    private static final String SIGN_SPEC = "prime256v1";
    private static final String HASH_ALGO = "SHA256withECDSA";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic = new ChannelTopic("data:signed");

    //  private PublicKey publicKey;
    //  private PrivateKey privateKey;
//    @Value("{}")
    private String encodedPrivateKey = "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgi/30J7mMn2Ngjqob1M6zYt6UO+Gn1NHzToF4LShUo8mgCgYIKoZIzj0DAQehRANCAAQF3PtG2SWmU0EsIaybkOnZUnecRrlF3Wldr2RMsN8gwvXw7XYm6IWn3UQLEZO52psGvF3iGdx5OEsq3prVvmsc";

    private PrivateKey privateKey;

    @SneakyThrows
    @PostConstruct
    public void init() {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
//        var publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(encodedPublicKey));
        var privateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(encodedPrivateKey));
        privateKey = KeyFactory.getInstance("EC").generatePrivate(privateKeySpec);
    }

  /*  @SneakyThrows
  public void initKeysIfNeed() {
    if (privateKey == null) {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
      ECParameterSpec spec = ECNamedCurveTable.getParameterSpec("prime256v1");
      keyPairGenerator.initialize(spec, new SecureRandom());
      KeyPair keypair = keyPairGenerator.generateKeyPair();
      this.publicKey = keypair.getPublic();
      this.privateKey = keypair.getPrivate();
    }
  }*/

    @EventListener
    public void redisChanged(RedisChanged redisChanged) {
        var data = redisChanged.getRandomData();
        var signature = getSignature(data.getMessage());
        data.setSignature(signature);
        redisTemplate.convertAndSend(topic.getTopic(), data);
    }

    @SneakyThrows
    private byte[] getSignature(byte[] message) {
        var signature = Signature.getInstance(HASH_ALGO);
        signature.initSign(privateKey);
        signature.update(message);
        return signature.sign();
    }

    private String getSignatureAsString(byte[] message) {
        return Base64.getEncoder().encodeToString(getSignature(message));
    }

}
