package app.services;

import app.daos.ExerciseLogDAO;
import app.daos.SetLogDAO;
import app.dtos.SetLogDTO;
import app.entities.ExerciseLog;
import app.entities.SetLog;
import app.exceptions.ApiException;

public class SetLogService {

    private final SetLogDAO setLogDAO;
    private final ExerciseLogDAO exerciseLogDAO;

    public SetLogService(
            SetLogDAO setLogDAO,
            ExerciseLogDAO exerciseLogDAO
    ) {
        this.setLogDAO = setLogDAO;
        this.exerciseLogDAO = exerciseLogDAO;
    }

    public SetLogDTO createSetLog(
            Integer exerciseLogId,
            Integer setNumber,
            Double weight,
            Integer reps,
            Integer rir,
            boolean completed
    ) {
        if (exerciseLogId == null) {
            throw new ApiException(
                    400,
                    "Exercise log id is required"
            );
        }

        if (setNumber == null || setNumber < 1) {
            throw new ApiException(
                    400,
                    "Set number must be greater than 0"
            );
        }

        if (reps != null && reps < 0) {
            throw new ApiException(
                    400,
                    "Reps cannot be negative"
            );
        }

        if (weight != null && weight < 0) {
            throw new ApiException(
                    400,
                    "Weight cannot be negative"
            );
        }

        if (rir != null && rir < 0) {
            throw new ApiException(
                    400,
                    "RIR cannot be negative"
            );
        }

        ExerciseLog exerciseLog =
                exerciseLogDAO.getById(exerciseLogId);

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

    public SetLogDTO getSetLog(Integer id) {
        if (id == null) {
            throw new ApiException(
                    400,
                    "Set log id is required"
            );
        }

        SetLog setLog =
                setLogDAO.getById(id);

        return toDTO(setLog);
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