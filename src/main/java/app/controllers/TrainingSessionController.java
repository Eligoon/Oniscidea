package app.controllers;

import app.dtos.TrainingSessionDTO;
import app.services.TrainingSessionService;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.util.List;

public class TrainingSessionController {

    private final TrainingSessionService trainingSessionService;

    public TrainingSessionController(
            TrainingSessionService trainingSessionService
    ) {
        this.trainingSessionService = trainingSessionService;
    }

    public void create(Context ctx) {

        TrainingSessionDTO request =
                ctx.bodyAsClass(TrainingSessionDTO.class);

        TrainingSessionDTO session =
                trainingSessionService.createTrainingSession(
                        request.getDate(),
                        request.getCalendarId(),
                        request.getTrainingProgramId()
                );

        ctx.status(201).json(session);
    }

    public void getAll(Context ctx) {

        List<TrainingSessionDTO> sessions =
                trainingSessionService.getAllTrainingSessions();

        ctx.json(sessions);
    }

    public void getById(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        TrainingSessionDTO session =
                trainingSessionService.getTrainingSession(id);

        ctx.json(session);
    }

    public void update(Context ctx) {

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
                        request.getCalendarId(),
                        request.getTrainingProgramId(),
                        request.isCompleted(),
                        request.getNotes()
                );

        ctx.json(session);
    }

    public void delete(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        trainingSessionService.deleteTrainingSession(id);

        ctx.status(204);
    }
}