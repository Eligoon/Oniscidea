package app.daos;

import app.entities.ExerciseLog;
import app.exceptions.ApiException;
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
            throw new ApiException(400, "Exercise log is required");
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

                throw new ApiException(
                        500,
                        "Create exercise log failed: " + e.getMessage()
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
            throw new ApiException(400, "Exercise log id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                ExerciseLog log = em.find(ExerciseLog.class, id);

                if (log != null) {
                    return log;
                }

                throw new ApiException(404, "Exercise log not found");

            } catch (PersistenceException e) {
                throw new ApiException(
                        500,
                        "Get exercise log failed: " + e.getMessage()
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
                throw new ApiException(
                        500,
                        "Get exercise logs failed: " + e.getMessage()
                );
            }
        }
    }

    @Override
    public ExerciseLog update(ExerciseLog log) {
        if (log == null || log.getId() == null) {
            throw new ApiException(400, "Exercise log id is required");
        }

        ExerciseLog updated;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                ExerciseLog existing =
                        em.find(ExerciseLog.class, log.getId());

                if (existing == null) {
                    throw new ApiException(404, "Exercise log not found");
                }

                updated = em.merge(log);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Update exercise log failed: " + e.getMessage()
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
            throw new ApiException(400, "Exercise log id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                ExerciseLog log = em.find(ExerciseLog.class, id);

                if (log == null) {
                    throw new ApiException(404, "Exercise log not found");
                }

                em.remove(log);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Delete exercise log failed: " + e.getMessage()
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