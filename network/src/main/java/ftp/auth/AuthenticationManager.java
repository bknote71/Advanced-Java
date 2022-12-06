package ftp.auth;

import ftp.repo.UserRepository;

public interface AuthenticationManager {

    boolean authenticate(String username, String password);
}
