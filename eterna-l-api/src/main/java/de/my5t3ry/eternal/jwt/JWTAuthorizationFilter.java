package de.my5t3ry.eternal.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static de.my5t3ry.eternal.jwt.SecurityConstants.HEADER_STRING;
import static de.my5t3ry.eternal.jwt.SecurityConstants.TOKEN_PREFIX;


public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    public JWTAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);

    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);
        SecurityContextImpl securityContext = new SecurityContextImpl();

        if (header == null || !header.startsWith(TOKEN_PREFIX) || Objects.nonNull(securityContext.getAuthentication())) {
            chain.doFilter(req, res);
            return;
        }
        securityContext.setAuthentication(getAuthentication(req));
        SecurityContextHolder.setContext(securityContext);
        chain.doFilter(req, res);
    }

    private JwtAuthentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            try {
                final String plainToken = token.replace(TOKEN_PREFIX, "");
//                tokenValidator.verifyToken(plainToken);
                DecodedJWT decodedJWT = JWT.decode(plainToken);
                if (decodedJWT != null) {
                    return new JwtAuthentication(decodedJWT);
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
