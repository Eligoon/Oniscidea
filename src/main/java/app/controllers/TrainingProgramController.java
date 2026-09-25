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

        TrainingProgramDTO request =
                ctx.bodyAsClass(TrainingProgramDTO.class);

        TrainingProgramDTO program =
                trainingProgramService.createTrainingProgram(
                        request.getName(),
                        request.getDescription(),
                        request.getProfileId()
                );

        ctx.status(201).json(program);
    }

    public void getAll(Context ctx) {

        List<TrainingProgramDTO> programs =
                trainingProgramService.getAllTrainingPrograms();

        ctx.json(programs);
    }

    public void getById(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        TrainingProgramDTO program =
                trainingProgramService.getTrainingProgram(id);

        ctx.json(program);
    }

    public void update(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        TrainingProgramDTO request =
                ctx.bodyAsClass(TrainingProgramDTO.class);

        TrainingProgramDTO program =
                trainingProgramService.updateTrainingProgram(
                        id,
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

        trainingProgramService.deleteTrainingProgram(id);

        ctx.status(204);
    }
}