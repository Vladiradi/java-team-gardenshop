package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.entity.CartItem;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.repository.CartItemRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final UserService userService;

    @Override
    public CartItem getById(Long cartItemId) {
        // user from SecurityContext
        String email = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null;

        if (email == null) {
            throw new UsernameNotFoundException("Anonymous access is not allowed");
        }

        User current = userService.getUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        // took item and checking that it belong to our current user
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem with id " + cartItemId + " not found"));

        if (item.getCart() == null || item.getCart().getUser() == null
                || !item.getCart().getUser().getId().equals(current.getId())) {
            // masking like EntityNotFound +security
            throw new EntityNotFoundException("CartItem with id " + cartItemId + " not found");
        }

        return item;
    }
}
