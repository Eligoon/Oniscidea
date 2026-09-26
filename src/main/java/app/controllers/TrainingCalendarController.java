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

        TrainingCalendarDTO request =
                ctx.bodyAsClass(
                        TrainingCalendarDTO.class
                );

        TrainingCalendarDTO calendar =
                trainingCalendarService.createTrainingCalendar(
                        request.getName(),
                        request.getProfileId()
                );

        ctx.status(201).json(calendar);
    }

    public void getAll(Context ctx) {

        List<TrainingCalendarDTO> calendars =
                trainingCalendarService
                        .getAllTrainingCalendars();

        ctx.json(calendars);
    }

    public void getById(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        TrainingCalendarDTO calendar =
                trainingCalendarService
                        .getTrainingCalendar(id);

        ctx.json(calendar);
    }

    public void update(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        TrainingCalendarDTO request =
                ctx.bodyAsClass(
                        TrainingCalendarDTO.class
                );

        TrainingCalendarDTO calendar =
                trainingCalendarService
                        .updateTrainingCalendar(
                                id,
                                request.getName()
                        );

        ctx.json(calendar);
    }

    public void delete(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        trainingCalendarService
                .deleteTrainingCalendar(id);

        ctx.status(204);
    }
}