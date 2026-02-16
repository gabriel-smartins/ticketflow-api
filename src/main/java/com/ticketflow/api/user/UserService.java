package com.ticketflow.api.user;

import com.ticketflow.api.user.dto.RegisterRequestDTO;
import com.ticketflow.api.user.enums.UserRole;
import com.ticketflow.api.user.exception.EmailAlreadyInUseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequestDTO request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyInUseException("E-mail already in use");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        var newUser = User.builder()
                .name(request.name())
                .email(request.email())
                .password(encodedPassword)
                .role(UserRole.USER)
                .build();

        return userRepository.save(newUser);

    }
}
