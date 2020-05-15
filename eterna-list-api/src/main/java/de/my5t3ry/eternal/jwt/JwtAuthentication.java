package de.my5t3ry.eternal.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Slf4j
public class JwtAuthentication implements Authentication {
    private static final long serialVersionUID = 5857180445588229489L;
    private final DecodedJWT decodedJWT;
    private final ObjectMapper om = new ObjectMapper();

    public JwtAuthentication(DecodedJWT decodedJWT) {
        this.decodedJWT = decodedJWT;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return decodedJWT.getToken();
    }

    @Override
    public Object getDetails() {
        return decodedJWT.getToken();
    }

    @Override
    public Object getPrincipal() {
        return decodedJWT;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {
    }

    @Override
    public String getName() {
        return getClaim("name");
    }


    private String getClaim(final String s) {
        return decodedJWT.getClaim(s).asString();
    }

    public String getEmail() {
        return decodedJWT.getClaim("email").asString();
    }
}
