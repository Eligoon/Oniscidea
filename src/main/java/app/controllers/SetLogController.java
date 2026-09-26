package app.controllers;

import app.dtos.SetLogDTO;
import app.services.SetLogService;
import io.javalin.http.Context;

public class SetLogController {

    private final SetLogService setLogService;

    public SetLogController(
            SetLogService setLogService
    ) {
        this.setLogService = setLogService;
    }

    public void create(Context ctx) {

        Integer exerciseLogId =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        SetLogDTO request =
                ctx.bodyAsClass(
                        SetLogDTO.class
                );

        SetLogDTO setLog =
                setLogService.createSetLog(
                        exerciseLogId,
                        request.getSetNumber(),
                        request.getWeight(),
                        request.getReps(),
                        request.getRir(),
                        request.isCompleted()
                );

        ctx.status(201).json(setLog);
    }

    public void getById(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        SetLogDTO setLog =
                setLogService.getSetLog(id);

        ctx.json(setLog);
    }
}