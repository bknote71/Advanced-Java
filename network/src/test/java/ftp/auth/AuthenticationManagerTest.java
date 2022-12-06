package ftp.auth;

import org.junit.Test;

import static org.junit.Assert.*;

public class AuthenticationManagerTest {
    @Test
    public void userLogin() {
        final AuthenticationManager authenticationManager = new BasicAuthenticationManager();
        boolean ret = authenticationManager.authenticate("user", "user");
        assertTrue(ret);
    }

    @Test
    public void incorrectPassword() {
        // but pass by basic authentication flow
        final AuthenticationManager authenticationManager = new BasicAuthenticationManager();
        boolean ret = authenticationManager.authenticate("user", "abcdefg");
        assertTrue(ret);
    }

    @Test
    public void incorrectUsername() {
        final AuthenticationManager authenticationManager = new BasicAuthenticationManager();
        final boolean ret = authenticationManager.authenticate("abc", "user");
        assertFalse(ret);

    }

    @Test
    public void trimTest() {
        final BasicAuthenticationManager basicAuthenticationManager = new BasicAuthenticationManager();
        final boolean authenticate = basicAuthenticationManager.authenticate("ftp ", "ftp ");
        assertTrue(authenticate);
    }

}