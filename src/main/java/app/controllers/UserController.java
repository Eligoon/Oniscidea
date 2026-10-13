
package app.controllers;

import app.dtos.LoginResponseDTO;
import app.dtos.UserDTO;
import app.services.JwtService;
import app.services.UserService;
import io.javalin.http.Context;

import java.util.List;

public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(
            UserService userService,
            JwtService jwtService
    ) {
        this.userService = userService;
        this.jwtService = jwtService;
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

    public void getAll(Context ctx) {

        List<UserDTO> users =
                userService.getAllUsers();

        ctx.json(users);
    }

    public void update(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        UserDTO request =
                ctx.bodyAsClass(UserDTO.class);

        UserDTO user =
                userService.updateUser(
                        id,
                        request.getName(),
                        request.getEmail(),
                        request.getPassword()
                );

        ctx.json(user);
    }

    public void delete(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        userService.deleteUser(id);

        ctx.status(204);
    }

    public void login(Context ctx) {

        UserDTO request =
                ctx.bodyAsClass(UserDTO.class);

        UserDTO user =
                userService.login(
                        request.getEmail(),
                        request.getPassword()
                );

        String token =
                jwtService.generateToken(
                        user.getId(),
                        user.getEmail()
                );

        LoginResponseDTO response =
                new LoginResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        token
                );

        ctx.json(response);
    }
}
