
package app.services;

import app.daos.ExerciseDAO;
import app.daos.ProfileDAO;
import app.daos.TrainingProgramDAO;
import app.daos.TrainingProgramExerciseDAO;
import app.dtos.TrainingProgramExerciseDTO;
import app.entities.Exercise;
import app.entities.Profile;
import app.entities.TrainingProgram;
import app.entities.TrainingProgramExercise;
import app.exceptions.ResourceNotFoundException;
import app.exceptions.ValidationException;
import io.javalin.http.ForbiddenResponse;

public class TrainingProgramExerciseService {

    private final TrainingProgramExerciseDAO trainingProgramExerciseDAO;
    private final TrainingProgramDAO trainingProgramDAO;
    private final ExerciseDAO exerciseDAO;
    private final ProfileDAO profileDAO;

    public TrainingProgramExerciseService(
            TrainingProgramExerciseDAO trainingProgramExerciseDAO,
            TrainingProgramDAO trainingProgramDAO,
            ExerciseDAO exerciseDAO,
            ProfileDAO profileDAO
    ) {
        this.trainingProgramExerciseDAO =
                trainingProgramExerciseDAO;

        this.trainingProgramDAO =
                trainingProgramDAO;

        this.exerciseDAO =
                exerciseDAO;

        this.profileDAO =
                profileDAO;
    }

    public TrainingProgramExerciseDTO addExercise(
            Integer trainingProgramId,
            Integer exerciseId,
            Integer exerciseOrder,
            Integer plannedSets,
            Integer plannedReps,
            Integer restSeconds,
            Integer authenticatedUserId
    ) {
        if (trainingProgramId == null) {
            throw new ValidationException(
                    "Training program id is required"
            );
        }

        if (exerciseId == null) {
            throw new ValidationException(
                    "Exercise id is required"
            );
        }

        if (exerciseOrder == null || exerciseOrder < 1) {
            throw new ValidationException(
                    "Exercise order must be greater than 0"
            );
        }

        TrainingProgram trainingProgram =
                trainingProgramDAO.getById(
                        trainingProgramId
                );

        verifyProgramOwnership(
                trainingProgram,
                authenticatedUserId
        );

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
            Integer exerciseId,
            Integer authenticatedUserId
    ) {
        if (trainingProgramId == null) {
            throw new ValidationException(
                    "Training program id is required"
            );
        }

        if (exerciseId == null) {
            throw new ValidationException(
                    "Exercise id is required"
            );
        }

        TrainingProgram trainingProgram =
                trainingProgramDAO.getById(
                        trainingProgramId
                );

        verifyProgramOwnership(
                trainingProgram,
                authenticatedUserId
        );

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

        throw new ResourceNotFoundException(
                "Exercise is not part of this training program"
        );
    }

    private void verifyProgramOwnership(
            TrainingProgram trainingProgram,
            Integer authenticatedUserId
    ) {
        if (authenticatedUserId == null) {
            throw new ForbiddenResponse(
                    "You do not have access to this training program"
            );
        }

        Integer profileId =
                trainingProgram.getProfile().getId();

        Profile profile =
                profileDAO.getById(profileId);

        if (profile.getUser() == null
                || !authenticatedUserId.equals(
                profile.getUser().getId()
        )) {
            throw new ForbiddenResponse(
                    "You do not have access to this training program"
            );
        }
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
