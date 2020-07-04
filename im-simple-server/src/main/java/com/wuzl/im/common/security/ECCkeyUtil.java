package com.wuzl.im.common.security;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wuzl.im.common.exception.ImException;
import com.wuzl.im.common.util.Bytes;

public class ECCkeyUtil {

    private static final Logger logger = LoggerFactory.getLogger("PROJECT_ERROR");
    private static final String stdName = "secp256k1";

    /**
     * 生成公钥和私钥对
     * 
     * @return
     */
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "SunEC");
            ECGenParameterSpec ecsp;
            ecsp = new ECGenParameterSpec(stdName);
            kpg.initialize(ecsp);
            KeyPair kp = kpg.genKeyPair();
            // PrivateKey privateKey = kp.getPrivate();
            // eccKeyPairModel.setPrivateKey(privateKey);
            // ECPublicKey publicKey = (ECPublicKey) kp.getPublic();
            // EccPublicKeyModel publicKeyModel = new EccPublicKeyModel();
            // publicKeyModel.setX(publicKey.getW().getAffineX());
            // publicKeyModel.setY(publicKey.getW().getAffineY());
            // eccKeyPairModel.setPublicKey(publicKeyModel);
            return kp;
        } catch (Exception e) {
            logger.error("获取公钥和私钥对失败:" + e.getMessage(), e);
            throw new ImException("获取公钥和私钥对失败");
        }
    }

    /**
     * 序列化公钥
     * 
     * @param pubicKey
     * @return
     */
    public static String serializePublicKey(PublicKey pubicKey) {
        if (pubicKey == null || !(pubicKey instanceof ECPublicKey)) {
            return null;
        }
        return Bytes.bytes2HexString(pubicKey.getEncoded());
    }

    /**
     * 解析公钥
     * 
     * @param key
     * @return
     */
    public static PublicKey deserializePublicKey(String key) {
        try {
            // AlgorithmParameters algoParameters = AlgorithmParameters.getInstance("EC");
            // algoParameters.init(new ECGenParameterSpec("secp256r1"));
            // ECParameterSpec parameterSpec = algoParameters.getParameterSpec(ECParameterSpec.class);
            //
            // KeySpec keySpec = new ECPublicKeySpec(new ECPoint(new BigInteger(publicKeyModel.getX()),
            // new BigInteger(publicKeyModel.getY())),
            // parameterSpec);
            // return KeyFactory.getInstance("EC").generatePublic(keySpec);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Bytes.hexString2Bytes(key));
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (Exception e) {
            logger.error("根据坐标解析公钥失败:" + e.getMessage(), e);
            throw new ImException("解析公钥失败");
        }
    }

    /**
     * 计算出key
     * 
     * @param keyPair
     * @return
     */
    public static byte[] generateSecret(KeyPair keyPair) {
        if (keyPair == null) {
            return null;
        }
        try {
            KeyAgreement ecdh = KeyAgreement.getInstance("ECDH");
            ecdh.init(keyPair.getPrivate());
            ecdh.doPhase(keyPair.getPublic(), true);
            // return (new BigInteger(1, ecdh.generateSecret()).toString(16)).toUpperCase();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(ecdh.generateSecret());
            byte[] b = md.digest();
            return b;
        } catch (Exception e) {
            logger.error("根据公钥私钥计算key失败:" + e.getMessage(), e);
            throw new ImException("根据公钥私钥计算key失败");
        }
    }
}
