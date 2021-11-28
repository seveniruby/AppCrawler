package com.ceshiren.appcrawler.pro;

import com.ceshiren.appcrawler.utils.Log;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LicenseTest {

    @Test
    void decrypt() throws Exception {
        Map<String, String> keyMap = License.genKeyPair();
        String publicKey=keyMap.get("public");
        String privateKey = keyMap.get("private");
        Log.log.info(publicKey);
        Log.log.info(privateKey);

        String message = "2021-12-17";
        String str=License.encrypt(message, publicKey, true);
        Log.log.info(str);
        String raw=License.decrypt(str, privateKey, false);
        Log.log.info(raw);
        assertEquals(raw, message);

        str=License.encrypt(message, privateKey, false);
        Log.log.info(str);
        raw=License.decrypt(str, publicKey, true);
        Log.log.info(raw);
        assertEquals(raw, message);
    }

    @Test
    void verify() throws Exception {
//        String strSigned="m1Lnz7TDPI1NNXCcyAwDzIDdjX/PF0dkmyE7G2fnimY9aJyraTnHHj2C0025pkIVTyxPtZmH+Rz2xSrKSUcAXZ8uWRFsIPf6PT8fChhqfT8P+6WT+oOnRkjCAgL3cO8iWRb7NX6HJenRxgeyAXKVX6pYj1vlYFoVR2DmdnTg4xM=";
//        String pubKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCpQKjyHjvutMOsGhHvTeahzb2dkIvsy2NdFZHpkUCwMicB49ie4W9/E+cQ/ExWhNjasyugCVsUKIm+FlWN+QnmPHBr6R4lHCqzWilTnbhFvS8Km97zCJTAMe7ah2/IPdz9ds54g6IIHxoWQlk3FSk+AD8W3YozkTKWqOGHFR58cwIDAQAB";
//        String privateKey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKlAqPIeO+60w6waEe9N5qHNvZ2Qi+zLY10VkemRQLAyJwHj2J7hb38T5xD8TFaE2NqzK6AJWxQoib4WVY35CeY8cGvpHiUcKrNaKVOduEW9Lwqb3vMIlMAx7tqHb8g93P12zniDoggfGhZCWTcVKT4APxbdijORMpao4YcVHnxzAgMBAAECgYBCeuFhaLrFwj7xhLPyuTiT6YpHL5WmhyUaVPShN6qfCrQDrAlULtxqn9is9UzO1xOOo73I+KPLwTiJb6BfSai3IRMRur1qDI32BQYhsE3IGsX5OkXM8UhE3HfC/z8b54tkJRHY4T5KklHgu5n8dxPKD4mCMrDRUMuYD+CY+w/ZcQJBANHEItKsHxttWaKIXlx21CD9Nbk1vX295z1aFhzF6HSqsAO3s+6jVM3zJDMUI8diUAiUpognr5Ah4Kabf+LQvU8CQQDOjpUMWZdRMXsF0ES6zoRmfePCqwv7ZxsZ41EgRm8qAazlQXi2QjpNWUadpyF+lXPfFh/HQ1QdGcWq1PyPFK2dAkEAil1Czw4D8tZ9Yo1rGLi1EhGTx1hgZrnF9x0eKtqMi7AvLDSXdli8TqEMBrlJJHJ/8jkDZBoxXvRJt/woLhecVwJBALE+3p1rX5JXoFZ7wg5+2lRMoJ3SQwTnE/Vh/6JvbkKgj2lmhhv3tqna+uKboP1LZ9O922UU3F/gVqNiApsQxm0CQDrFXgC+UTND5ULBXYhcqTFaUDPRN1E3IK0k++TsoQ+a44RlXYcSxbNmZyPjlAPCdP/W9BIeqiclJIFCFpxPUQ0=";
        String message="123";
        Map<String, String> map = License.genKeyPair();
        String pubKey=map.get("public");
        String privateKey=map.get("private");

        String strSigned=License.sign(message, privateKey);
        Log.log.info(strSigned);
        assertTrue(License.verify(message, strSigned, pubKey));
    }

    @Test
    void encryptWithLocal() throws Exception {
        java.lang.String path="/Users/seveniruby/WeDrive/测吧(北京)科技有限公司/项目管理/东软自动遍历测试合作/证书/appcrawler_private_v8.pem";
        String content=new String(Files.readAllBytes(Paths.get(path)));
        String[] lines = content.split("\n");
        String key=String.join("", Arrays.copyOfRange(lines, 1, lines.length-1));
        String message="2021-12-17";
        String messageEncrypt=License.encrypt(message, key, false);
        Log.log.info(messageEncrypt);
    }

    @Test
    void decryptWithLocal() throws Exception {
        String path="/Users/seveniruby/WeDrive/测吧(北京)科技有限公司/项目管理/东软自动遍历测试合作/证书/appcrawler_public.pem";
        String privateKey= new String(Files.readAllBytes(Paths.get(path)));
        String message="2021-12-17";
        String messageEncrypt=License.encrypt(message, privateKey, false);
        Log.log.info(messageEncrypt);
    }

    @Test
    void sign2() throws Exception {
        Signature signature = Signature.getInstance("SHA256WithDSA");
        SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        signature.initSign(keyPair.getPrivate(), secureRandom);
        byte[] data = "abcdefghijklmnopqrstuvxyz".getBytes("UTF-8");
        signature.update(data);

        byte[] digitalSignature = signature.sign();

        Signature signature2 = Signature.getInstance("SHA256WithDSA");

        signature2.initVerify(keyPair.getPublic());

        byte[] data2 = "abcdefghijklmnopqrstuvxyz".getBytes("UTF-8");
        signature2.update(data2);

        boolean verified = signature2.verify(digitalSignature);
        assertTrue(verified);

    }

}