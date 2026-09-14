package app.services;

import app.daos.GoogleCalendarConnectionDAO;
import app.entities.GoogleCalendarConnection;
import app.exceptions.ApiException;
import app.utils.Utils;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Date;

public class GoogleCalendarService {

    private static final String APPLICATION_NAME =
            "TrainingProject";

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

    private final GoogleCalendarConnectionDAO connectionDAO;

    public GoogleCalendarService(
            GoogleCalendarConnectionDAO connectionDAO
    ) {
        this.connectionDAO = connectionDAO;
    }

    private GoogleCalendarConnection getConnection(Integer userId) {

        if (userId == null) {
            throw new ApiException(
                    401,
                    "User must be logged in"
            );
        }

        GoogleCalendarConnection connection =
                connectionDAO.getByUserId(userId);

        if (connection == null) {
            throw new ApiException(
                    400,
                    "Google Calendar is not connected"
            );
        }

        return connection;
    }

    private Calendar getCalendar(
            GoogleCalendarConnection connection
    ) {

        try {

            Credential credential =
                    new Credential.Builder(
                            BearerToken.authorizationHeaderAccessMethod()
                    )
                            .setTransport(
                                    GoogleNetHttpTransport
                                            .newTrustedTransport()
                            )
                            .setJsonFactory(
                                    GsonFactory.getDefaultInstance()
                            )
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
                            .setAccessToken(
                                    connection.getAccessToken()
                            )
                            .setRefreshToken(
                                    connection.getRefreshToken()
                            );

            return new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    credential
            )
                    .setApplicationName(APPLICATION_NAME)
                    .build();

        } catch (Exception e) {

            throw new ApiException(
                    500,
                    "Could not connect to Google Calendar"
            );
        }
    }

    public String createEvent(
            Integer userId,
            String title,
            String description,
            ZonedDateTime start,
            ZonedDateTime end
    ) {

        if (title == null || title.isBlank()) {
            throw new ApiException(
                    400,
                    "Event title is required"
            );
        }

        if (start == null || end == null) {
            throw new ApiException(
                    400,
                    "Event start and end are required"
            );
        }

        if (!end.isAfter(start)) {
            throw new ApiException(
                    400,
                    "Event end must be after event start"
            );
        }

        GoogleCalendarConnection connection =
                getConnection(userId);

        try {

            Calendar calendar =
                    getCalendar(connection);

            Event event =
                    new Event()
                            .setSummary(title)
                            .setDescription(description);

            event.setStart(
                    toEventDateTime(start)
            );

            event.setEnd(
                    toEventDateTime(end)
            );

            Event createdEvent =
                    calendar.events()
                            .insert(
                                    connection.getCalendarId(),
                                    event
                            )
                            .execute();

            return createdEvent.getId();

        } catch (IOException e) {

            throw new ApiException(
                    500,
                    "Could not create Google Calendar event"
            );
        }
    }

    public void updateEvent(
            Integer userId,
            String eventId,
            String title,
            String description,
            ZonedDateTime start,
            ZonedDateTime end
    ) {

        if (eventId == null || eventId.isBlank()) {
            throw new ApiException(
                    400,
                    "Google event ID is required"
            );
        }

        if (title == null || title.isBlank()) {
            throw new ApiException(
                    400,
                    "Event title is required"
            );
        }

        if (start == null || end == null) {
            throw new ApiException(
                    400,
                    "Event start and end are required"
            );
        }

        if (!end.isAfter(start)) {
            throw new ApiException(
                    400,
                    "Event end must be after event start"
            );
        }

        GoogleCalendarConnection connection =
                getConnection(userId);

        try {

            Calendar calendar =
                    getCalendar(connection);

            Event event =
                    calendar.events()
                            .get(
                                    connection.getCalendarId(),
                                    eventId
                            )
                            .execute();

            event.setSummary(title);
            event.setDescription(description);
            event.setStart(toEventDateTime(start));
            event.setEnd(toEventDateTime(end));

            calendar.events()
                    .update(
                            connection.getCalendarId(),
                            eventId,
                            event
                    )
                    .execute();

        } catch (IOException e) {

            throw new ApiException(
                    500,
                    "Could not update Google Calendar event"
            );
        }
    }

    public void deleteEvent(
            Integer userId,
            String eventId
    ) {

        if (eventId == null || eventId.isBlank()) {
            return;
        }

        GoogleCalendarConnection connection =
                getConnection(userId);

        try {

            Calendar calendar =
                    getCalendar(connection);

            calendar.events()
                    .delete(
                            connection.getCalendarId(),
                            eventId
                    )
                    .execute();

        } catch (IOException e) {

            throw new ApiException(
                    500,
                    "Could not delete Google Calendar event"
            );
        }
    }

    private EventDateTime toEventDateTime(
            ZonedDateTime dateTime
    ) {

        return new EventDateTime()
                .setDateTime(
                        new com.google.api.client.util.DateTime(
                                Date.from(
                                        dateTime.toInstant()
                                )
                        )
                )
                .setTimeZone(
                        dateTime.getZone().getId()
                );
    }
}