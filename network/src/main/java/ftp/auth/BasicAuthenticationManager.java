package ftp.auth;

import ftp.repo.UserRepository;

public class BasicAuthenticationManager implements AuthenticationManager{
    private final UserRepository userRepository = UserRepository.getInstance();

    @Override
    public boolean authenticate(String username, String password) {
        return userRepository.get(username) != null ? true : false;
    }
}
