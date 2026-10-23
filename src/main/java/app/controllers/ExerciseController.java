package app.controllers;

import app.dtos.CreateExerciseDTO;
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

    public void create(Context ctx) {
        CreateExerciseDTO dto =
                ctx.bodyAsClass(CreateExerciseDTO.class);

        Integer userId = ctx.attribute("authenticatedUserId");

        ExerciseDTO exercise =
                exerciseService.createExercise(dto, userId);

        ctx.status(201);
        ctx.json(exercise);
    }

    public void getAll(Context ctx) {
        Integer userId = ctx.attribute("authenticatedUserId");

        List<ExerciseDTO> exercises =
                exerciseService.getAllExercises(userId);

        ctx.json(exercises);
    }

    public void getById(Context ctx) {
        Integer id =
                Integer.parseInt(ctx.pathParam("id"));

        ExerciseDTO exercise =
                exerciseService.getExercise(id);

        ctx.json(exercise);
    }

    public void update(Context ctx) {
        Integer id = Integer.parseInt(ctx.pathParam("id"));

        Integer userId = ctx.attribute("authenticatedUserId");

        CreateExerciseDTO dto =
                ctx.bodyAsClass(CreateExerciseDTO.class);

        ExerciseDTO exercise =
                exerciseService.updateExercise(id, dto, userId);

        ctx.json(exercise);
    }

    public void delete(Context ctx) {
        Integer id = Integer.parseInt(ctx.pathParam("id"));

        Integer userId = ctx.attribute("authenticatedUserId");

        exerciseService.deleteExercise(id, userId);

        ctx.status(204);
    }
}