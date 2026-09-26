package app.services;

import app.daos.NoteDAO;
import app.daos.ProfileDAO;
import app.dtos.NoteDTO;
import app.entities.Note;
import app.entities.Profile;
import app.exceptions.ApiException;

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
            Integer profileId
    ) {
        if (title == null || title.isBlank()) {
            throw new ApiException(
                    400,
                    "Note title is required"
            );
        }

        if (text == null || text.isBlank()) {
            throw new ApiException(
                    400,
                    "Note text is required"
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

    public NoteDTO getNote(Integer id) {
        if (id == null) {
            throw new ApiException(
                    400,
                    "Note id is required"
            );
        }

        Note note =
                noteDAO.getById(id);

        return toDTO(note);
    }

    public List<NoteDTO> getAllNotes() {

        List<Note> notes =
                noteDAO.getAll();

        return notes.stream()
                .map(this::toDTO)
                .toList();
    }

    public NoteDTO updateNote(
            Integer id,
            String title,
            String text
    ) {
        if (id == null) {
            throw new ApiException(
                    400,
                    "Note id is required"
            );
        }

        if (title == null || title.isBlank()) {
            throw new ApiException(
                    400,
                    "Note title is required"
            );
        }

        if (text == null || text.isBlank()) {
            throw new ApiException(
                    400,
                    "Note text is required"
            );
        }

        Note existing =
                noteDAO.getById(id);

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

    public void deleteNote(Integer id) {

        if (id == null) {
            throw new ApiException(
                    400,
                    "Note id is required"
            );
        }

        noteDAO.delete(id);
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