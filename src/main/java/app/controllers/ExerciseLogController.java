package app.controllers;

import app.dtos.ExerciseLogDTO;
import app.services.ExerciseLogService;
import io.javalin.http.Context;

public class ExerciseLogController {

    private final ExerciseLogService exerciseLogService;

    public ExerciseLogController(
            ExerciseLogService exerciseLogService
    ) {
        this.exerciseLogService = exerciseLogService;
    }

    public void create(Context ctx) {

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        Integer trainingSessionId =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        ExerciseLogDTO request =
                ctx.bodyAsClass(
                        ExerciseLogDTO.class
                );

        ExerciseLogDTO exerciseLog =
                exerciseLogService.createExerciseLog(
                        trainingSessionId,
                        request.getExerciseId(),
                        authenticatedUserId
                );

        ctx.status(201).json(exerciseLog);
    }

    public void getById(Context ctx) {

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        ExerciseLogDTO exerciseLog =
                exerciseLogService.getExerciseLog(
                        id,
                        authenticatedUserId
                );

        ctx.json(exerciseLog);
    }
}