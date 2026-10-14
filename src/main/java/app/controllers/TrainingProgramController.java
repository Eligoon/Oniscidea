
package app.controllers;

import app.dtos.TrainingProgramDTO;
import app.services.TrainingProgramService;
import io.javalin.http.Context;

import java.util.List;

public class TrainingProgramController {

    private final TrainingProgramService trainingProgramService;

    public TrainingProgramController(
            TrainingProgramService trainingProgramService
    ) {
        this.trainingProgramService = trainingProgramService;
    }

    public void create(Context ctx) {

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        TrainingProgramDTO request =
                ctx.bodyAsClass(TrainingProgramDTO.class);

        TrainingProgramDTO program =
                trainingProgramService.createTrainingProgram(
                        request.getName(),
                        request.getDescription(),
                        request.getProfileId(),
                        authenticatedUserId
                );

        ctx.status(201).json(program);
    }

    public void getAll(Context ctx) {

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        List<TrainingProgramDTO> programs =
                trainingProgramService.getAllTrainingPrograms(
                        authenticatedUserId
                );

        ctx.json(programs);
    }

    public void getById(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        TrainingProgramDTO program =
                trainingProgramService.getTrainingProgram(
                        id,
                        authenticatedUserId
                );

        ctx.json(program);
    }

    public void update(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        TrainingProgramDTO request =
                ctx.bodyAsClass(TrainingProgramDTO.class);

        TrainingProgramDTO program =
                trainingProgramService.updateTrainingProgram(
                        id,
                        authenticatedUserId,
                        request.getName(),
                        request.getDescription()
                );

        ctx.json(program);
    }

    public void delete(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        trainingProgramService.deleteTrainingProgram(
                id,
                authenticatedUserId
        );

        ctx.status(204);
    }
}
