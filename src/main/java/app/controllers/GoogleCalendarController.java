package app.controllers;

import app.dtos.GoogleCalendarEventDTO;
import app.services.GoogleCalendarService;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;

public class GoogleCalendarController {

    private final GoogleCalendarService googleCalendarService;

    public GoogleCalendarController(
            GoogleCalendarService googleCalendarService
    ) {
        this.googleCalendarService = googleCalendarService;
    }

    public void createEvent(Context ctx) {
        Integer userId = getAuthenticatedUserId(ctx);

        GoogleCalendarEventDTO dto =
                ctx.bodyAsClass(GoogleCalendarEventDTO.class);

        String eventId = googleCalendarService.createEvent(
                userId,
                dto.getTitle(),
                dto.getDescription(),
                dto.getStart(),
                dto.getEnd()
        );

        ctx.status(201).json(eventId);
    }

    public void updateEvent(Context ctx) {
        Integer userId = getAuthenticatedUserId(ctx);
        String eventId = ctx.pathParam("eventId");

        GoogleCalendarEventDTO dto =
                ctx.bodyAsClass(GoogleCalendarEventDTO.class);

        googleCalendarService.updateEvent(
                userId,
                eventId,
                dto.getTitle(),
                dto.getDescription(),
                dto.getStart(),
                dto.getEnd()
        );

        ctx.status(204);
    }

    public void deleteEvent(Context ctx) {
        Integer userId = getAuthenticatedUserId(ctx);
        String eventId = ctx.pathParam("eventId");

        googleCalendarService.deleteEvent(userId, eventId);

        ctx.status(204);
    }

    private Integer getAuthenticatedUserId(Context ctx) {
        Integer userId = ctx.attribute("authenticatedUserId");

        if (userId == null) {
            throw new UnauthorizedResponse("User must be logged in");
        }

        return userId;
    }
}
