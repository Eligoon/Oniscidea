package app.daos;

import app.entities.BodyPart;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class BodyPartDAO implements IDAO<BodyPart, Integer> {

    private final EntityManagerFactory emf;

    public BodyPartDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public BodyPart create(BodyPart bodyPart) {
        if (bodyPart == null) {
            throw new ApiException(400, "Body part is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                em.persist(bodyPart);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Create body part failed: " + e.getMessage()
                );

            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw e;
            }
        }

        return bodyPart;
    }

    @Override
    public BodyPart getById(Integer id) {
        if (id == null) {
            throw new ApiException(400, "Body part id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                BodyPart bodyPart = em.find(BodyPart.class, id);

                if (bodyPart != null) {
                    return bodyPart;
                }

                throw new ApiException(404, "Body part not found");

            } catch (PersistenceException e) {
                throw new ApiException(
                        500,
                        "Get body part failed: " + e.getMessage()
                );
            }
        }
    }

    @Override
    public List<BodyPart> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<BodyPart> query =
                        em.createQuery(
                                "SELECT b FROM BodyPart b",
                                BodyPart.class
                        );

                return query.getResultList();

            } catch (PersistenceException e) {
                throw new ApiException(
                        500,
                        "Get body parts failed: " + e.getMessage()
                );
            }
        }
    }

    @Override
    public BodyPart update(BodyPart bodyPart) {
        if (bodyPart == null || bodyPart.getId() == null) {
            throw new ApiException(400, "Body part id is required");
        }

        BodyPart updated;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                BodyPart existing =
                        em.find(BodyPart.class, bodyPart.getId());

                if (existing == null) {
                    throw new ApiException(404, "Body part not found");
                }

                updated = em.merge(bodyPart);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Update body part failed: " + e.getMessage()
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
            throw new ApiException(400, "Body part id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                BodyPart bodyPart = em.find(BodyPart.class, id);

                if (bodyPart == null) {
                    throw new ApiException(404, "Body part not found");
                }

                em.remove(bodyPart);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Delete body part failed: " + e.getMessage()
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