package app.services;

import app.daos.ProfileDAO;
import app.daos.TrainingProgramDAO;
import app.dtos.TrainingProgramDTO;
import app.entities.Profile;
import app.entities.TrainingProgram;
import app.exceptions.ApiException;

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
            Integer profileId
    ) {

        if (name == null || name.isBlank()) {
            throw new ApiException(
                    400,
                    "Training program name is required"
            );
        }

        if (profileId == null) {
            throw new ApiException(
                    400,
                    "Profile id is required"
            );
        }

        Profile profile =
                profileDAO.getById(profileId);

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

    public TrainingProgramDTO getTrainingProgram(Integer id) {

        if (id == null) {
            throw new ApiException(
                    400,
                    "Training program id is required"
            );
        }

        TrainingProgram program =
                trainingProgramDAO.getById(id);

        return toDTO(program);
    }

    public List<TrainingProgramDTO> getAllTrainingPrograms() {

        List<TrainingProgram> programs =
                trainingProgramDAO.getAll();

        return programs.stream()
                .map(this::toDTO)
                .toList();
    }

    public TrainingProgramDTO updateTrainingProgram(
            Integer id,
            String name,
            String description
    ) {

        if (id == null) {
            throw new ApiException(
                    400,
                    "Training program id is required"
            );
        }

        if (name == null || name.isBlank()) {
            throw new ApiException(
                    400,
                    "Training program name is required"
            );
        }

        TrainingProgram program =
                trainingProgramDAO.getById(id);

        program.update(
                name,
                description
        );

        TrainingProgram updated =
                trainingProgramDAO.update(program);

        return toDTO(updated);
    }

    public void deleteTrainingProgram(Integer id) {

        if (id == null) {
            throw new ApiException(
                    400,
                    "Training program id is required"
            );
        }

        trainingProgramDAO.delete(id);
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