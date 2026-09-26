package app.controllers;

import app.services.ExerciseBodyPartService;
import io.javalin.http.Context;

public class ExerciseBodyPartController {

    private final ExerciseBodyPartService exerciseBodyPartService;

    public ExerciseBodyPartController(
            ExerciseBodyPartService exerciseBodyPartService
    ) {
        this.exerciseBodyPartService = exerciseBodyPartService;
    }

    public void addBodyPart(Context ctx) {

        Integer exerciseId =
                Integer.parseInt(ctx.pathParam("id"));

        Integer bodyPartId =
                Integer.parseInt(ctx.pathParam("bodyPartId"));

        exerciseBodyPartService.addBodyPart(
                exerciseId,
                bodyPartId
        );

        ctx.status(204);
    }

    public void removeBodyPart(Context ctx) {

        Integer exerciseId =
                Integer.parseInt(ctx.pathParam("id"));

        Integer bodyPartId =
                Integer.parseInt(ctx.pathParam("bodyPartId"));

        exerciseBodyPartService.removeBodyPart(
                exerciseId,
                bodyPartId
        );

        ctx.status(204);
    }
}