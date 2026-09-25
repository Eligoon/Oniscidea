package app.services;

import app.daos.ExerciseDAO;
import app.daos.TrainingProgramDAO;
import app.daos.TrainingProgramExerciseDAO;
import app.dtos.TrainingProgramExerciseDTO;
import app.entities.Exercise;
import app.entities.TrainingProgram;
import app.entities.TrainingProgramExercise;
import app.exceptions.ApiException;

public class TrainingProgramExerciseService {

    private final TrainingProgramExerciseDAO trainingProgramExerciseDAO;
    private final TrainingProgramDAO trainingProgramDAO;
    private final ExerciseDAO exerciseDAO;

    public TrainingProgramExerciseService(
            TrainingProgramExerciseDAO trainingProgramExerciseDAO,
            TrainingProgramDAO trainingProgramDAO,
            ExerciseDAO exerciseDAO
    ) {
        this.trainingProgramExerciseDAO = trainingProgramExerciseDAO;
        this.trainingProgramDAO = trainingProgramDAO;
        this.exerciseDAO = exerciseDAO;
    }

    public TrainingProgramExerciseDTO addExercise(
            Integer trainingProgramId,
            Integer exerciseId,
            Integer exerciseOrder,
            Integer plannedSets,
            Integer plannedReps,
            Integer restSeconds
    ) {
        if (trainingProgramId == null) {
            throw new ApiException(
                    400,
                    "Training program id is required"
            );
        }

        if (exerciseId == null) {
            throw new ApiException(
                    400,
                    "Exercise id is required"
            );
        }

        if (exerciseOrder == null || exerciseOrder < 1) {
            throw new ApiException(
                    400,
                    "Exercise order must be greater than 0"
            );
        }

        TrainingProgram trainingProgram =
                trainingProgramDAO.getById(trainingProgramId);

        Exercise exercise =
                exerciseDAO.getById(exerciseId);

        TrainingProgramExercise trainingProgramExercise =
                new TrainingProgramExercise(
                        trainingProgram,
                        exercise,
                        exerciseOrder,
                        plannedSets,
                        plannedReps,
                        restSeconds
                );

        TrainingProgramExercise created =
                trainingProgramExerciseDAO.create(
                        trainingProgramExercise
                );

        return toDTO(created);
    }

    public void deleteExercise(
            Integer trainingProgramId,
            Integer exerciseId
    ) {
        if (trainingProgramId == null) {
            throw new ApiException(
                    400,
                    "Training program id is required"
            );
        }

        if (exerciseId == null) {
            throw new ApiException(
                    400,
                    "Exercise id is required"
            );
        }

        // Make sure the training program exists
        trainingProgramDAO.getById(trainingProgramId);

        // Make sure the exercise exists
        exerciseDAO.getById(exerciseId);

        for (TrainingProgramExercise entity :
                trainingProgramExerciseDAO.getAll()) {

            if (entity.getTrainingProgram().getId()
                    .equals(trainingProgramId)
                    && entity.getExercise().getId()
                    .equals(exerciseId)) {

                trainingProgramExerciseDAO.delete(
                        entity.getId()
                );

                return;
            }
        }

        throw new ApiException(
                404,
                "Exercise is not part of this training program"
        );
    }

    private TrainingProgramExerciseDTO toDTO(
            TrainingProgramExercise entity
    ) {
        return new TrainingProgramExerciseDTO(
                entity.getId(),
                entity.getTrainingProgram().getId(),
                entity.getExercise().getId(),
                entity.getOrderIndex(),
                entity.getPlannedSets(),
                entity.getPlannedReps(),
                entity.getRestSeconds()
        );
    }
}