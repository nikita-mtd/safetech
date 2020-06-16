package ru.safetech.signature.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import ru.safetech.signature.domain.RandomData;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

//@Service
@AllArgsConstructor
public class SignatureService {

    static {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

//  public Flux<RandomData> sing(Flux<RandomData> dataStream) {
//      dataStream
//              .publishOn(Schedulers.newElastic("sign-thread-pool"))
//              .map()
//      return
//  }

  private void ecdsaSign() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
      ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
//      KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "BC");
      KeyPairGenerator g = KeyPairGenerator.getInstance("EC");

      g.initialize(ecSpec, new SecureRandom());
      KeyPair keypair = g.generateKeyPair();
      PublicKey publicKey = keypair.getPublic();
      PrivateKey privateKey = keypair.getPrivate();
  }

    private static final String SPEC = "prime256v1";
    private static final String ALGO = "SHA256withECDSA";

    private Map sender() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, UnsupportedEncodingException, SignatureException, JSONException, NoSuchProviderException {

//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");

        ECParameterSpec spec = ECNamedCurveTable.getParameterSpec("prime256v1");
        keyPairGenerator.initialize(spec, new SecureRandom());
        KeyPair keypair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keypair.getPublic();
        PrivateKey privateKey = keypair.getPrivate();

        String plaintext = "Hello";

        //...... sign
        Signature ecdsaSign = Signature.getInstance(ALGO);
        ecdsaSign.initSign(privateKey);
        ecdsaSign.update(plaintext.getBytes(UTF_8));
        byte[] signature = ecdsaSign.sign();
        String pub = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String sig = Base64.getEncoder().encodeToString(signature);
        String priv = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        System.out.println("signature " + sig);
        System.out.println("public " + pub);
        System.out.println("priv " + priv);

        Map obj = new HashMap();
        obj.put("publicKey", pub);
        obj.put("signature", sig);
        obj.put("message", plaintext);
        obj.put("algorithm", ALGO);

        return obj;
    }

    private boolean receiver(Map obj) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, UnsupportedEncodingException, SignatureException, JSONException {

        Signature ecdsaVerify = Signature.getInstance((String) obj.get("algorithm"));

        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode((String) obj.get("publicKey")));

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(((String) obj.get("message")).getBytes("UTF-8"));
        boolean result = ecdsaVerify.verify(Base64.getDecoder().decode((String) obj.get("signature")));

        return result;
    }

    @SneakyThrows
    public static void main(String[] args) {
            SignatureService SignatureService = new SignatureService();
            var obj = SignatureService.sender();
            boolean result = SignatureService.receiver(obj);
            System.out.println(result);
    }

}
