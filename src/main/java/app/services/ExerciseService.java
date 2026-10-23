package app.services;

import app.daos.ExerciseDAO;
import app.daos.UserDAO;
import app.dtos.BodyPartDTO;
import app.dtos.CreateExerciseDTO;
import app.dtos.ExerciseDTO;
import app.entities.Exercise;
import app.entities.User;
import app.exceptions.ValidationException;

import java.util.List;

public class ExerciseService {

    private final ExerciseDAO exerciseDAO;
    private final UserDAO userDAO;

    public ExerciseService(
            ExerciseDAO exerciseDAO,
            UserDAO userDAO
    ) {
        this.exerciseDAO = exerciseDAO;
        this.userDAO = userDAO;
    }

    public ExerciseDTO createExercise(
            CreateExerciseDTO dto,
            Integer userId
    ) {
        if (dto == null || dto.getName() == null || dto.getName().isBlank()) {
            throw new ValidationException("Exercise name is required");
        }

        if (userId == null) {
            throw new ValidationException("User id is required");
        }

        User owner = userDAO.getById(userId);

        Exercise exercise = new Exercise(
                dto.getName().trim(),
                dto.getDescription()
        );

        exercise.setOwner(owner);

        Exercise createdExercise = exerciseDAO.create(exercise);

        return toDTO(createdExercise);
    }

    public ExerciseDTO getExercise(Integer id) {
        if (id == null) {
            throw new ValidationException(
                    "Exercise id is required"
            );
        }

        Exercise exercise = exerciseDAO.getById(id);

        return toDTO(exercise);
    }

    public List<ExerciseDTO> getAllExercises(Integer userId) {
        List<Exercise> exercises =
                exerciseDAO.getAvailableExercises(userId);

        return exercises.stream()
                .map(this::toDTO)
                .toList();
    }

    public ExerciseDTO updateExercise(
            Integer id,
            CreateExerciseDTO dto,
            Integer userId
    ) {
        if (id == null) {
            throw new ValidationException("Exercise id is required");
        }

        if (userId == null) {
            throw new ValidationException("User id is required");
        }

        if (dto == null || dto.getName() == null || dto.getName().isBlank()) {
            throw new ValidationException("Exercise name is required");
        }

        Exercise updated = exerciseDAO.updateOwned(
                id,
                userId,
                dto.getName().trim(),
                dto.getDescription()
        );

        return toDTO(updated);
    }

    public void deleteExercise(Integer id, Integer userId) {
        if (id == null) {
            throw new ValidationException("Exercise id is required");
        }

        if (userId == null) {
            throw new ValidationException("User id is required");
        }

        exerciseDAO.deleteOwned(id, userId);
    }


    private ExerciseDTO toDTO(Exercise exercise) {
        List<BodyPartDTO> bodyParts = exercise.getBodyParts()
                .stream()
                .map(bodyPart -> new BodyPartDTO(
                        bodyPart.getId(),
                        bodyPart.getName(),
                        bodyPart.getColor()
                ))
                .toList();

        return new ExerciseDTO(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                bodyParts
        );
    }

}
