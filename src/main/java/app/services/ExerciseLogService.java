package app.services;

import app.daos.ExerciseDAO;
import app.daos.ExerciseLogDAO;
import app.daos.ProfileDAO;
import app.daos.TrainingCalendarDAO;
import app.daos.TrainingSessionDAO;
import app.dtos.ExerciseLogDTO;
import app.entities.Exercise;
import app.entities.ExerciseLog;
import app.entities.Profile;
import app.entities.TrainingCalendar;
import app.entities.TrainingSession;
import app.exceptions.ApiException;
import app.exceptions.ValidationException;

public class ExerciseLogService {

    private final ExerciseLogDAO exerciseLogDAO;
    private final TrainingSessionDAO trainingSessionDAO;
    private final ExerciseDAO exerciseDAO;
    private final TrainingCalendarDAO trainingCalendarDAO;
    private final ProfileDAO profileDAO;

    public ExerciseLogService(
            ExerciseLogDAO exerciseLogDAO,
            TrainingSessionDAO trainingSessionDAO,
            ExerciseDAO exerciseDAO,
            TrainingCalendarDAO trainingCalendarDAO,
            ProfileDAO profileDAO
    ) {
        this.exerciseLogDAO = exerciseLogDAO;
        this.trainingSessionDAO = trainingSessionDAO;
        this.exerciseDAO = exerciseDAO;
        this.trainingCalendarDAO = trainingCalendarDAO;
        this.profileDAO = profileDAO;
    }

    public ExerciseLogDTO createExerciseLog(
            Integer trainingSessionId,
            Integer exerciseId,
            Integer authenticatedUserId
    ) {
        requireAuthenticatedUser(authenticatedUserId);

        if (trainingSessionId == null) {
            throw new ValidationException(
                    "Training session id is required"
            );
        }

        if (exerciseId == null) {
            throw new ValidationException(
                    "Exercise id is required"
            );
        }

        TrainingSession trainingSession =
                trainingSessionDAO.getById(trainingSessionId);

        ensureSessionOwnership(
                trainingSession,
                authenticatedUserId
        );

        Exercise exercise =
                exerciseDAO.getById(exerciseId);

        ExerciseLog exerciseLog =
                ExerciseLog.builder()
                        .trainingSession(trainingSession)
                        .exercise(exercise)
                        .build();

        ExerciseLog created =
                exerciseLogDAO.create(exerciseLog);

        return toDTO(created);
    }

    public ExerciseLogDTO getExerciseLog(
            Integer id,
            Integer authenticatedUserId
    ) {
        requireAuthenticatedUser(authenticatedUserId);

        if (id == null) {
            throw new ValidationException(
                    "Exercise log id is required"
            );
        }

        ExerciseLog exerciseLog =
                exerciseLogDAO.getById(id);

        ensureSessionOwnership(
                exerciseLog.getTrainingSession(),
                authenticatedUserId
        );

        return toDTO(exerciseLog);
    }

    private void ensureSessionOwnership(
            TrainingSession session,
            Integer authenticatedUserId
    ) {
        TrainingCalendar calendar =
                trainingCalendarDAO.getById(
                        session.getCalendar().getId()
                );

        Profile profile =
                profileDAO.getById(
                        calendar.getProfile().getId()
                );

        if (!profile.getUser().getId()
                .equals(authenticatedUserId)) {
            throw new ApiException(
                    403,
                    "You do not have access to this exercise log"
            );
        }
    }

    private void requireAuthenticatedUser(
            Integer authenticatedUserId
    ) {
        if (authenticatedUserId == null) {
            throw new ApiException(
                    401,
                    "Authentication is required"
            );
        }
    }

    private ExerciseLogDTO toDTO(
            ExerciseLog exerciseLog
    ) {
        return new ExerciseLogDTO(
                exerciseLog.getId(),
                exerciseLog.getTrainingSession().getId(),
                exerciseLog.getExercise().getId()
        );
    }
}