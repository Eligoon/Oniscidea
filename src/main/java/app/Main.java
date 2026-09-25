package app;

import app.config.HibernateConfig;
import app.controllers.ProfileController;
import app.controllers.UserController;
import app.daos.ProfileDAO;
import app.daos.UserDAO;
import app.dtos.ErrorResponseDTO;
import app.exceptions.ApiException;
import app.services.ProfileService;
import app.services.UserService;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;
import app.controllers.TrainingProgramController;
import app.daos.TrainingProgramDAO;
import app.services.TrainingProgramService;

public class Main {

    public static void main(String[] args) {

        EntityManagerFactory emf =
                HibernateConfig.getEntityManagerFactory();

        // User
        UserDAO userDAO =
                new UserDAO(emf);

        UserService userService =
                new UserService(userDAO);

        UserController userController =
                new UserController(userService);

        // Profile
        ProfileDAO profileDAO =
                new ProfileDAO(emf);

        ProfileService profileService =
                new ProfileService(
                        profileDAO,
                        userDAO
                );

        ProfileController profileController =
                new ProfileController(profileService);

        // TrainingProgram
        TrainingProgramDAO trainingProgramDAO =
                new TrainingProgramDAO(emf);

        TrainingProgramService trainingProgramService =
                new TrainingProgramService(
                        trainingProgramDAO,
                        profileDAO
                );

        TrainingProgramController trainingProgramController =
                new TrainingProgramController(
                        trainingProgramService
                );

        Javalin app =
                Javalin.create(config -> {

                    // Error handling
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

                    // Home
                    config.routes.get(
                            "/",
                            ctx -> ctx.result(
                                    "Training API is running"
                            )
                    );

                    // User routes
                    config.routes.post(
                            "/api/users",
                            userController::create
                    );

                    config.routes.get(
                            "/api/users",
                            userController::getAll
                    );

                    config.routes.get(
                            "/api/users/{id}",
                            userController::getById
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

                    // Profile routes
                    config.routes.post(
                            "/api/profiles",
                            profileController::create
                    );

                    config.routes.get(
                            "/api/profiles",
                            profileController::getAll
                    );

                    config.routes.get(
                            "/api/profiles/{id}",
                            profileController::getById
                    );

                    config.routes.put(
                            "/api/profiles/{id}",
                            profileController::update
                    );

                    config.routes.delete(
                            "/api/profiles/{id}",
                            profileController::delete
                    );

                    // TrainingProgram routes

                    config.routes.post(
                            "/api/training-programs",
                            trainingProgramController::create
                    );

                    config.routes.get(
                            "/api/training-programs",
                            trainingProgramController::getAll
                    );

                    config.routes.get(
                            "/api/training-programs/{id}",
                            trainingProgramController::getById
                    );

                    config.routes.put(
                            "/api/training-programs/{id}",
                            trainingProgramController::update
                    );

                    config.routes.delete(
                            "/api/training-programs/{id}",
                            trainingProgramController::delete
                    );

                }).start(7070);
    }
}