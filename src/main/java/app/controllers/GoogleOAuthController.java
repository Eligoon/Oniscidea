package app.controllers;

import app.services.GoogleOAuthService;
import io.javalin.http.Context;

public class GoogleOAuthController {

    private final GoogleOAuthService googleOAuthService;

    public GoogleOAuthController(
            GoogleOAuthService googleOAuthService
    ) {
        this.googleOAuthService =
                googleOAuthService;
    }

    public void connect(Context ctx) {

        Integer userId =
                Integer.parseInt(
                        ctx.queryParam("userId")
                );

        String authorizationUrl =
                googleOAuthService.getAuthorizationUrl(
                        userId
                );

        ctx.redirect(authorizationUrl);
    }

    public void callback(Context ctx) {

        String code =
                ctx.queryParam("code");

        String state =
                ctx.queryParam("state");

        googleOAuthService.authenticate(
                code,
                state
        );

        ctx.result(
                "Google Calendar connected successfully"
        );
    }
}