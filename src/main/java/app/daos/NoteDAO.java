package app.daos;

import app.entities.Note;
import app.exceptions.DatabaseException;
import app.exceptions.ResourceNotFoundException;
import app.exceptions.ValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class NoteDAO implements IDAO<Note, Integer> {

    private final EntityManagerFactory emf;

    public NoteDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Note create(Note note) {
        if (note == null) {
            throw new ValidationException("Note is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                em.persist(note);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Create note failed",
                        e
                );

            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw e;
            }
        }

        return note;
    }

    @Override
    public Note getById(Integer id) {
        if (id == null) {
            throw new ValidationException("Note id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                Note note = em.find(Note.class, id);

                if (note != null) {
                    return note;
                }

                throw new ResourceNotFoundException(
                        "Note not found"
                );

            } catch (PersistenceException e) {
                throw new DatabaseException(
                        "Get note failed",
                        e
                );
            }
        }
    }

    @Override
    public List<Note> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<Note> query =
                        em.createQuery(
                                "SELECT n FROM Note n",
                                Note.class
                        );

                return query.getResultList();

            } catch (PersistenceException e) {
                throw new DatabaseException(
                        "Get notes failed",
                        e
                );
            }
        }
    }

    @Override
    public Note update(Note note) {
        if (note == null || note.getId() == null) {
            throw new ValidationException(
                    "Note id is required"
            );
        }

        Note updated;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                Note existing =
                        em.find(Note.class, note.getId());

                if (existing == null) {
                    throw new ResourceNotFoundException(
                            "Note not found"
                    );
                }

                updated = em.merge(note);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Update note failed",
                        e
                );

            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw e;
            }
        }

        return updated;
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) {
            throw new ValidationException(
                    "Note id is required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                Note note = em.find(Note.class, id);

                if (note == null) {
                    throw new ResourceNotFoundException(
                            "Note not found"
                    );
                }

                em.remove(note);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Delete note failed",
                        e
                );

            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw e;
            }
        }

        return true;
    }
}