package app.services;

import app.daos.ProfileDAO;
import app.daos.TrainingCalendarDAO;
import app.daos.TrainingProgramDAO;
import app.daos.TrainingSessionDAO;
import app.dtos.TrainingSessionDTO;
import app.entities.Profile;
import app.entities.TrainingCalendar;
import app.entities.TrainingProgram;
import app.entities.TrainingSession;
import app.exceptions.ApiException;
import app.exceptions.ValidationException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class TrainingSessionService {

    private final TrainingSessionDAO trainingSessionDAO;
    private final TrainingCalendarDAO trainingCalendarDAO;
    private final TrainingProgramDAO trainingProgramDAO;
    private final ProfileDAO profileDAO;
    private final GoogleCalendarService googleCalendarService;

    public TrainingSessionService(
            TrainingSessionDAO trainingSessionDAO,
            TrainingCalendarDAO trainingCalendarDAO,
            TrainingProgramDAO trainingProgramDAO,
            ProfileDAO profileDAO,
            GoogleCalendarService googleCalendarService
    ) {
        this.trainingSessionDAO = trainingSessionDAO;
        this.trainingCalendarDAO = trainingCalendarDAO;
        this.trainingProgramDAO = trainingProgramDAO;
        this.profileDAO = profileDAO;
        this.googleCalendarService = googleCalendarService;
    }

    public TrainingSessionDTO createTrainingSession(
            LocalDate trainingDate,
            LocalTime startTime,
            LocalTime endTime,
            Integer calendarId,
            Integer trainingProgramId,
            Integer authenticatedUserId
    ) {
        requireAuthenticatedUser(authenticatedUserId);

        validateSessionDetails(
                trainingDate,
                startTime,
                endTime,
                calendarId,
                trainingProgramId
        );

        TrainingCalendar calendar =
                trainingCalendarDAO.getById(calendarId);

        TrainingProgram trainingProgram =
                trainingProgramDAO.getById(trainingProgramId);

        ensureCalendarOwnership(calendar, authenticatedUserId);
        ensureTrainingProgramOwnership(
                trainingProgram,
                authenticatedUserId
        );

        TrainingSession session = new TrainingSession(
                trainingDate,
                startTime,
                endTime,
                calendar,
                trainingProgram
        );

        TrainingSession created =
                trainingSessionDAO.create(session);

        // Diagnostic logging: check the Google Calendar ID loaded by Hibernate.
        String googleCalendarId = calendar.getGoogleCalendarId();

        System.out.println(
                "[Google Calendar] Calendar ID for training calendar "
                        + calendarId + ": "
                        + (googleCalendarId == null
                        || googleCalendarId.isBlank()
                        ? "<null or blank>"
                        : "<present>")
        );

        if (googleCalendarId != null
                && !googleCalendarId.isBlank()) {

            System.out.println(
                    "[Google Calendar] Creating event for training session "
                            + created.getId()
            );

            ZonedDateTime start = ZonedDateTime.of(
                    trainingDate,
                    startTime,
                    ZoneId.of("Europe/Copenhagen")
            );

            ZonedDateTime end = ZonedDateTime.of(
                    trainingDate,
                    endTime,
                    ZoneId.of("Europe/Copenhagen")
            );

            String googleEventId = googleCalendarService.createEvent(
                    authenticatedUserId,
                    googleCalendarId,
                    trainingProgram.getName(),
                    null,
                    start,
                    end
            );

            System.out.println(
                    "[Google Calendar] Event creation returned an ID: "
                            + (googleEventId != null
                            && !googleEventId.isBlank())
            );

            created.setGoogleEventId(googleEventId);

            created = trainingSessionDAO.update(created);

            System.out.println(
                    "[Google Calendar] Training session "
                            + created.getId()
                            + " updated with Google event ID: "
                            + (created.getGoogleEventId() != null
                            && !created.getGoogleEventId().isBlank())
            );
        } else {
            System.out.println(
                    "[Google Calendar] Event creation skipped because "
                            + "the calendar ID is missing."
            );
        }

        return toDTO(created);
    }

    public TrainingSessionDTO getTrainingSession(
            Integer id,
            Integer authenticatedUserId
    ) {
        requireAuthenticatedUser(authenticatedUserId);

        if (id == null) {
            throw new ValidationException(
                    "Training session id is required"
            );
        }

        TrainingSession session =
                trainingSessionDAO.getById(id);

        ensureSessionOwnership(session, authenticatedUserId);

        return toDTO(session);
    }

    public List<TrainingSessionDTO> getAllTrainingSessions(
            Integer authenticatedUserId
    ) {
        requireAuthenticatedUser(authenticatedUserId);

        List<TrainingSession> sessions =
                trainingSessionDAO.getAll();

        return sessions.stream()
                .filter(session -> isOwnedByUser(
                        session,
                        authenticatedUserId
                ))
                .map(this::toDTO)
                .toList();
    }

    public TrainingSessionDTO updateTrainingSession(
            Integer id,
            LocalDate trainingDate,
            LocalTime startTime,
            LocalTime endTime,
            Integer calendarId,
            Integer trainingProgramId,
            boolean completed,
            String notes,
            Integer authenticatedUserId
    ) {
        requireAuthenticatedUser(authenticatedUserId);

        if (id == null) {
            throw new ValidationException(
                    "Training session id is required"
            );
        }

        validateSessionDetails(
                trainingDate,
                startTime,
                endTime,
                calendarId,
                trainingProgramId
        );

        TrainingSession session =
                trainingSessionDAO.getById(id);

        ensureSessionOwnership(session, authenticatedUserId);

        TrainingCalendar calendar =
                trainingCalendarDAO.getById(calendarId);

        TrainingProgram trainingProgram =
                trainingProgramDAO.getById(trainingProgramId);

        ensureCalendarOwnership(calendar, authenticatedUserId);
        ensureTrainingProgramOwnership(
                trainingProgram,
                authenticatedUserId
        );

        TrainingCalendar oldCalendar =
                trainingCalendarDAO.getById(
                        session.getCalendar().getId()
                );
        String oldGoogleCalendarId =
                oldCalendar.getGoogleCalendarId();
        String newGoogleCalendarId =
                calendar.getGoogleCalendarId();
        String googleEventId = session.getGoogleEventId();

        if (googleEventId != null && !googleEventId.isBlank()) {
            if (oldGoogleCalendarId == null
                    || oldGoogleCalendarId.isBlank()) {
                throw new ValidationException(
                        "Google Calendar ID is required to synchronize this event"
                );
            }

            if (newGoogleCalendarId == null
                    || newGoogleCalendarId.isBlank()) {
                googleCalendarService.deleteEvent(
                        authenticatedUserId,
                        oldGoogleCalendarId,
                        googleEventId
                );
                session.setGoogleEventId(null);
            } else {
                ZonedDateTime start = ZonedDateTime.of(
                        trainingDate,
                        startTime,
                        ZoneId.of("Europe/Copenhagen")
                );

                ZonedDateTime end = ZonedDateTime.of(
                        trainingDate,
                        endTime,
                        ZoneId.of("Europe/Copenhagen")
                );

                if (oldGoogleCalendarId.equals(newGoogleCalendarId)) {
                    googleCalendarService.updateEvent(
                            authenticatedUserId,
                            newGoogleCalendarId,
                            googleEventId,
                            trainingProgram.getName(),
                            null,
                            start,
                            end
                    );
                } else {
                    String newGoogleEventId =
                            googleCalendarService.createEvent(
                                    authenticatedUserId,
                                    newGoogleCalendarId,
                                    trainingProgram.getName(),
                                    null,
                                    start,
                                    end
                            );

                    googleCalendarService.deleteEvent(
                            authenticatedUserId,
                            oldGoogleCalendarId,
                            googleEventId
                    );

                    session.setGoogleEventId(newGoogleEventId);
                }
            }
        } else if (newGoogleCalendarId != null
                && !newGoogleCalendarId.isBlank()) {
            ZonedDateTime start = ZonedDateTime.of(
                    trainingDate,
                    startTime,
                    ZoneId.of("Europe/Copenhagen")
            );

            ZonedDateTime end = ZonedDateTime.of(
                    trainingDate,
                    endTime,
                    ZoneId.of("Europe/Copenhagen")
            );

            String newGoogleEventId = googleCalendarService.createEvent(
                    authenticatedUserId,
                    newGoogleCalendarId,
                    trainingProgram.getName(),
                    null,
                    start,
                    end
            );

            session.setGoogleEventId(newGoogleEventId);
        }

        session.update(
                trainingDate,
                startTime,
                endTime,
                completed,
                notes,
                calendar,
                trainingProgram
        );

        TrainingSession updated =
                trainingSessionDAO.update(session);

        return toDTO(updated);
    }

    public void deleteTrainingSession(
            Integer id,
            Integer authenticatedUserId
    ) {
        requireAuthenticatedUser(authenticatedUserId);

        if (id == null) {
            throw new ValidationException(
                    "Training session id is required"
            );
        }

        TrainingSession session =
                trainingSessionDAO.getById(id);

        ensureSessionOwnership(session, authenticatedUserId);

        String googleEventId = session.getGoogleEventId();
        TrainingCalendar calendar =
                trainingCalendarDAO.getById(
                        session.getCalendar().getId()
                );
        String googleCalendarId = calendar.getGoogleCalendarId();

        if (googleEventId != null && !googleEventId.isBlank()) {
            if (googleCalendarId == null || googleCalendarId.isBlank()) {
                throw new ValidationException(
                        "Google Calendar ID is required to delete this event"
                );
            }

            googleCalendarService.deleteEvent(
                    authenticatedUserId,
                    googleCalendarId,
                    googleEventId
            );
        }

        trainingSessionDAO.delete(id);
    }

    private void validateSessionDetails(
            LocalDate trainingDate,
            LocalTime startTime,
            LocalTime endTime,
            Integer calendarId,
            Integer trainingProgramId
    ) {
        if (trainingDate == null) {
            throw new ValidationException(
                    "Training date is required"
            );
        }

        if (startTime == null) {
            throw new ValidationException(
                    "Start time is required"
            );
        }

        if (endTime == null) {
            throw new ValidationException(
                    "End time is required"
            );
        }

        if (!endTime.isAfter(startTime)) {
            throw new ValidationException(
                    "End time must be after start time"
            );
        }

        if (calendarId == null) {
            throw new ValidationException(
                    "Calendar id is required"
            );
        }

        if (trainingProgramId == null) {
            throw new ValidationException(
                    "Training program id is required"
            );
        }
    }

    private boolean isOwnedByUser(
            TrainingSession session,
            Integer authenticatedUserId
    ) {
        TrainingCalendar calendar =
                trainingCalendarDAO.getById(
                        session.getCalendar().getId()
                );

        return isCalendarOwnedByUser(
                calendar,
                authenticatedUserId
        );
    }

    private boolean isCalendarOwnedByUser(
            TrainingCalendar calendar,
            Integer authenticatedUserId
    ) {
        Profile profile =
                profileDAO.getById(
                        calendar.getProfile().getId()
                );

        return profile.getUser().getId()
                .equals(authenticatedUserId);
    }

    private void ensureSessionOwnership(
            TrainingSession session,
            Integer authenticatedUserId
    ) {
        if (!isOwnedByUser(session, authenticatedUserId)) {
            throw new ApiException(
                    403,
                    "You do not have access to this training session"
            );
        }
    }

    private void ensureCalendarOwnership(
            TrainingCalendar calendar,
            Integer authenticatedUserId
    ) {
        if (!isCalendarOwnedByUser(
                calendar,
                authenticatedUserId
        )) {
            throw new ApiException(
                    403,
                    "You do not have access to this training calendar"
            );
        }
    }

    private void ensureTrainingProgramOwnership(
            TrainingProgram trainingProgram,
            Integer authenticatedUserId
    ) {
        Profile profile =
                profileDAO.getById(
                        trainingProgram.getProfile().getId()
                );

        if (!profile.getUser().getId()
                .equals(authenticatedUserId)) {
            throw new ApiException(
                    403,
                    "You do not have access to this training program"
            );
        }
    }

    private void requireAuthenticatedUser(
            Integer authenticatedUserId
    ) {
        if (authenticatedUserId == null) {
            throw new ApiException(
                    401,
                    "Authentication is required"
            );
        }
    }

    private TrainingSessionDTO toDTO(
            TrainingSession session
    ) {
        return new TrainingSessionDTO(
                session.getId(),
                session.getCalendar().getId(),
                session.getTrainingProgram().getId(),
                session.getTrainingDate(),
                session.getStartTime(),
                session.getEndTime(),
                session.isCompleted(),
                session.getNotes(),
                session.getGoogleEventId()
        );
    }
}