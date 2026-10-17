
package app.services;

import app.daos.NoteDAO;
import app.daos.ProfileDAO;
import app.dtos.NoteDTO;
import app.entities.Note;
import app.entities.Profile;
import app.exceptions.ValidationException;
import io.javalin.http.ForbiddenResponse;

import java.util.List;

public class NoteService {

    private final NoteDAO noteDAO;
    private final ProfileDAO profileDAO;

    public NoteService(
            NoteDAO noteDAO,
            ProfileDAO profileDAO
    ) {
        this.noteDAO = noteDAO;
        this.profileDAO = profileDAO;
    }

    public NoteDTO createNote(
            String title,
            String text,
            Integer profileId,
            Integer authenticatedUserId
    ) {
        if (title == null || title.isBlank()) {
            throw new ValidationException(
                    "Note title is required"
            );
        }

        if (text == null || text.isBlank()) {
            throw new ValidationException(
                    "Note text is required"
            );
        }

        if (profileId == null) {
            throw new ValidationException(
                    "Profile id is required"
            );
        }

        verifyProfileOwnership(
                profileId,
                authenticatedUserId
        );

        Profile profile =
                profileDAO.getById(profileId);

        Note note =
                new Note(
                        title,
                        text,
                        profile
                );

        Note created =
                noteDAO.create(note);

        return toDTO(created);
    }

    public NoteDTO getNote(
            Integer id,
            Integer authenticatedUserId
    ) {
        if (id == null) {
            throw new ValidationException(
                    "Note id is required"
            );
        }

        Note note =
                noteDAO.getById(id);

        verifyProfileOwnership(
                note.getProfile().getId(),
                authenticatedUserId
        );

        return toDTO(note);
    }

    public List<NoteDTO> getAllNotes(
            Integer authenticatedUserId
    ) {
        return noteDAO.getAll()
                .stream()
                .filter(note -> isProfileOwnedByUser(
                        note.getProfile().getId(),
                        authenticatedUserId
                ))
                .map(this::toDTO)
                .toList();
    }

    public NoteDTO updateNote(
            Integer id,
            String title,
            String text,
            Integer authenticatedUserId
    ) {
        if (id == null) {
            throw new ValidationException(
                    "Note id is required"
            );
        }

        if (title == null || title.isBlank()) {
            throw new ValidationException(
                    "Note title is required"
            );
        }

        if (text == null || text.isBlank()) {
            throw new ValidationException(
                    "Note text is required"
            );
        }

        Note existing =
                noteDAO.getById(id);

        verifyProfileOwnership(
                existing.getProfile().getId(),
                authenticatedUserId
        );

        Note updatedNote =
                Note.builder()
                        .id(existing.getId())
                        .title(title)
                        .text(text)
                        .profile(existing.getProfile())
                        .build();

        Note updated =
                noteDAO.update(updatedNote);

        return toDTO(updated);
    }

    public void deleteNote(
            Integer id,
            Integer authenticatedUserId
    ) {
        if (id == null) {
            throw new ValidationException(
                    "Note id is required"
            );
        }

        Note note =
                noteDAO.getById(id);

        verifyProfileOwnership(
                note.getProfile().getId(),
                authenticatedUserId
        );

        noteDAO.delete(id);
    }

    private void verifyProfileOwnership(
            Integer profileId,
            Integer authenticatedUserId
    ) {
        if (!isProfileOwnedByUser(
                profileId,
                authenticatedUserId
        )) {
            throw new ForbiddenResponse(
                    "You do not have access to this note"
            );
        }
    }

    private boolean isProfileOwnedByUser(
            Integer profileId,
            Integer authenticatedUserId
    ) {
        if (profileId == null || authenticatedUserId == null) {
            return false;
        }

        Profile profile =
                profileDAO.getById(profileId);

        return profile != null
                && profile.getUser() != null
                && authenticatedUserId.equals(
                profile.getUser().getId()
        );
    }

    private NoteDTO toDTO(Note note) {
        return new NoteDTO(
                note.getId(),
                note.getTitle(),
                note.getText(),
                note.getProfile().getId()
        );
    }
}
