package com.woobeee.back;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Test;

public class JasyptEncryptorSmokeTest {
    String jasyptPassword = System.getenv("JASYPT_ENCRYPTOR_PASSWORD"); // 환경변수 읽기

    private PooledPBEStringEncryptor encryptor() {
        SimpleStringPBEConfig c = new SimpleStringPBEConfig();
        c.setPassword(jasyptPassword); // 네가 지정한 비밀번호
        c.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        c.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        c.setKeyObtentionIterations("200000"); // 충분히 크게
        c.setPoolSize("1");
        c.setStringOutputType("base64");

        PooledPBEStringEncryptor e = new PooledPBEStringEncryptor();
        e.setConfig(c);
        return e;
    }

    @Test
    void printEncryptedAdmin() {
        var enc = encryptor();
        String userCipher = enc.encrypt("admin");
        String passCipher = enc.encrypt("admin");

        System.out.println("username: ENC(" + userCipher + ")");
        System.out.println("password: ENC(" + passCipher + ")");
    }
}