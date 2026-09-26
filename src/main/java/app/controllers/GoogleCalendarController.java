package app.controllers;

import app.dtos.GoogleCalendarEventDTO;
import app.services.GoogleCalendarService;
import io.javalin.http.Context;

public class GoogleCalendarController {

    private final GoogleCalendarService googleCalendarService;

    public GoogleCalendarController(
            GoogleCalendarService googleCalendarService
    ) {
        this.googleCalendarService =
                googleCalendarService;
    }

    public void createEvent(Context ctx) {

        GoogleCalendarEventDTO dto =
                ctx.bodyAsClass(
                        GoogleCalendarEventDTO.class
                );

        String eventId =
                googleCalendarService.createEvent(
                        dto.getUserId(),
                        dto.getTitle(),
                        dto.getDescription(),
                        dto.getStart(),
                        dto.getEnd()
                );

        ctx.status(201);
        ctx.json(eventId);
    }

    public void updateEvent(Context ctx) {

        String eventId =
                ctx.pathParam("eventId");

        GoogleCalendarEventDTO dto =
                ctx.bodyAsClass(
                        GoogleCalendarEventDTO.class
                );

        googleCalendarService.updateEvent(
                dto.getUserId(),
                eventId,
                dto.getTitle(),
                dto.getDescription(),
                dto.getStart(),
                dto.getEnd()
        );

        ctx.status(204);
    }

    public void deleteEvent(Context ctx) {

        String eventId =
                ctx.pathParam("eventId");

        Integer userId =
                Integer.parseInt(
                        ctx.queryParam("userId")
                );

        googleCalendarService.deleteEvent(
                userId,
                eventId
        );

        ctx.status(204);
    }
}