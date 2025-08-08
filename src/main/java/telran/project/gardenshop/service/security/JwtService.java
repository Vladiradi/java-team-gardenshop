package telran.project.gardenshop.service.security;

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

    // jwt - string 1.2.3

    @Value("${jwt.signing.key}")
    private String jwtSecret;

    /*
    {
  "sub": "anna.updated@garden.ru",
  "iat": 1753870544,
  "exp": 1753874144,
  "roles": [
    {
      "authority": "USER"
    }
  ],
  "login": "anna.updated@garden.ru"
}


     */

    public String generateToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("login", user.getUsername());
        claims.put("roles", user.getAuthorities());

        return generateToken(user, claims);
    }

    private String generateToken(UserDetails user, Map<String, Object> claims) {
        return Jwts.builder()
                .claims()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 10000))
                .add(claims)
                .and()
                .signWith(getJwtSecret())
                .compact();
    }

    private SecretKey getJwtSecret() {
        byte[] decode = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(decode);
    }

    public String extractUserName(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    public Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }

    public boolean isValidToken(String token) {
        return new Date().before(extractExpiration(token));
    }

    private <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
        Claims claims = extractClaims(jwt);
        return claimsResolver.apply(claims);
    }

    private Claims extractClaims(String jwt) {
        return Jwts.parser()
                .setSigningKey(getJwtSecret())
                .build()
                .parseSignedClaims(jwt).getPayload();
    }
}