package telran.project.gardenshop.service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.dto.security.AuthRequest;
import telran.project.gardenshop.dto.security.AuthResponse;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final JwtService jwtService;

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
        //Нужно сделать проверку , аналогичную вызову метода
        //loadUserByUsername - UserDetailsService
        authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(authRequest.email(),
                                authRequest.password()));

        //Если пользователя нет или пароль кривой то сюда не дойдем
        UserDetails user = userDetailsService.loadUserByUsername(authRequest.email());

        return new AuthResponse(jwtService.generateToken(user));
    }
}