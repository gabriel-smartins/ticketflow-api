package com.ticketflow.api.user.auth;

import com.ticketflow.api.config.TokenService;
import com.ticketflow.api.user.User;
import com.ticketflow.api.user.UserService;
import com.ticketflow.api.user.dto.LoginRequestDTO;
import com.ticketflow.api.user.dto.LoginResponseDTO;
import com.ticketflow.api.user.dto.RegisterRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());

        Authentication auth = authenticationManager.authenticate(usernamePassword);

        var user = userService.getUserByEmail(auth.getName());

        String token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequestDTO data) {
        userService.registerUser(data);
        return ResponseEntity.ok().build();
    }
}
