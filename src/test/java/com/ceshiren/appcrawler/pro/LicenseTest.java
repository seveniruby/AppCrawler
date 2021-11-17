package com.ceshiren.appcrawler.pro;

import com.ceshiren.appcrawler.utils.Log;
import org.junit.jupiter.api.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LicenseTest {

    @Test
    void decrypt() throws Exception {
        Map<Integer, String> keyMap = null;

        try {
            keyMap = License.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String str=License.encrypt("2021-12-17", keyMap.get(0));
        Log.log.info(str);
        String raw=License.decrypt(str, keyMap.get(1));
        Log.log.info(raw);
        assertEquals(raw, "2021-12-17");
    }

    @Test
    void verify() throws Exception {
//        String strSigned="m1Lnz7TDPI1NNXCcyAwDzIDdjX/PF0dkmyE7G2fnimY9aJyraTnHHj2C0025pkIVTyxPtZmH+Rz2xSrKSUcAXZ8uWRFsIPf6PT8fChhqfT8P+6WT+oOnRkjCAgL3cO8iWRb7NX6HJenRxgeyAXKVX6pYj1vlYFoVR2DmdnTg4xM=";
//        String pubKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCpQKjyHjvutMOsGhHvTeahzb2dkIvsy2NdFZHpkUCwMicB49ie4W9/E+cQ/ExWhNjasyugCVsUKIm+FlWN+QnmPHBr6R4lHCqzWilTnbhFvS8Km97zCJTAMe7ah2/IPdz9ds54g6IIHxoWQlk3FSk+AD8W3YozkTKWqOGHFR58cwIDAQAB";
//        String privateKey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKlAqPIeO+60w6waEe9N5qHNvZ2Qi+zLY10VkemRQLAyJwHj2J7hb38T5xD8TFaE2NqzK6AJWxQoib4WVY35CeY8cGvpHiUcKrNaKVOduEW9Lwqb3vMIlMAx7tqHb8g93P12zniDoggfGhZCWTcVKT4APxbdijORMpao4YcVHnxzAgMBAAECgYBCeuFhaLrFwj7xhLPyuTiT6YpHL5WmhyUaVPShN6qfCrQDrAlULtxqn9is9UzO1xOOo73I+KPLwTiJb6BfSai3IRMRur1qDI32BQYhsE3IGsX5OkXM8UhE3HfC/z8b54tkJRHY4T5KklHgu5n8dxPKD4mCMrDRUMuYD+CY+w/ZcQJBANHEItKsHxttWaKIXlx21CD9Nbk1vX295z1aFhzF6HSqsAO3s+6jVM3zJDMUI8diUAiUpognr5Ah4Kabf+LQvU8CQQDOjpUMWZdRMXsF0ES6zoRmfePCqwv7ZxsZ41EgRm8qAazlQXi2QjpNWUadpyF+lXPfFh/HQ1QdGcWq1PyPFK2dAkEAil1Czw4D8tZ9Yo1rGLi1EhGTx1hgZrnF9x0eKtqMi7AvLDSXdli8TqEMBrlJJHJ/8jkDZBoxXvRJt/woLhecVwJBALE+3p1rX5JXoFZ7wg5+2lRMoJ3SQwTnE/Vh/6JvbkKgj2lmhhv3tqna+uKboP1LZ9O922UU3F/gVqNiApsQxm0CQDrFXgC+UTND5ULBXYhcqTFaUDPRN1E3IK0k++TsoQ+a44RlXYcSxbNmZyPjlAPCdP/W9BIeqiclJIFCFpxPUQ0=";
        String message="123";
        Map<Integer, String> map = License.genKeyPair();
        String pubKey=map.get(0);
        String privateKey=map.get(1);

        String strSigned=License.sign(message, privateKey);
        Log.log.info(strSigned);
        Log.log.info(License.verify(message, strSigned, pubKey));
    }

}