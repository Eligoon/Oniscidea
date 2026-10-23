package app.services;

import app.daos.ExerciseDAO;
import app.dtos.CreateExerciseDTO;
import app.dtos.ExerciseDTO;
import app.entities.Exercise;
import app.exceptions.ValidationException;

import java.util.List;

public class ExerciseService {

    private final ExerciseDAO exerciseDAO;

    public ExerciseService(ExerciseDAO exerciseDAO) {
        this.exerciseDAO = exerciseDAO;
    }

    public ExerciseDTO createExercise(CreateExerciseDTO dto) {
        if (dto == null || dto.getName() == null || dto.getName().isBlank()) {
            throw new ValidationException("Exercise name is required");
        }

        Exercise exercise = new Exercise(
                dto.getName().trim(),
                dto.getDescription()
        );

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

    private ExerciseDTO toDTO(Exercise exercise) {
        return new ExerciseDTO(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription()
        );
    }
}