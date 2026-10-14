
package app.services;

import app.daos.ProfileDAO;
import app.daos.TrainingProgramDAO;
import app.dtos.TrainingProgramDTO;
import app.entities.Profile;
import app.entities.TrainingProgram;
import app.exceptions.ApiException;
import app.exceptions.ValidationException;

import java.util.List;

public class TrainingProgramService {

    private final TrainingProgramDAO trainingProgramDAO;
    private final ProfileDAO profileDAO;

    public TrainingProgramService(
            TrainingProgramDAO trainingProgramDAO,
            ProfileDAO profileDAO
    ) {
        this.trainingProgramDAO = trainingProgramDAO;
        this.profileDAO = profileDAO;
    }

    public TrainingProgramDTO createTrainingProgram(
            String name,
            String description,
            Integer profileId,
            Integer authenticatedUserId
    ) {

        if (name == null || name.isBlank()) {
            throw new ValidationException(
                    "Training program name is required"
            );
        }

        if (profileId == null) {
            throw new ValidationException(
                    "Profile id is required"
            );
        }

        requireAuthenticatedUser(authenticatedUserId);

        Profile profile =
                profileDAO.getById(profileId);

        if (!profile.getUser().getId().equals(authenticatedUserId)) {
            throw new ApiException(
                    403,
                    "You are not allowed to create a training program for this profile"
            );
        }

        TrainingProgram program =
                new TrainingProgram(
                        name,
                        description,
                        profile
                );

        TrainingProgram created =
                trainingProgramDAO.create(program);

        return toDTO(created);
    }

    public TrainingProgramDTO getTrainingProgram(
            Integer id,
            Integer authenticatedUserId
    ) {

        if (id == null) {
            throw new ValidationException(
                    "Training program id is required"
            );
        }

        requireAuthenticatedUser(authenticatedUserId);

        TrainingProgram program =
                trainingProgramDAO.getById(id);

        ensureOwnership(program, authenticatedUserId);

        return toDTO(program);
    }

    public List<TrainingProgramDTO> getAllTrainingPrograms(
            Integer authenticatedUserId
    ) {

        requireAuthenticatedUser(authenticatedUserId);

        List<TrainingProgram> programs =
                trainingProgramDAO.getAll();

        return programs.stream()
                .filter(program -> isOwnedByUser(
                        program,
                        authenticatedUserId
                ))
                .map(this::toDTO)
                .toList();
    }

    public TrainingProgramDTO updateTrainingProgram(
            Integer id,
            Integer authenticatedUserId,
            String name,
            String description
    ) {

        if (id == null) {
            throw new ValidationException(
                    "Training program id is required"
            );
        }

        if (name == null || name.isBlank()) {
            throw new ValidationException(
                    "Training program name is required"
            );
        }

        requireAuthenticatedUser(authenticatedUserId);

        TrainingProgram program =
                trainingProgramDAO.getById(id);

        ensureOwnership(program, authenticatedUserId);

        program.update(
                name,
                description
        );

        TrainingProgram updated =
                trainingProgramDAO.update(program);

        return toDTO(updated);
    }

    public void deleteTrainingProgram(
            Integer id,
            Integer authenticatedUserId
    ) {

        if (id == null) {
            throw new ValidationException(
                    "Training program id is required"
            );
        }

        requireAuthenticatedUser(authenticatedUserId);

        TrainingProgram program =
                trainingProgramDAO.getById(id);

        ensureOwnership(program, authenticatedUserId);

        trainingProgramDAO.delete(id);
    }

    private void requireAuthenticatedUser(Integer authenticatedUserId) {
        if (authenticatedUserId == null) {
            throw new ApiException(
                    401,
                    "Authentication is required"
            );
        }
    }

    private boolean isOwnedByUser(
            TrainingProgram program,
            Integer authenticatedUserId
    ) {
        Profile profile =
                profileDAO.getById(program.getProfile().getId());

        return profile.getUser().getId().equals(authenticatedUserId);
    }

    private void ensureOwnership(
            TrainingProgram program,
            Integer authenticatedUserId
    ) {
        if (!isOwnedByUser(program, authenticatedUserId)) {
            throw new ApiException(
                    403,
                    "You are not allowed to access this training program"
            );
        }
    }

    private TrainingProgramDTO toDTO(
            TrainingProgram program
    ) {

        return new TrainingProgramDTO(
                program.getId(),
                program.getName(),
                program.getDescription(),
                program.getProfile().getId()
        );
    }
}
