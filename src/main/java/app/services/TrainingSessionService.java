package app.services;

import app.daos.TrainingCalendarDAO;
import app.daos.TrainingProgramDAO;
import app.daos.TrainingSessionDAO;
import app.dtos.TrainingSessionDTO;
import app.entities.TrainingCalendar;
import app.entities.TrainingProgram;
import app.entities.TrainingSession;
import app.exceptions.ApiException;

import java.time.LocalDate;
import java.util.List;

public class TrainingSessionService {

    private final TrainingSessionDAO trainingSessionDAO;
    private final TrainingCalendarDAO trainingCalendarDAO;
    private final TrainingProgramDAO trainingProgramDAO;

    public TrainingSessionService(
            TrainingSessionDAO trainingSessionDAO,
            TrainingCalendarDAO trainingCalendarDAO,
            TrainingProgramDAO trainingProgramDAO
    ) {
        this.trainingSessionDAO = trainingSessionDAO;
        this.trainingCalendarDAO = trainingCalendarDAO;
        this.trainingProgramDAO = trainingProgramDAO;
    }

    public TrainingSessionDTO createTrainingSession(
            LocalDate trainingDate,
            Integer calendarId,
            Integer trainingProgramId
    ) {
        if (trainingDate == null) {
            throw new ApiException(
                    400,
                    "Training date is required"
            );
        }

        if (calendarId == null) {
            throw new ApiException(
                    400,
                    "Calendar id is required"
            );
        }

        if (trainingProgramId == null) {
            throw new ApiException(
                    400,
                    "Training program id is required"
            );
        }

        TrainingCalendar calendar =
                trainingCalendarDAO.getById(calendarId);

        TrainingProgram trainingProgram =
                trainingProgramDAO.getById(trainingProgramId);

        TrainingSession session =
                new TrainingSession(
                        trainingDate,
                        calendar,
                        trainingProgram
                );

        TrainingSession created =
                trainingSessionDAO.create(session);

        return toDTO(created);
    }

    public TrainingSessionDTO getTrainingSession(Integer id) {
        if (id == null) {
            throw new ApiException(
                    400,
                    "Training session id is required"
            );
        }

        TrainingSession session =
                trainingSessionDAO.getById(id);

        return toDTO(session);
    }

    public List<TrainingSessionDTO> getAllTrainingSessions() {
        List<TrainingSession> sessions =
                trainingSessionDAO.getAll();

        return sessions.stream()
                .map(this::toDTO)
                .toList();
    }

    public TrainingSessionDTO updateTrainingSession(
            Integer id,
            LocalDate trainingDate,
            Integer calendarId,
            Integer trainingProgramId,
            boolean completed,
            String notes
    ) {
        if (id == null) {
            throw new ApiException(
                    400,
                    "Training session id is required"
            );
        }

        if (trainingDate == null) {
            throw new ApiException(
                    400,
                    "Training date is required"
            );
        }

        if (calendarId == null) {
            throw new ApiException(
                    400,
                    "Calendar id is required"
            );
        }

        if (trainingProgramId == null) {
            throw new ApiException(
                    400,
                    "Training program id is required"
            );
        }

        TrainingSession existing =
                trainingSessionDAO.getById(id);

        TrainingCalendar calendar =
                trainingCalendarDAO.getById(calendarId);

        TrainingProgram trainingProgram =
                trainingProgramDAO.getById(trainingProgramId);

        TrainingSession session =
                TrainingSession.builder()
                        .id(existing.getId())
                        .trainingDate(trainingDate)
                        .completed(completed)
                        .notes(notes)
                        .calendar(calendar)
                        .trainingProgram(trainingProgram)
                        .build();

        TrainingSession updated =
                trainingSessionDAO.update(session);

        return toDTO(updated);
    }

    public void deleteTrainingSession(Integer id) {
        if (id == null) {
            throw new ApiException(
                    400,
                    "Training session id is required"
            );
        }

        trainingSessionDAO.delete(id);
    }

    private TrainingSessionDTO toDTO(
            TrainingSession session
    ) {
        return new TrainingSessionDTO(
                session.getId(),
                session.getCalendar().getId(),
                session.getTrainingProgram().getId(),
                session.getTrainingDate(),
                session.isCompleted(),
                session.getNotes()
        );
    }
}