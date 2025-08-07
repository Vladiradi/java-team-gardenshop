package telran.project.gardenshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import telran.project.gardenshop.dto.security.AuthRequest;
import telran.project.gardenshop.dto.security.AuthResponse;
import telran.project.gardenshop.service.security.AuthenticationService;

@RestController
@RequestMapping("/v1/login")  //http://localhost:8080/login
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    /*
        AuthRequest ;
        {
            "email" : "alex@alex.com",
            "password" : "12345"

        }

        AuthResponse :

        {
            "token" : "dfgfdgfgdfg.dfgdfgdgdfg.dfgdfgdfgd"
        }

        //POST, GET, PATCH,DELETE
        //Authorization : "Bearer Token" : dfgfdgfgdfg.dfgdfgdgdfg.dfgdfgdfgd

     */

    @PostMapping
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        return authenticationService.authenticate(authRequest);
    }
}