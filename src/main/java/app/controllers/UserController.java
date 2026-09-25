package app.controllers;

import app.dtos.UserDTO;
import app.services.UserService;
import io.javalin.http.Context;

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void create(Context ctx) {

        UserDTO request =
                ctx.bodyAsClass(UserDTO.class);

        UserDTO user =
                userService.createUser(
                        request.getName(),
                        request.getEmail(),
                        request.getPassword()
                );

        ctx.status(201).json(user);
    }

    public void getById(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        UserDTO user =
                userService.getUser(id);

        ctx.json(user);
    }
}