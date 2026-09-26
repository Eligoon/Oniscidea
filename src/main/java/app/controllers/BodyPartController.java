package app.controllers;

import app.dtos.BodyPartDTO;
import app.services.BodyPartService;
import io.javalin.http.Context;

import java.util.List;

public class BodyPartController {

    private final BodyPartService bodyPartService;

    public BodyPartController(
            BodyPartService bodyPartService
    ) {
        this.bodyPartService = bodyPartService;
    }

    public void create(Context ctx) {

        BodyPartDTO request =
                ctx.bodyAsClass(
                        BodyPartDTO.class
                );

        BodyPartDTO bodyPart =
                bodyPartService.createBodyPart(
                        request.getName(),
                        request.getColor()
                );

        ctx.status(201).json(bodyPart);
    }

    public void getAll(Context ctx) {

        List<BodyPartDTO> bodyParts =
                bodyPartService.getAllBodyParts();

        ctx.json(bodyParts);
    }

    public void getById(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        BodyPartDTO bodyPart =
                bodyPartService.getBodyPart(id);

        ctx.json(bodyPart);
    }

    public void update(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        BodyPartDTO request =
                ctx.bodyAsClass(
                        BodyPartDTO.class
                );

        BodyPartDTO bodyPart =
                bodyPartService.updateBodyPart(
                        id,
                        request.getName(),
                        request.getColor()
                );

        ctx.json(bodyPart);
    }

    public void delete(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        bodyPartService.deleteBodyPart(id);

        ctx.status(204);
    }
}