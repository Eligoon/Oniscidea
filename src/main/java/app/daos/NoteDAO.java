package app.daos;

import app.entities.Note;
import app.exceptions.ApiException;
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
            throw new ApiException(400, "Note is required");
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

                throw new ApiException(
                        500,
                        "Create note failed: " + e.getMessage()
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
            throw new ApiException(400, "Note id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                Note note = em.find(Note.class, id);

                if (note != null) {
                    return note;
                }

                throw new ApiException(404, "Note not found");

            } catch (PersistenceException e) {
                throw new ApiException(
                        500,
                        "Get note failed: " + e.getMessage()
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
                throw new ApiException(
                        500,
                        "Get notes failed: " + e.getMessage()
                );
            }
        }
    }

    @Override
    public Note update(Note note) {
        if (note == null || note.getId() == null) {
            throw new ApiException(400, "Note id is required");
        }

        Note updated;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                Note existing =
                        em.find(Note.class, note.getId());

                if (existing == null) {
                    throw new ApiException(404, "Note not found");
                }

                updated = em.merge(note);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Update note failed: " + e.getMessage()
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
            throw new ApiException(400, "Note id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                Note note = em.find(Note.class, id);

                if (note == null) {
                    throw new ApiException(404, "Note not found");
                }

                em.remove(note);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Delete note failed: " + e.getMessage()
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