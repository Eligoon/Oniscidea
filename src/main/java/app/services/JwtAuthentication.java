
package app.services;

import app.exceptions.ApiException;
import io.javalin.http.Context;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

public class JwtAuthentication {

    private final JwtService jwtService;

    public JwtAuthentication(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public void authenticate(Context ctx) {

        String authorizationHeader =
                ctx.header("Authorization");

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {
            throw new ApiException(
                    401,
                    "Authentication required"
            );
        }

        String token =
                authorizationHeader.substring(7);

        if (token.isBlank()) {
            throw new ApiException(
                    401,
                    "Authentication required"
            );
        }

        try {
            Claims claims =
                    jwtService.validateToken(token);

            Integer userId =
                    Integer.valueOf(claims.getSubject());

            ctx.attribute(
                    "authenticatedUserId",
                    userId
            );

        } catch (JwtException | IllegalArgumentException e) {
            throw new ApiException(
                    401,
                    "Invalid or expired token"
            );
        }
    }
}
