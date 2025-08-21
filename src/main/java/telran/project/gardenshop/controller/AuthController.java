package telran.project.gardenshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import telran.project.gardenshop.dto.security.AuthRequest;
import telran.project.gardenshop.dto.security.AuthResponse;
import telran.project.gardenshop.service.security.AuthenticationService;

@RestController
@RequestMapping("/v1/login")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication operations")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping
    @Operation(summary = "User login", description = "Authenticate user and receive JWT token")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        return authenticationService.authenticate(authRequest);
    }
}
