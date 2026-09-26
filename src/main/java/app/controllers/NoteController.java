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

        NoteDTO request =
                ctx.bodyAsClass(
                        NoteDTO.class
                );

        NoteDTO note =
                noteService.createNote(
                        request.getTitle(),
                        request.getText(),
                        request.getProfileId()
                );

        ctx.status(201).json(note);
    }

    public void getAll(Context ctx) {

        List<NoteDTO> notes =
                noteService.getAllNotes();

        ctx.json(notes);
    }

    public void getById(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        NoteDTO note =
                noteService.getNote(id);

        ctx.json(note);
    }

    public void update(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        NoteDTO request =
                ctx.bodyAsClass(
                        NoteDTO.class
                );

        NoteDTO note =
                noteService.updateNote(
                        id,
                        request.getTitle(),
                        request.getText()
                );

        ctx.json(note);
    }

    public void delete(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        noteService.deleteNote(id);

        ctx.status(204);
    }
}