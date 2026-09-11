package app.services;

import app.daos.GoogleCalendarConnectionDAO;
import app.daos.UserDAO;
import app.entities.GoogleCalendarConnection;
import app.entities.User;
import app.exceptions.ApiException;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
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
            "http://localhost:7070/api/google-calendar/callback";

    private static final String CALENDAR_SCOPE =
            "https://www.googleapis.com/auth/calendar";

    private static final NetHttpTransport HTTP_TRANSPORT =
            new NetHttpTransport();

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
                HTTP_TRANSPORT,
                JSON_FACTORY,
                CLIENT_ID,
                CLIENT_SECRET,
                Collections.singletonList(CALENDAR_SCOPE)
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

        // Make sure the application user exists
        userDAO.getById(userId);

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
            throw new ApiException(
                    400,
                    "Authorization code is required"
            );
        }

        User user = userDAO.getById(userId);

        try {

            GoogleAuthorizationCodeFlow flow =
                    createFlow();

            GoogleTokenResponse tokenResponse =
                    flow.newTokenRequest(code)
                            .setRedirectUri(REDIRECT_URI)
                            .execute();

            Credential credential =
                    new Credential.Builder(
                            com.google.api.client.auth.oauth2
                                    .BearerToken
                                    .authorizationHeaderAccessMethod()
                    )
                            .setTransport(HTTP_TRANSPORT)
                            .setJsonFactory(JSON_FACTORY)
                            .setTokenServerEncodedUrl(
                                    "https://oauth2.googleapis.com/token"
                            )
                            .setClientAuthentication(
                                    new com.google.api.client.auth.oauth2
                                            .ClientParametersAuthentication(
                                            CLIENT_ID,
                                            CLIENT_SECRET
                                    )
                            )
                            .build()
                            .setFromTokenResponse(tokenResponse);

            Oauth2 oauth2 =
                    new Oauth2.Builder(
                            HTTP_TRANSPORT,
                            JSON_FACTORY,
                            credential
                    )
                            .setApplicationName("TrainingProject")
                            .build();

            Userinfo googleUser =
                    oauth2.userinfo()
                            .get()
                            .execute();

            GoogleCalendarConnection connection =
                    connectionDAO.getByUserId(userId);

            if (connection == null) {

                connection =
                        new GoogleCalendarConnection(
                                user,
                                googleUser.getId(),
                                credential.getAccessToken(),
                                credential.getRefreshToken()
                        );

                connectionDAO.create(connection);

            } else {

                connection.updateTokens(
                        credential.getAccessToken(),
                        credential.getRefreshToken()
                );

                connectionDAO.update(connection);
            }

        } catch (IOException e) {

            throw new ApiException(
                    500,
                    "Could not authenticate with Google"
            );
        }
    }
}