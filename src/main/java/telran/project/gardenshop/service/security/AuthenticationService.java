package telran.project.gardenshop.service.security;

import telran.project.gardenshop.dto.security.AuthRequest;
import telran.project.gardenshop.dto.security.AuthResponse;

public interface AuthenticationService {

    AuthResponse authenticate(AuthRequest authRequest);
}