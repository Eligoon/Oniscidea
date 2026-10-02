package app.daos;

import app.entities.ExerciseLog;
import app.exceptions.DatabaseException;
import app.exceptions.ResourceNotFoundException;
import app.exceptions.ValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ExerciseLogDAO implements IDAO<ExerciseLog, Integer> {

    private final EntityManagerFactory emf;

    public ExerciseLogDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public ExerciseLog create(ExerciseLog log) {
        if (log == null) {
            throw new ValidationException("Exercise log is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                em.persist(log);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Create exercise log failed",
                        e
                );

            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw e;
            }
        }

        return log;
    }

    @Override
    public ExerciseLog getById(Integer id) {
        if (id == null) {
            throw new ValidationException(
                    "Exercise log id is required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                ExerciseLog log = em.find(ExerciseLog.class, id);

                if (log != null) {
                    return log;
                }

                throw new ResourceNotFoundException(
                        "Exercise log not found"
                );

            } catch (PersistenceException e) {
                throw new DatabaseException(
                        "Get exercise log failed",
                        e
                );
            }
        }
    }

    @Override
    public List<ExerciseLog> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<ExerciseLog> query =
                        em.createQuery(
                                "SELECT l FROM ExerciseLog l",
                                ExerciseLog.class
                        );

                return query.getResultList();

            } catch (PersistenceException e) {
                throw new DatabaseException(
                        "Get exercise logs failed",
                        e
                );
            }
        }
    }

    @Override
    public ExerciseLog update(ExerciseLog log) {
        if (log == null || log.getId() == null) {
            throw new ValidationException(
                    "Exercise log id is required"
            );
        }

        ExerciseLog updated;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                ExerciseLog existing =
                        em.find(ExerciseLog.class, log.getId());

                if (existing == null) {
                    throw new ResourceNotFoundException(
                            "Exercise log not found"
                    );
                }

                updated = em.merge(log);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Update exercise log failed",
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
                    "Exercise log id is required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                ExerciseLog log = em.find(ExerciseLog.class, id);

                if (log == null) {
                    throw new ResourceNotFoundException(
                            "Exercise log not found"
                    );
                }

                em.remove(log);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Delete exercise log failed",
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