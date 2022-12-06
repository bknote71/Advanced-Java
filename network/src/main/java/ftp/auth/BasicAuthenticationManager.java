package ftp.auth;

import ftp.repo.UserRepository;

public class BasicAuthenticationManager implements AuthenticationManager{
    private final UserRepository userRepository = UserRepository.getInstance();

    @Override
    public boolean authenticate(String username, String password) {
        final String trimmedUsername = username.trim();
        System.out.println(trimmedUsername);
        return userRepository.get(trimmedUsername) != null ? true : false;
    }
}
