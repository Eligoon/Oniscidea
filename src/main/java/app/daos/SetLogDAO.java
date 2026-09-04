package app.daos;

import app.entities.SetLog;
import app.exceptions.ApiException;
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
            throw new ApiException(400, "Set log is required");
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

                throw new ApiException(
                        500,
                        "Create set log failed: " + e.getMessage()
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
            throw new ApiException(400, "Set log id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                SetLog setLog = em.find(SetLog.class, id);

                if (setLog != null) {
                    return setLog;
                }

                throw new ApiException(404, "Set log not found");

            } catch (PersistenceException e) {
                throw new ApiException(
                        500,
                        "Get set log failed: " + e.getMessage()
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
                throw new ApiException(
                        500,
                        "Get set logs failed: " + e.getMessage()
                );
            }
        }
    }

    @Override
    public SetLog update(SetLog setLog) {
        if (setLog == null || setLog.getId() == null) {
            throw new ApiException(400, "Set log id is required");
        }

        SetLog updated;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                SetLog existing =
                        em.find(SetLog.class, setLog.getId());

                if (existing == null) {
                    throw new ApiException(404, "Set log not found");
                }

                updated = em.merge(setLog);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Update set log failed: " + e.getMessage()
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
            throw new ApiException(400, "Set log id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                SetLog setLog = em.find(SetLog.class, id);

                if (setLog == null) {
                    throw new ApiException(404, "Set log not found");
                }

                em.remove(setLog);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Delete set log failed: " + e.getMessage()
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
