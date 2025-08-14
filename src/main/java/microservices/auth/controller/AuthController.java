package microservices.auth.controller;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    @Value("${auth.jwt.secret:change-me-please-change-me-please-change-me}")
    private String jwtSecret;

    @Value("${auth.jwt.issuer:tickeasy}")
    private String jwtIssuer;

    @Value("${auth.jwt.ttlSeconds:3600}")
    private long ttlSeconds;

    @PostMapping("/token")
    public ResponseEntity<Map<String, Object>> issueToken(
            @RequestParam @NotBlank String subject,
            @RequestParam(defaultValue = "USER") String role
    ) throws Exception {
        Instant now = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer(jwtIssuer)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(ttlSeconds)))
                .claim("role", role)
                .build();

        JWSSigner signer = new MACSigner(jwtSecret.getBytes());
        SignedJWT jwt = new SignedJWT(
                new com.nimbusds.jose.JWSHeader(JWSAlgorithm.HS256),
                claims
        );
        jwt.sign(signer);

        Map<String, Object> resp = new HashMap<>();
        resp.put("access_token", jwt.serialize());
        resp.put("token_type", "Bearer");
        resp.put("expires_in", ttlSeconds);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/introspect")
    public ResponseEntity<Map<String, Object>> introspect(
            @RequestParam(value = "token", required = false) String token,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if ((token == null || token.isBlank()) && authorization != null && authorization.startsWith("Bearer ")) {
                token = authorization.substring("Bearer ".length()).trim();
            }
            if (token == null || token.isBlank()) {
                resp.put("active", false);
                resp.put("error", "missing_token");
                return ResponseEntity.badRequest().body(resp);
            }

            SignedJWT jwt = SignedJWT.parse(token);
            boolean signatureValid = jwt.verify(new MACVerifier(jwtSecret.getBytes()));
            if (!signatureValid) {
                resp.put("active", false);
                resp.put("error", "invalid_signature");
                return ResponseEntity.ok(resp);
            }

            var claims = jwt.getJWTClaimsSet();
            Date now = new Date();
            Date exp = claims.getExpirationTime();
            if (exp == null || exp.before(now)) {
                resp.put("active", false);
                resp.put("error", "token_expired");
                return ResponseEntity.ok(resp);
            }

            resp.put("active", true);
            resp.put("sub", claims.getSubject());
            resp.put("iss", claims.getIssuer());
            resp.put("iat", claims.getIssueTime() != null ? claims.getIssueTime().getTime() / 1000 : null);
            resp.put("exp", claims.getExpirationTime() != null ? claims.getExpirationTime().getTime() / 1000 : null);
            Object role = claims.getClaim("role");
            if (role != null) {
                resp.put("role", role);
            }
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("active", false);
            resp.put("error", "invalid_token");
            return ResponseEntity.ok(resp);
        }
    }
}


