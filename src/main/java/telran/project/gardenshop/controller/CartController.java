package telran.project.gardenshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telran.project.gardenshop.dto.CartResponseDto;
import telran.project.gardenshop.service.CartService;

@RestController
@RequestMapping("/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;


}
