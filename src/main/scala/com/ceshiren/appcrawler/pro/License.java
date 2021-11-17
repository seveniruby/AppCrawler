package com.ceshiren.appcrawler.pro;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class License {

    private final static int KEY_SIZE = 1024;

    public static Map<String, String> genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器
        keyPairGen.initialize(KEY_SIZE, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        // 得到私钥字符串
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        // 将公钥和私钥保存到Map
        //0表示公钥
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("public", publicKeyString);
        //1表示私钥
        keyMap.put("private", privateKeyString);
        return keyMap;
    }

    public static Key getKey(String keyBase64, Boolean isPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Key key;
        byte[] decoded = Base64.getDecoder().decode(keyBase64);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        if (isPublicKey) {
            key = keyFactory.generatePublic(new X509EncodedKeySpec(decoded));
        } else {
            key = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decoded));
        }
        return key;
    }

    public static String encrypt(String message, String keyBase64, Boolean isPublicKey) throws Exception {

        Key key = getKey(keyBase64, isPublicKey);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        String outStr = Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes("UTF-8")));
        return outStr;
    }

    public static String decrypt(String messageEncrypt, String keyBase64, Boolean isPublicKey) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.getDecoder().decode(messageEncrypt);
        Key key = getKey(keyBase64, isPublicKey);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);

        String message = new String(cipher.doFinal(inputByte));
        return message;
    }

    public static String sign(String message, String privateKey) throws Exception {

        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(priKey);
        signature.update(message.getBytes(StandardCharsets.UTF_8));
        byte[] digitalSignature = signature.sign();
        return Base64.getEncoder().encodeToString(digitalSignature);
    }

    public static boolean verify(String message, String signed, String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {

        byte[] decoded = Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(pubKey);

        byte[] signedDecoded = Base64.getDecoder().decode(signed);
        signature.update(message.getBytes(StandardCharsets.UTF_8));
        return signature.verify(signedDecoded);
    }

}
