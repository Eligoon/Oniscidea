package app;



import app.config.HibernateConfig;
import app.controllers.UserController;
import app.daos.UserDAO;
import app.dtos.ErrorResponseDTO;
import app.dtos.UserDTO;
import app.exceptions.ApiException;
import app.services.UserService;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {

        EntityManagerFactory emf =
                HibernateConfig.getEntityManagerFactory();

        UserDAO userDAO =
                new UserDAO(emf);

        UserService userService =
                new UserService(userDAO);

        UserController userController =
                new UserController(userService);

        Javalin app =
                Javalin.create(config -> {

                    config.routes.exception(
                            ApiException.class,
                            (e, ctx) -> {
                                ctx.status(e.getCode());

                                ctx.json(
                                        new ErrorResponseDTO(
                                                e.getCode(),
                                                e.getMessage()
                                        )
                                );
                            }
                    );

                    config.routes.get(
                            "/",
                            ctx -> ctx.result(
                                    "Training API is running"
                            )
                    );

                    config.routes.post(
                            "/api/users",
                            userController::create
                    );

                    config.routes.get(
                            "/api/users/{id}",
                            userController::getById
                    );

                    config.routes.get(
                            "/api/users",
                            userController::getAll
                    );

                    config.routes.put(
                            "/api/users/{id}",
                            userController::update
                    );

                    config.routes.delete(
                            "/api/users/{id}",
                            userController::delete
                    );

                    config.routes.post(
                            "/api/login",
                            userController::login
                    );


                }).start(7070);
        }
    }

