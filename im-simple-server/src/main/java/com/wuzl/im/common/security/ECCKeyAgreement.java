package com.wuzl.im.common.security;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import javax.crypto.KeyAgreement;

/**
 * 
 * 类ECCKeyAgreement.java的实现描述：TODO 类实现描述 
 * @author macun 2017年3月7日 下午8:25:31
 */
public class ECCKeyAgreement {
  public static void main(String[] args) throws Exception {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC","SunEC");
    ECGenParameterSpec ecsp= new ECGenParameterSpec("secp192k1");
    kpg.initialize(ecsp);

    KeyPair kpU = kpg.genKeyPair();
    PrivateKey privKeyU = kpU.getPrivate();
    PublicKey pubKeyU = kpU.getPublic();
    System.out.println("User U: " + privKeyU.toString());
    System.out.println("User U: " + pubKeyU.toString());

    KeyPair kpV = kpg.genKeyPair();
    PrivateKey privKeyV = kpV.getPrivate();
    PublicKey pubKeyV = kpV.getPublic();
    System.out.println("User V: " + privKeyV.toString());
    System.out.println("User V: " + pubKeyV.toString());

    KeyAgreement ecdhU = KeyAgreement.getInstance("ECDH");
    ecdhU.init(privKeyU);
    ecdhU.doPhase(pubKeyV,true);

    KeyAgreement ecdhV = KeyAgreement.getInstance("ECDH");
    ecdhV.init(privKeyV);
    ecdhV.doPhase(pubKeyU,true);

    System.out.println("Secret computed by U: 0x" + 
                       (new BigInteger(1, ecdhU.generateSecret()).toString(16)).toUpperCase());
    System.out.println("Secret computed by V: 0x" + 
                       (new BigInteger(1, ecdhV.generateSecret()).toString(16)).toUpperCase());
  }
}
