package app;



import app.config.HibernateConfig;
import app.controllers.UserController;
import app.daos.UserDAO;
import app.dtos.UserDTO;
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


                }).start(7070);
        }
    }

