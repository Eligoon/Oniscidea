package app.services;

import app.daos.GoogleCalendarConnectionDAO;
import app.daos.UserDAO;
import app.entities.GoogleCalendarConnection;
import app.entities.User;
import app.exceptions.ApiException;
import app.utils.Utils;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Collections;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class GoogleOAuthService {

    private static final String CLIENT_ID =
            Utils.getPropertyValue(
                    "GOOGLE_CLIENT_ID",
                    "config.properties"
            );

    private static final String CLIENT_SECRET =
            Utils.getPropertyValue(
                    "GOOGLE_CLIENT_SECRET",
                    "config.properties"
            );

    private static final String REDIRECT_URI =
            Utils.getPropertyValue(
                    "GOOGLE_REDIRECT_URI",
                    "config.properties"
            );

    private static final String OAUTH_STATE_SECRET =
            Utils.getPropertyValue(
                    "GOOGLE_OAUTH_STATE_SECRET",
                    "config.properties"
            );

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

    public String getAuthorizationUrl(Integer userId) {

        if (userId == null) {
            throw new ApiException(
                    401,
                    "User must be logged in"
            );
        }

        // Make sure the application user exists
        userDAO.getById(userId);

        String state =
                createState(userId);

        return createFlow()
                .newAuthorizationUrl()
                .setRedirectUri(REDIRECT_URI)
                .setState(state)
                .build();
    }

    public void authenticate(
            String code,
            String state
    ) {

        if (code == null || code.isBlank()) {
            throw new ApiException(
                    400,
                    "Authorization code is required"
            );
        }

        Integer userId =
                validateState(state);

        // Make sure the application user exists
        User user =
                userDAO.getById(userId);

        try {

            GoogleAuthorizationCodeFlow flow =
                    createFlow();

            GoogleTokenResponse tokenResponse =
                    flow.newTokenRequest(code)
                            .setRedirectUri(REDIRECT_URI)
                            .execute();

            Credential credential =
                    new Credential.Builder(
                            BearerToken.authorizationHeaderAccessMethod()
                    )
                            .setTransport(HTTP_TRANSPORT)
                            .setJsonFactory(JSON_FACTORY)
                            .setTokenServerEncodedUrl(
                                    "https://oauth2.googleapis.com/token"
                            )
                            .setClientAuthentication(
                                    new ClientParametersAuthentication(
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
                    null;

            try {
                connection =
                        connectionDAO.getByUserId(userId);
            } catch (ApiException e) {

                if (e.getCode() != 404) {
                    throw e;
                }
            }

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

        } catch (ApiException e) {

            throw e;

        } catch (IOException e) {

            throw new ApiException(
                    500,
                    "Could not authenticate with Google"
            );
        }
    }

    private String createState(Integer userId) {

        if (userId == null) {
            throw new ApiException(
                    401,
                    "User must be logged in"
            );
        }

        String value =
                userId + ":" + System.currentTimeMillis();

        try {

            Mac mac =
                    Mac.getInstance("HmacSHA256");

            SecretKeySpec key =
                    new SecretKeySpec(
                            OAUTH_STATE_SECRET.getBytes(
                                    StandardCharsets.UTF_8
                            ),
                            "HmacSHA256"
                    );

            mac.init(key);

            byte[] signature =
                    mac.doFinal(
                            value.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            String encodedSignature =
                    Base64.getUrlEncoder()
                            .withoutPadding()
                            .encodeToString(signature);

            return value + ":" + encodedSignature;

        } catch (Exception e) {

            throw new ApiException(
                    500,
                    "Could not create OAuth state"
            );
        }
    }

    private Integer validateState(String state) {

        if (state == null || state.isBlank()) {
            throw new ApiException(
                    400,
                    "OAuth state is required"
            );
        }

        String[] parts =
                state.split(":");

        if (parts.length != 3) {
            throw new ApiException(
                    400,
                    "Invalid OAuth state"
            );
        }

        String value =
                parts[0] + ":" + parts[1];

        String providedSignature =
                parts[2];

        try {

            Mac mac =
                    Mac.getInstance("HmacSHA256");

            SecretKeySpec key =
                    new SecretKeySpec(
                            OAUTH_STATE_SECRET.getBytes(
                                    StandardCharsets.UTF_8
                            ),
                            "HmacSHA256"
                    );

            mac.init(key);

            byte[] signature =
                    mac.doFinal(
                            value.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            String expectedSignature =
                    Base64.getUrlEncoder()
                            .withoutPadding()
                            .encodeToString(signature);

            boolean valid =
                    MessageDigest.isEqual(
                            expectedSignature.getBytes(
                                    StandardCharsets.UTF_8
                            ),
                            providedSignature.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            if (!valid) {
                throw new ApiException(
                        400,
                        "Invalid OAuth state"
                );
            }

            return Integer.parseInt(parts[0]);

        } catch (ApiException e) {

            throw e;

        } catch (Exception e) {

            throw new ApiException(
                    400,
                    "Invalid OAuth state"
            );
        }
    }
}