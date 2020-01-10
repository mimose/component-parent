package com.mimose.component.jwt.starter.util;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @Description
 * @Author ccy
 * @Date 2020/1/8
 */
public class RSAUtil {

    private static final String SIGNATURE_CONST = "MD5withRSA";
    public static String RSA_ALGORITHM = "RSA";
    public static String UTF8 = "UTF-8";


    /**
     * 获取公钥对象
     *
     * @param pubKeyData
     * @return
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(byte[] pubKeyData) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKeyData);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * 获取公钥对象
     *
     * @param pubKey 公钥
     * @return
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String pubKey) throws Exception {
        return getPublicKey(Base64Util.decodeBase64(pubKey));

    }

    public static String encryptByPublicKey(String data, String publicKey) throws Exception {
        return encryptByPublicKey(data, getPublicKey(publicKey));
    }

    /**
     * 公钥加密
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data, RSAPublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytes = cipher.doFinal(data.getBytes(UTF8));
        return Base64Util.encodeBase64String(bytes);
    }

    //======================================================================================

    /**
     * 获取私钥对象
     * @param privateKeyData
     * @return
     * @throws Exception
     */
    private static RSAPrivateKey getPrivateKey(byte[] privateKeyData) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyData);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    /**
     * 获取私钥对象
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws Exception {
        return getPrivateKey(Base64Util.decodeBase64(privateKey));
    }

    public static String decryptByPrivateKey(String data, String rsaPrivateKey) throws Exception {
        return decryptByPrivateKey(data, getPrivateKey(rsaPrivateKey));
    }

    /**
     * 私钥解密
     *
     * @param data
     * @param rsaPrivateKey
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String data, RSAPrivateKey rsaPrivateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] inputData = Base64Util.decodeBase64(data);
        byte[] bytes = cipher.doFinal(inputData);
        return new String(bytes, UTF8);
    }


    //======================================================================================

    /**
     * 签名
     * @param data          数据
     * @param privateKey    私钥
     * @return
     * @throws Exception
     */
    public static String sign(String data, String privateKey) throws Exception {
        byte[] keyBytes = Base64Util.decodeBase64(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_CONST);
        signature.initSign(key);
        signature.update(data.getBytes());
        return new String(Base64Util.encodeBase64(signature.sign()));
    }

    /**
     * 验签
     * @param srcData   原始值
     * @param publicKey 公钥
     * @param sign      签名
     * @return
     * @throws Exception
     */
    public static boolean verify(String srcData, String publicKey, String sign) throws Exception {
        byte[] keyBytes = Base64Util.decodeBase64(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PublicKey key = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_CONST);
        signature.initVerify(key);
        signature.update(srcData.getBytes());
        return signature.verify(Base64Util.decodeBase64(sign.getBytes()));
    }

}
