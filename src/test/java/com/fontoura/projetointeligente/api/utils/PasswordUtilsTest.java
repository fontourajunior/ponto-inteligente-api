package com.fontoura.projetointeligente.api.utils;

import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class PasswordUtilsTest {

    private static final String SENHA = "123456";
    private final BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();

    @Test
    public void testSenhaNula() throws Exception {
        assertNull(PasswordUtils.gerarBCrypt(null));
    }

    @Test
    public void testarGerarHashSenha() throws Exception {
        String hash = PasswordUtils.gerarBCrypt(SENHA);
        assertTrue(bCryptEncoder.matches(SENHA, hash));
    }

}