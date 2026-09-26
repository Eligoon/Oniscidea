package app.services;

import app.daos.ExerciseDAO;
import app.dtos.ExerciseDTO;
import app.entities.Exercise;
import app.exceptions.ApiException;

import java.util.List;

public class ExerciseService {

    private final ExerciseDAO exerciseDAO;

    public ExerciseService(ExerciseDAO exerciseDAO) {
        this.exerciseDAO = exerciseDAO;
    }

    public ExerciseDTO getExercise(Integer id) {
        if (id == null) {
            throw new ApiException(
                    400,
                    "Exercise id is required"
            );
        }

        Exercise exercise =
                exerciseDAO.getById(id);

        return toDTO(exercise);
    }

    public List<ExerciseDTO> getAllExercises() {
        List<Exercise> exercises =
                exerciseDAO.getAll();

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