package app.daos;

import app.entities.SetLog;
import app.exceptions.DatabaseException;
import app.exceptions.ResourceNotFoundException;
import app.exceptions.ValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class SetLogDAO implements IDAO<SetLog, Integer> {

    private final EntityManagerFactory emf;

    public SetLogDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public SetLog create(SetLog setLog) {
        if (setLog == null) {
            throw new ValidationException("Set log is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                em.persist(setLog);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Create set log failed",
                        e
                );

            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw e;
            }
        }

        return setLog;
    }

    @Override
    public SetLog getById(Integer id) {
        if (id == null) {
            throw new ValidationException(
                    "Set log id is required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                SetLog setLog = em.find(SetLog.class, id);

                if (setLog != null) {
                    return setLog;
                }

                throw new ResourceNotFoundException(
                        "Set log not found"
                );

            } catch (PersistenceException e) {
                throw new DatabaseException(
                        "Get set log failed",
                        e
                );
            }
        }
    }

    @Override
    public List<SetLog> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<SetLog> query =
                        em.createQuery(
                                "SELECT s FROM SetLog s",
                                SetLog.class
                        );

                return query.getResultList();

            } catch (PersistenceException e) {
                throw new DatabaseException(
                        "Get set logs failed",
                        e
                );
            }
        }
    }

    @Override
    public SetLog update(SetLog setLog) {
        if (setLog == null || setLog.getId() == null) {
            throw new ValidationException(
                    "Set log id is required"
            );
        }

        SetLog updated;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                SetLog existing =
                        em.find(SetLog.class, setLog.getId());

                if (existing == null) {
                    throw new ResourceNotFoundException(
                            "Set log not found"
                    );
                }

                updated = em.merge(setLog);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Update set log failed",
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
                    "Set log id is required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                SetLog setLog = em.find(SetLog.class, id);

                if (setLog == null) {
                    throw new ResourceNotFoundException(
                            "Set log not found"
                    );
                }

                em.remove(setLog);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Delete set log failed",
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