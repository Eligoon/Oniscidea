package app.controllers;

import app.dtos.ExerciseDTO;
import app.services.ExerciseService;
import io.javalin.http.Context;

import java.util.List;

public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(
            ExerciseService exerciseService
    ) {
        this.exerciseService = exerciseService;
    }

    public void getAll(Context ctx) {

        List<ExerciseDTO> exercises =
                exerciseService.getAllExercises();

        ctx.json(exercises);
    }

    public void getById(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        ExerciseDTO exercise =
                exerciseService.getExercise(id);

        ctx.json(exercise);
    }
}