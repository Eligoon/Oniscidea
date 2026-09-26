package app.services;

import app.daos.ExerciseDAO;
import app.daos.ExerciseLogDAO;
import app.daos.TrainingSessionDAO;
import app.dtos.ExerciseLogDTO;
import app.entities.Exercise;
import app.entities.ExerciseLog;
import app.entities.TrainingSession;
import app.exceptions.ApiException;

public class ExerciseLogService {

    private final ExerciseLogDAO exerciseLogDAO;
    private final TrainingSessionDAO trainingSessionDAO;
    private final ExerciseDAO exerciseDAO;

    public ExerciseLogService(
            ExerciseLogDAO exerciseLogDAO,
            TrainingSessionDAO trainingSessionDAO,
            ExerciseDAO exerciseDAO
    ) {
        this.exerciseLogDAO = exerciseLogDAO;
        this.trainingSessionDAO = trainingSessionDAO;
        this.exerciseDAO = exerciseDAO;
    }

    public ExerciseLogDTO createExerciseLog(
            Integer trainingSessionId,
            Integer exerciseId
    ) {
        if (trainingSessionId == null) {
            throw new ApiException(
                    400,
                    "Training session id is required"
            );
        }

        if (exerciseId == null) {
            throw new ApiException(
                    400,
                    "Exercise id is required"
            );
        }

        TrainingSession trainingSession =
                trainingSessionDAO.getById(trainingSessionId);

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

    public ExerciseLogDTO getExerciseLog(Integer id) {
        if (id == null) {
            throw new ApiException(
                    400,
                    "Exercise log id is required"
            );
        }

        ExerciseLog exerciseLog =
                exerciseLogDAO.getById(id);

        return toDTO(exerciseLog);
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