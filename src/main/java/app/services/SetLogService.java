
package app.services;

import app.daos.ExerciseLogDAO;
import app.daos.ProfileDAO;
import app.daos.SetLogDAO;
import app.daos.TrainingCalendarDAO;
import app.daos.TrainingSessionDAO;
import app.dtos.SetLogDTO;
import app.entities.ExerciseLog;
import app.entities.Profile;
import app.entities.SetLog;
import app.entities.TrainingCalendar;
import app.entities.TrainingSession;
import app.exceptions.ApiException;
import app.exceptions.ValidationException;

public class SetLogService {

    private final SetLogDAO setLogDAO;
    private final ExerciseLogDAO exerciseLogDAO;
    private final TrainingSessionDAO trainingSessionDAO;
    private final TrainingCalendarDAO trainingCalendarDAO;
    private final ProfileDAO profileDAO;

    public SetLogService(
            SetLogDAO setLogDAO,
            ExerciseLogDAO exerciseLogDAO,
            TrainingSessionDAO trainingSessionDAO,
            TrainingCalendarDAO trainingCalendarDAO,
            ProfileDAO profileDAO
    ) {
        this.setLogDAO = setLogDAO;
        this.exerciseLogDAO = exerciseLogDAO;
        this.trainingSessionDAO = trainingSessionDAO;
        this.trainingCalendarDAO = trainingCalendarDAO;
        this.profileDAO = profileDAO;
    }

    public SetLogDTO createSetLog(
            Integer exerciseLogId,
            Integer setNumber,
            Double weight,
            Integer reps,
            Integer rir,
            boolean completed,
            Integer authenticatedUserId
    ) {
        requireAuthenticatedUser(authenticatedUserId);

        if (exerciseLogId == null) {
            throw new ValidationException(
                    "Exercise log id is required"
            );
        }

        if (setNumber == null || setNumber < 1) {
            throw new ValidationException(
                    "Set number must be greater than 0"
            );
        }

        if (reps != null && reps < 0) {
            throw new ValidationException(
                    "Reps cannot be negative"
            );
        }

        if (weight != null && weight < 0) {
            throw new ValidationException(
                    "Weight cannot be negative"
            );
        }

        if (rir != null && rir < 0) {
            throw new ValidationException(
                    "RIR cannot be negative"
            );
        }

        ExerciseLog exerciseLog =
                exerciseLogDAO.getById(exerciseLogId);

        TrainingSession trainingSession =
                trainingSessionDAO.getById(
                        exerciseLog.getTrainingSession().getId()
                );

        ensureSessionOwnership(
                trainingSession,
                authenticatedUserId
        );

        SetLog setLog =
                new SetLog(
                        exerciseLog,
                        setNumber,
                        weight,
                        reps,
                        rir,
                        completed
                );

        SetLog created =
                setLogDAO.create(setLog);

        return toDTO(created);
    }

    public SetLogDTO getSetLog(
            Integer id,
            Integer authenticatedUserId
    ) {
        requireAuthenticatedUser(authenticatedUserId);

        if (id == null) {
            throw new ValidationException(
                    "Set log id is required"
            );
        }

        SetLog setLog =
                setLogDAO.getById(id);

        ExerciseLog exerciseLog =
                exerciseLogDAO.getById(
                        setLog.getExerciseLog().getId()
                );

        TrainingSession trainingSession =
                trainingSessionDAO.getById(
                        exerciseLog.getTrainingSession().getId()
                );

        ensureSessionOwnership(
                trainingSession,
                authenticatedUserId
        );

        return toDTO(setLog);
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
                    "You do not have access to this set log"
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

    private SetLogDTO toDTO(SetLog setLog) {
        return new SetLogDTO(
                setLog.getId(),
                setLog.getExerciseLog().getId(),
                setLog.getSetNumber(),
                setLog.getWeight(),
                setLog.getReps(),
                setLog.getRir(),
                setLog.isCompleted()
        );
    }
}
