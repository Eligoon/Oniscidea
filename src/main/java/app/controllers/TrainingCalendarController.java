
package app.controllers;

import app.dtos.TrainingCalendarDTO;
import app.services.TrainingCalendarService;
import io.javalin.http.Context;

import java.util.List;

public class TrainingCalendarController {

    private final TrainingCalendarService trainingCalendarService;

    public TrainingCalendarController(
            TrainingCalendarService trainingCalendarService
    ) {
        this.trainingCalendarService =
                trainingCalendarService;
    }

    public void create(Context ctx) {

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        TrainingCalendarDTO request =
                ctx.bodyAsClass(
                        TrainingCalendarDTO.class
                );

        TrainingCalendarDTO calendar =
                trainingCalendarService.createTrainingCalendar(
                        request.getName(),
                        request.getProfileId(),
                        authenticatedUserId
                );

        ctx.status(201).json(calendar);
    }

    public void getAll(Context ctx) {

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        List<TrainingCalendarDTO> calendars =
                trainingCalendarService
                        .getAllTrainingCalendars(
                                authenticatedUserId
                        );

        ctx.json(calendars);
    }

    public void getById(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        TrainingCalendarDTO calendar =
                trainingCalendarService
                        .getTrainingCalendar(
                                id,
                                authenticatedUserId
                        );

        ctx.json(calendar);
    }

    public void update(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        TrainingCalendarDTO request =
                ctx.bodyAsClass(
                        TrainingCalendarDTO.class
                );

        TrainingCalendarDTO calendar =
                trainingCalendarService
                        .updateTrainingCalendar(
                                id,
                                request.getName(),
                                authenticatedUserId
                        );

        ctx.json(calendar);
    }

    public void delete(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        trainingCalendarService
                .deleteTrainingCalendar(
                        id,
                        authenticatedUserId
                );

        ctx.status(204);
    }
}
