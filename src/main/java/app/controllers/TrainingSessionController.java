package app.controllers;

import app.dtos.TrainingSessionDTO;
import app.services.TrainingSessionService;
import io.javalin.http.Context;

import java.util.List;

public class TrainingSessionController {

    private final TrainingSessionService trainingSessionService;

    public TrainingSessionController(
            TrainingSessionService trainingSessionService
    ) {
        this.trainingSessionService = trainingSessionService;
    }

    public void create(Context ctx) {

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        TrainingSessionDTO request =
                ctx.bodyAsClass(TrainingSessionDTO.class);

        TrainingSessionDTO session =
                trainingSessionService.createTrainingSession(
                        request.getDate(),
                        request.getStartTime(),
                        request.getEndTime(),
                        request.getCalendarId(),
                        request.getTrainingProgramId(),
                        authenticatedUserId
                );

        ctx.status(201).json(session);
    }

    public void getAll(Context ctx) {

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        List<TrainingSessionDTO> sessions =
                trainingSessionService.getAllTrainingSessions(
                        authenticatedUserId
                );

        ctx.json(sessions);
    }

    public void getById(Context ctx) {

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        TrainingSessionDTO session =
                trainingSessionService.getTrainingSession(
                        id,
                        authenticatedUserId
                );

        ctx.json(session);
    }

    public void update(Context ctx) {

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        TrainingSessionDTO request =
                ctx.bodyAsClass(TrainingSessionDTO.class);

        TrainingSessionDTO session =
                trainingSessionService.updateTrainingSession(
                        id,
                        request.getDate(),
                        request.getStartTime(),
                        request.getEndTime(),
                        request.getCalendarId(),
                        request.getTrainingProgramId(),
                        request.isCompleted(),
                        request.getNotes(),
                        authenticatedUserId
                );

        ctx.json(session);
    }

    public void delete(Context ctx) {

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        trainingSessionService.deleteTrainingSession(
                id,
                authenticatedUserId
        );

        ctx.status(204);
    }
}