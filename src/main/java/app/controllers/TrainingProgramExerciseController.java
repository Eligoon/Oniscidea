package app.controllers;

import app.dtos.TrainingProgramExerciseDTO;
import app.services.TrainingProgramExerciseService;
import io.javalin.http.Context;

public class TrainingProgramExerciseController {

    private final TrainingProgramExerciseService
            trainingProgramExerciseService;

    public TrainingProgramExerciseController(
            TrainingProgramExerciseService trainingProgramExerciseService
    ) {
        this.trainingProgramExerciseService =
                trainingProgramExerciseService;
    }

    public void addExercise(Context ctx) {

        Integer trainingProgramId =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        TrainingProgramExerciseDTO request =
                ctx.bodyAsClass(
                        TrainingProgramExerciseDTO.class
                );

        TrainingProgramExerciseDTO result =
                trainingProgramExerciseService.addExercise(
                        trainingProgramId,
                        request.getExerciseId(),
                        request.getExerciseOrder(),
                        request.getPlannedSets(),
                        request.getPlannedReps(),
                        request.getRestSeconds()
                );

        ctx.status(201).json(result);
    }

    public void deleteExercise(Context ctx) {

        Integer trainingProgramId =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        Integer exerciseId =
                Integer.parseInt(
                        ctx.pathParam("exerciseId")
                );

        trainingProgramExerciseService.deleteExercise(
                trainingProgramId,
                exerciseId
        );

        ctx.status(204);
    }
}