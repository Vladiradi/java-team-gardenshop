package telran.project.gardenshop.service.security;

//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//import javax.crypto.SecretKey;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//
//
//@Service
//public class JwtService {
//
//    @Value("${jwt.signing.key}")
//    private String jwtSecret;
//
//    public String generateToken(UserDetails user) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("login", user.getUsername());
//        claims.put("roles", user.getAuthorities());
//
//        return generateToken(user, claims);
//    }
//
//    private String generateToken(UserDetails user, Map<String, Object> claims) {
//        return Jwts.builder()
//                .claims()
//                .subject(user.getUsername())
//                .issuedAt(new Date())
//                .expiration(new Date(System.currentTimeMillis() + 10000))
//                .add(claims)
//                .and()
//                .signWith(getJwtSecret())
//                .compact();
//    }
//
//    private SecretKey getJwtSecret() {
//        byte[] decode = Decoders.BASE64.decode(jwtSecret);
//        return Keys.hmacShaKeyFor(decode);
//    }
//
//    public String extractUserName(String jwt) {
//        return extractClaim(jwt, Claims::getSubject);
//    }
//
//    public Date extractExpiration(String jwt) {
//        return extractClaim(jwt, Claims::getExpiration);
//    }
//
//    public boolean isValidToken(String token) {
//        return new Date().before(extractExpiration(token));
//    }
//
//    private <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
//        Claims claims = extractClaims(jwt);
//        return claimsResolver.apply(claims);
//    }
//
//    private Claims extractClaims(String jwt) {
//        return Jwts.parser()
//                .setSigningKey(getJwtSecret())
//                .build()
//                .parseSignedClaims(jwt).getPayload();
//    }
//}
//

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.signing.key}")
    private String jwtSecretBase64;

    @Value("${jwt.expiration-ms:86400000}")
    private long expirationMs;

    public String generateToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("login", user.getUsername());
        claims.put("roles", user.getAuthorities());

        Date now = new Date();
        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isValidToken(String token) {
        try {
            Claims claims = parse(token); // проверяет подпись
            return new Date().before(claims.getExpiration());
        } catch (Exception e) {
            return false;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(parse(token));
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // корректный способ для 0.12.x
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] key = Decoders.BASE64.decode(jwtSecretBase64);
        return Keys.hmacShaKeyFor(key);
    }
}
