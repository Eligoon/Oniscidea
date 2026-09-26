package app.services;

import app.daos.BodyPartDAO;
import app.daos.ExerciseDAO;
import app.entities.BodyPart;
import app.entities.Exercise;
import app.exceptions.ApiException;

public class ExerciseBodyPartService {

    private final ExerciseDAO exerciseDAO;
    private final BodyPartDAO bodyPartDAO;

    public ExerciseBodyPartService(
            ExerciseDAO exerciseDAO,
            BodyPartDAO bodyPartDAO
    ) {
        this.exerciseDAO = exerciseDAO;
        this.bodyPartDAO = bodyPartDAO;
    }

    public void addBodyPart(Integer exerciseId, Integer bodyPartId) {

        Exercise exercise = exerciseDAO.getById(exerciseId);
        BodyPart bodyPart = bodyPartDAO.getById(bodyPartId);

        if (exercise.getBodyParts().contains(bodyPart)) {
            throw new ApiException(409, "Body part is already assigned to this exercise");
        }

        exercise.addBodyPart(bodyPart);

        exerciseDAO.update(exercise);
    }

    public void removeBodyPart(Integer exerciseId, Integer bodyPartId) {

        Exercise exercise = exerciseDAO.getById(exerciseId);
        BodyPart bodyPart = bodyPartDAO.getById(bodyPartId);

        if (!exercise.getBodyParts().contains(bodyPart)) {
            throw new ApiException(404, "Body part is not assigned to this exercise");
        }

        exercise.removeBodyPart(bodyPart);

        exerciseDAO.update(exercise);
    }
}