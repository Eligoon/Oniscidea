package app.services;

import app.daos.ProfileDAO;
import app.daos.TrainingCalendarDAO;
import app.dtos.TrainingCalendarDTO;
import app.entities.Profile;
import app.entities.TrainingCalendar;
import app.exceptions.ValidationException;

import java.util.List;

public class TrainingCalendarService {

    private final TrainingCalendarDAO trainingCalendarDAO;
    private final ProfileDAO profileDAO;

    public TrainingCalendarService(
            TrainingCalendarDAO trainingCalendarDAO,
            ProfileDAO profileDAO
    ) {
        this.trainingCalendarDAO = trainingCalendarDAO;
        this.profileDAO = profileDAO;
    }

    public TrainingCalendarDTO createTrainingCalendar(
            String name,
            Integer profileId
    ) {
        if (name == null || name.isBlank()) {
            throw new ValidationException(
                    "Calendar name is required"
            );
        }

        if (profileId == null) {
            throw new ValidationException(
                    "Profile id is required"
            );
        }

        Profile profile =
                profileDAO.getById(profileId);

        TrainingCalendar calendar =
                new TrainingCalendar(
                        name,
                        profile
                );

        TrainingCalendar created =
                trainingCalendarDAO.create(calendar);

        return toDTO(created);
    }

    public TrainingCalendarDTO getTrainingCalendar(
            Integer id
    ) {
        if (id == null) {
            throw new ValidationException(
                    "Calendar id is required"
            );
        }

        TrainingCalendar calendar =
                trainingCalendarDAO.getById(id);

        return toDTO(calendar);
    }

    public List<TrainingCalendarDTO> getAllTrainingCalendars() {

        List<TrainingCalendar> calendars =
                trainingCalendarDAO.getAll();

        return calendars.stream()
                .map(this::toDTO)
                .toList();
    }

    public TrainingCalendarDTO updateTrainingCalendar(
            Integer id,
            String name
    ) {
        if (id == null) {
            throw new ValidationException(
                    "Calendar id is required"
            );
        }

        if (name == null || name.isBlank()) {
            throw new ValidationException(
                    "Calendar name is required"
            );
        }

        TrainingCalendar existing =
                trainingCalendarDAO.getById(id);

        TrainingCalendar updatedCalendar =
                TrainingCalendar.builder()
                        .id(existing.getId())
                        .name(name)
                        .profile(existing.getProfile())
                        .build();

        TrainingCalendar updated =
                trainingCalendarDAO.update(
                        updatedCalendar
                );

        return toDTO(updated);
    }

    public void deleteTrainingCalendar(Integer id) {

        if (id == null) {
            throw new ValidationException(
                    "Calendar id is required"
            );
        }

        trainingCalendarDAO.delete(id);
    }

    private TrainingCalendarDTO toDTO(
            TrainingCalendar calendar
    ) {
        return new TrainingCalendarDTO(
                calendar.getId(),
                calendar.getName(),
                calendar.getProfile().getId(),
                calendar.getGoogleCalendarId()
        );
    }
}