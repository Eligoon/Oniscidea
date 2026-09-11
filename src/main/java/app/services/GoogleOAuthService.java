package app.services;

import app.daos.GoogleCalendarConnectionDAO;
import app.daos.UserDAO;
import app.entities.GoogleCalendarConnection;
import app.entities.User;
import app.exceptions.ApiException;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.auth.oauth2.Credential;

import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;

import java.io.IOException;
import java.util.Collections;

public class GoogleOAuthService {

    private static final String CLIENT_ID =
            System.getenv("GOOGLE_CLIENT_ID");

    private static final String CLIENT_SECRET =
            System.getenv("GOOGLE_CLIENT_SECRET");

    private static final String REDIRECT_URI =
            "http://localhost:8080/api/google-calendar/callback";

    private static final GsonFactory JSON_FACTORY =
            GsonFactory.getDefaultInstance();

    private final GoogleCalendarConnectionDAO connectionDAO;
    private final UserDAO userDAO;

    public GoogleOAuthService(
            GoogleCalendarConnectionDAO connectionDAO,
            UserDAO userDAO
    ) {
        this.connectionDAO = connectionDAO;
        this.userDAO = userDAO;
    }

    private GoogleAuthorizationCodeFlow createFlow() {

        return new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(),
                JSON_FACTORY,
                CLIENT_ID,
                CLIENT_SECRET,
                Collections.singletonList(
                        "https://www.googleapis.com/auth/calendar"
                )
        )
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();
    }

    public String getAuthorizationUrl(
            Integer userId,
            String state
    ) {

        if (userId == null) {
            throw new ApiException(401, "User must be logged in");
        }

        if (state == null || state.isBlank()) {
            throw new ApiException(400, "OAuth state is required");
        }

        return createFlow()
                .newAuthorizationUrl()
                .setRedirectUri(REDIRECT_URI)
                .setState(state)
                .build();
    }

    public void authenticate(
            Integer userId,
            String code
    ) {

        if (userId == null) {
            throw new ApiException(401, "User must be logged in");
        }

        if (code == null || code.isBlank()) {
            throw new ApiException(400, "Authorization code is required");
        }

        try {

            GoogleAuthorizationCodeFlow flow =
                    createFlow();

            GoogleTokenResponse tokenResponse =
                    flow.newTokenRequest(code)
                            .setRedirectUri(REDIRECT_URI)
                            .execute();

            Credential credential =
                    flow.createAndStoreCredential(
                            tokenResponse,
                            String.valueOf(userId)
                    );

            Oauth2 oauth2 = new Oauth2.Builder(
                    new NetHttpTransport(),
                    JSON_FACTORY,
                    credential
            )
                    .setApplicationName("Training Calendar")
                    .build();

            Userinfo googleUser =
                    oauth2.userinfo()
                            .get()
                            .execute();

            User user =
                    userDAO.getById(userId);

            GoogleCalendarConnection connection;

            try {

                connection =
                        connectionDAO.getByUserId(userId);

                connection.updateTokens(
                        credential.getAccessToken(),
                        credential.getRefreshToken()
                );

                connectionDAO.update(connection);

            } catch (ApiException e) {

                if (e.getCode() != 404) {
                    throw e;
                }
                connection =
                        new GoogleCalendarConnection(
                                user,
                                googleUser.getId(),
                                credential.getAccessToken(),
                                credential.getRefreshToken()
                        );

                connectionDAO.create(connection);
            }

        } catch (IOException e) {

            throw new ApiException(
                    500,
                    "Could not authenticate with Google"
            );
        }
    }
}
