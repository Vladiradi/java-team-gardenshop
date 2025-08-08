package telran.project.gardenshop.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.service.UserService;
import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        ///input - >  username - email
        /// output - > User(email, password, Roles)
        org.springframework.security.core.userdetails.User springUser = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword() //jxgkjhsdjkfhsdkshgkjshgkjfhgjkdhgkjdfghjdfghjghkfjghdjkgh
                , Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())));

        return springUser;

        /// UI -> alex@gmail.com , 12345
        /// DB -> alex@gmail.com , sdmfngjkdfhgjkdfhgsdkfjghsdfkgjhdsfkgjhsfdkgjhfdkghdfkghdfgkhgk
        /// Spring alex@gmail.com -> alex@gmail.com

        /// Spring -> 12345 ->  sdmfngjkdfhgjkdfhgsdkfjghsdfkgjhdsfkgjhsfdkgjhfdkghdfkghdfgkhgk
        /// Spring -> sdmfngjkdfhgjkdfhgsdkfjghsdfkgjhdsfkgjhsfdkgjhfdkghdfkghdfgkhgk ,
        ///           sdmfngjkdfhgjkdfhgsdkfjghsdfkgjhdsfkgjhsfdkgjhfdkghdfkghdfgkhgk

    }
}