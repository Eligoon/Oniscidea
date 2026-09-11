package app.services;

import app.daos.GoogleCalendarConnectionDAO;
import app.entities.GoogleCalendarConnection;
import app.exceptions.ApiException;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class GoogleCalendarService {

    private static final String APPLICATION_NAME =
            "TrainingProject";

    private final GoogleCalendarConnectionDAO connectionDAO;

    public GoogleCalendarService(
            GoogleCalendarConnectionDAO connectionDAO
    ) {
        this.connectionDAO = connectionDAO;
    }

    private Calendar getCalendar(Integer userId) {

        try {

            GoogleCalendarConnection connection =
                    connectionDAO.getByUserId(userId);

            GoogleCredential credential =
                    new GoogleCredential.Builder()
                            .setTransport(
                                    GoogleNetHttpTransport
                                            .newTrustedTransport()
                            )
                            .setJsonFactory(
                                    GsonFactory.getDefaultInstance()
                            )
                            .setClientSecrets(
                                    System.getenv("GOOGLE_CLIENT_ID"),
                                    System.getenv("GOOGLE_CLIENT_SECRET")
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

        if (userId == null) {
            throw new ApiException(401, "User must be logged in");
        }

        if (title == null || title.isBlank()) {
            throw new ApiException(400, "Event title is required");
        }

        if (start == null || end == null) {
            throw new ApiException(
                    400,
                    "Event start and end are required"
            );
        }

        try {

            Calendar calendar =
                    getCalendar(userId);

            Event event =
                    new Event()
                            .setSummary(title)
                            .setDescription(description);

            EventDateTime startDateTime =
                    new EventDateTime()
                            .setDateTime(
                                    new com.google.api.client.util.DateTime(
                                            Date.from(
                                                    start.toInstant()
                                            )
                                    )
                            )
                            .setTimeZone(
                                    start.getZone().getId()
                            );

            EventDateTime endDateTime =
                    new EventDateTime()
                            .setDateTime(
                                    new com.google.api.client.util.DateTime(
                                            Date.from(
                                                    end.toInstant()
                                            )
                                    )
                            )
                            .setTimeZone(
                                    end.getZone().getId()
                            );

            event.setStart(startDateTime);
            event.setEnd(endDateTime);

            Event createdEvent =
                    calendar.events()
                            .insert(
                                    "primary",
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

        try {

            Calendar calendar =
                    getCalendar(userId);

            Event event =
                    calendar.events()
                            .get("primary", eventId)
                            .execute();

            event.setSummary(title);
            event.setDescription(description);

            event.setStart(
                    new EventDateTime()
                            .setDateTime(
                                    new com.google.api.client.util.DateTime(
                                            Date.from(
                                                    start.toInstant()
                                            )
                                    )
                            )
                            .setTimeZone(
                                    start.getZone().getId()
                            )
            );

            event.setEnd(
                    new EventDateTime()
                            .setDateTime(
                                    new com.google.api.client.util.DateTime(
                                            Date.from(
                                                    end.toInstant()
                                            )
                                    )
                            )
                            .setTimeZone(
                                    end.getZone().getId()
                            )
            );

            calendar.events()
                    .update(
                            "primary",
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

        try {

            Calendar calendar =
                    getCalendar(userId);

            calendar.events()
                    .delete(
                            "primary",
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
}