package app.services;

import app.daos.ProfileDAO;
import app.daos.TrainingCalendarDAO;
import app.dtos.TrainingCalendarDTO;
import app.entities.Profile;
import app.entities.TrainingCalendar;
import app.exceptions.ApiException;
import app.exceptions.ValidationException;

import java.util.List;

public class TrainingCalendarService {

    private final TrainingCalendarDAO trainingCalendarDAO;
    private final ProfileDAO profileDAO;
    private final GoogleCalendarService googleCalendarService;

    public TrainingCalendarService(
            TrainingCalendarDAO trainingCalendarDAO,
            ProfileDAO profileDAO,
            GoogleCalendarService googleCalendarService
    ) {
        this.trainingCalendarDAO = trainingCalendarDAO;
        this.profileDAO = profileDAO;
        this.googleCalendarService = googleCalendarService;
    }

    public TrainingCalendarDTO createTrainingCalendar(
            String name,
            Integer profileId,
            Integer authenticatedUserId
    ) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Calendar name is required");
        }

        if (profileId == null) {
            throw new ValidationException("Profile id is required");
        }

        requireAuthenticatedUser(authenticatedUserId);

        Profile profile = profileDAO.getById(profileId);

        if (!profile.getUser().getId().equals(authenticatedUserId)) {
            throw new ApiException(
                    403,
                    "You are not allowed to create a calendar for this profile"
            );
        }

        String googleCalendarId =
                googleCalendarService.createCalendar(
                        authenticatedUserId,
                        name
                );

        TrainingCalendar calendar =
                new TrainingCalendar(name, profile);

        calendar.setGoogleCalendarId(googleCalendarId);

        TrainingCalendar created = trainingCalendarDAO.create(calendar);

        return toDTO(created);
    }

    public TrainingCalendarDTO getTrainingCalendar(
            Integer id,
            Integer authenticatedUserId
    ) {
        if (id == null) {
            throw new ValidationException("Calendar id is required");
        }

        requireAuthenticatedUser(authenticatedUserId);

        TrainingCalendar calendar = trainingCalendarDAO.getById(id);
        ensureOwnership(calendar, authenticatedUserId);

        return toDTO(calendar);
    }

    public List<TrainingCalendarDTO> getAllTrainingCalendars(
            Integer authenticatedUserId
    ) {
        requireAuthenticatedUser(authenticatedUserId);

        return trainingCalendarDAO.getAll().stream()
                .filter(calendar -> isOwnedByUser(calendar, authenticatedUserId))
                .map(this::toDTO)
                .toList();
    }

    public TrainingCalendarDTO updateTrainingCalendar(
            Integer id,
            String name,
            Integer authenticatedUserId
    ) {
        if (id == null) {
            throw new ValidationException("Calendar id is required");
        }

        if (name == null || name.isBlank()) {
            throw new ValidationException("Calendar name is required");
        }

        requireAuthenticatedUser(authenticatedUserId);

        TrainingCalendar existing = trainingCalendarDAO.getById(id);
        ensureOwnership(existing, authenticatedUserId);

        TrainingCalendar updatedCalendar =
                TrainingCalendar.builder()
                        .id(existing.getId())
                        .name(name)
                        .profile(existing.getProfile())
                        .googleCalendarId(existing.getGoogleCalendarId())
                        .build();

        TrainingCalendar updated =
                trainingCalendarDAO.update(updatedCalendar);

        return toDTO(updated);
    }

    public void deleteTrainingCalendar(
            Integer id,
            Integer authenticatedUserId
    ) {
        if (id == null) {
            throw new ValidationException("Calendar id is required");
        }

        requireAuthenticatedUser(authenticatedUserId);

        TrainingCalendar calendar = trainingCalendarDAO.getById(id);
        ensureOwnership(calendar, authenticatedUserId);

        trainingCalendarDAO.delete(id);
    }

    private void requireAuthenticatedUser(Integer authenticatedUserId) {
        if (authenticatedUserId == null) {
            throw new ApiException(401, "Authentication is required");
        }
    }

    private boolean isOwnedByUser(
            TrainingCalendar calendar,
            Integer authenticatedUserId
    ) {
        Profile profile = profileDAO.getById(calendar.getProfile().getId());
        return profile.getUser().getId().equals(authenticatedUserId);
    }

    private void ensureOwnership(
            TrainingCalendar calendar,
            Integer authenticatedUserId
    ) {
        if (!isOwnedByUser(calendar, authenticatedUserId)) {
            throw new ApiException(
                    403,
                    "You are not allowed to access this calendar"
            );
        }
    }

    private TrainingCalendarDTO toDTO(TrainingCalendar calendar) {
        return new TrainingCalendarDTO(
                calendar.getId(),
                calendar.getName(),
                calendar.getProfile().getId(),
                calendar.getGoogleCalendarId()
        );
    }
}
