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
import app.controllers.TrainingProgramExerciseController;
import app.daos.ExerciseDAO;
import app.daos.TrainingProgramExerciseDAO;
import app.services.TrainingProgramExerciseService;
import app.controllers.TrainingSessionController;
import app.daos.TrainingCalendarDAO;
import app.daos.TrainingSessionDAO;
import app.services.TrainingSessionService;
import app.controllers.TrainingCalendarController;
import app.services.TrainingCalendarService;
import app.controllers.ExerciseController;
import app.services.ExerciseService;
import app.controllers.ExerciseLogController;
import app.controllers.SetLogController;
import app.daos.ExerciseLogDAO;
import app.daos.SetLogDAO;
import app.services.ExerciseLogService;
import app.services.SetLogService;
import app.controllers.NoteController;
import app.daos.NoteDAO;
import app.services.NoteService;

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

        // TrainingProgramExercise
        TrainingProgramExerciseDAO trainingProgramExerciseDAO =
                new TrainingProgramExerciseDAO(emf);

        ExerciseDAO exerciseDAO =
                new ExerciseDAO(emf);

        TrainingProgramExerciseService trainingProgramExerciseService =
                new TrainingProgramExerciseService(
                        trainingProgramExerciseDAO,
                        trainingProgramDAO,
                        exerciseDAO
                );

        TrainingProgramExerciseController trainingProgramExerciseController =
                new TrainingProgramExerciseController(
                        trainingProgramExerciseService
                );

        ExerciseService exerciseService =
                new ExerciseService(exerciseDAO);

        ExerciseController exerciseController =
                new ExerciseController(exerciseService);

        // TrainingSession

        TrainingSessionDAO trainingSessionDAO =
                new TrainingSessionDAO(emf);

        TrainingCalendarDAO trainingCalendarDAO =
                new TrainingCalendarDAO(emf);

        TrainingSessionService trainingSessionService =
                new TrainingSessionService(
                        trainingSessionDAO,
                        trainingCalendarDAO,
                        trainingProgramDAO
                );

        TrainingSessionController trainingSessionController =
                new TrainingSessionController(
                        trainingSessionService
                );

        // TrainingCalendar

        TrainingCalendarService trainingCalendarService =
                new TrainingCalendarService(
                        trainingCalendarDAO,
                        profileDAO
                );

        TrainingCalendarController trainingCalendarController =
                new TrainingCalendarController(
                        trainingCalendarService
                );

        // Logs

        ExerciseLogDAO exerciseLogDAO =
                new ExerciseLogDAO(emf);

        SetLogDAO setLogDAO =
                new SetLogDAO(emf);

        ExerciseLogService exerciseLogService =
                new ExerciseLogService(
                        exerciseLogDAO,
                        trainingSessionDAO,
                        exerciseDAO
                );

        SetLogService setLogService =
                new SetLogService(
                        setLogDAO,
                        exerciseLogDAO
                );

        ExerciseLogController exerciseLogController =
                new ExerciseLogController(
                        exerciseLogService
                );

        SetLogController setLogController =
                new SetLogController(
                        setLogService
                );

        // Note

        NoteDAO noteDAO =
                new NoteDAO(emf);

        NoteService noteService =
                new NoteService(
                        noteDAO,
                        profileDAO
                );

        NoteController noteController =
                new NoteController(
                        noteService
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

                    // Training program exercises routes
                    config.routes.post(
                            "/api/training-programs/{id}/exercises",
                            trainingProgramExerciseController::addExercise
                    );

                    config.routes.delete(
                            "/api/training-programs/{id}/exercises/{exerciseId}",
                            trainingProgramExerciseController::deleteExercise
                    );

                    // Training sessions
                    config.routes.post(
                            "/api/training-sessions",
                            trainingSessionController::create
                    );

                    config.routes.get(
                            "/api/training-sessions",
                            trainingSessionController::getAll
                    );

                    config.routes.get(
                            "/api/training-sessions/{id}",
                            trainingSessionController::getById
                    );

                    config.routes.put(
                            "/api/training-sessions/{id}",
                            trainingSessionController::update
                    );

                    config.routes.delete(
                            "/api/training-sessions/{id}",
                            trainingSessionController::delete
                    );

                    // Training calendars
                    config.routes.post(
                            "/api/calendars",
                            trainingCalendarController::create
                    );

                    config.routes.get(
                            "/api/calendars",
                            trainingCalendarController::getAll
                    );

                    config.routes.get(
                            "/api/calendars/{id}",
                            trainingCalendarController::getById
                    );

                    config.routes.put(
                            "/api/calendars/{id}",
                            trainingCalendarController::update
                    );

                    config.routes.delete(
                            "/api/calendars/{id}",
                            trainingCalendarController::delete
                    );

                    // Exercises
                    config.routes.get(
                            "/api/exercises",
                            exerciseController::getAll
                    );

                    config.routes.get(
                            "/api/exercises/{id}",
                            exerciseController::getById
                    );

                    // Exercise logs
                    config.routes.post(
                            "/api/training-sessions/{id}/logs",
                            exerciseLogController::create
                    );

                    config.routes.get(
                            "/api/exercise-logs/{id}",
                            exerciseLogController::getById
                    );

                    // Set logs
                    config.routes.post(
                            "/api/exercise-logs/{id}/sets",
                            setLogController::create
                    );

                    config.routes.get(
                            "/api/set-logs/{id}",
                            setLogController::getById
                    );

                    // Notes
                    config.routes.post(
                            "/api/notes",
                            noteController::create
                    );

                    config.routes.get(
                            "/api/notes",
                            noteController::getAll
                    );

                    config.routes.get(
                            "/api/notes/{id}",
                            noteController::getById
                    );

                    config.routes.put(
                            "/api/notes/{id}",
                            noteController::update
                    );

                    config.routes.delete(
                            "/api/notes/{id}",
                            noteController::delete
                    );

                }).start(7070);
    }
}