package app.daos;

import app.entities.TrainingSession;
import app.exceptions.DatabaseException;
import app.exceptions.ResourceNotFoundException;
import app.exceptions.ValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class TrainingSessionDAO
        implements IDAO<TrainingSession, Integer> {

    private final EntityManagerFactory emf;

    public TrainingSessionDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public TrainingSession create(TrainingSession session) {
        if (session == null) {
            throw new ValidationException(
                    "Training session is required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                em.persist(session);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Create training session failed",
                        e
                );

            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw e;
            }
        }

        return session;
    }

    @Override
    public TrainingSession getById(Integer id) {
        if (id == null) {
            throw new ValidationException(
                    "Training session id is required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                TrainingSession session =
                        em.find(TrainingSession.class, id);

                if (session != null) {
                    return session;
                }

                throw new ResourceNotFoundException(
                        "Training session not found"
                );

            } catch (PersistenceException e) {
                throw new DatabaseException(
                        "Get training session failed",
                        e
                );
            }
        }
    }

    @Override
    public List<TrainingSession> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<TrainingSession> query =
                        em.createQuery(
                                "SELECT s FROM TrainingSession s",
                                TrainingSession.class
                        );

                return query.getResultList();

            } catch (PersistenceException e) {
                throw new DatabaseException(
                        "Get training sessions failed",
                        e
                );
            }
        }
    }

    @Override
    public TrainingSession update(TrainingSession session) {
        if (session == null || session.getId() == null) {
            throw new ValidationException(
                    "Training session id is required"
            );
        }

        TrainingSession updated;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                TrainingSession existing =
                        em.find(
                                TrainingSession.class,
                                session.getId()
                        );

                if (existing == null) {
                    throw new ResourceNotFoundException(
                            "Training session not found"
                    );
                }

                updated = em.merge(session);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Update training session failed",
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
                    "Training session id is required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                TrainingSession session =
                        em.find(
                                TrainingSession.class,
                                id
                        );

                if (session == null) {
                    throw new ResourceNotFoundException(
                            "Training session not found"
                    );
                }

                em.remove(session);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Delete training session failed",
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