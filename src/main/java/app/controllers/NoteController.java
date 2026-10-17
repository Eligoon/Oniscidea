
package app.controllers;

import app.dtos.NoteDTO;
import app.services.NoteService;
import io.javalin.http.Context;

import java.util.List;

public class NoteController {

    private final NoteService noteService;

    public NoteController(
            NoteService noteService
    ) {
        this.noteService = noteService;
    }

    public void create(Context ctx) {
        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        NoteDTO request =
                ctx.bodyAsClass(
                        NoteDTO.class
                );

        NoteDTO note =
                noteService.createNote(
                        request.getTitle(),
                        request.getText(),
                        request.getProfileId(),
                        authenticatedUserId
                );

        ctx.status(201).json(note);
    }

    public void getAll(Context ctx) {
        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        List<NoteDTO> notes =
                noteService.getAllNotes(
                        authenticatedUserId
                );

        ctx.json(notes);
    }

    public void getById(Context ctx) {
        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        NoteDTO note =
                noteService.getNote(
                        id,
                        authenticatedUserId
                );

        ctx.json(note);
    }

    public void update(Context ctx) {
        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        NoteDTO request =
                ctx.bodyAsClass(
                        NoteDTO.class
                );

        NoteDTO note =
                noteService.updateNote(
                        id,
                        request.getTitle(),
                        request.getText(),
                        authenticatedUserId
                );

        ctx.json(note);
    }

    public void delete(Context ctx) {
        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        noteService.deleteNote(
                id,
                authenticatedUserId
        );

        ctx.status(204);
    }
}
