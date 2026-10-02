package app.daos;

import app.entities.BodyPart;
import app.exceptions.DatabaseException;
import app.exceptions.ResourceNotFoundException;
import app.exceptions.ValidationException;
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
            throw new ValidationException("Body part is required");
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

                throw new DatabaseException(
                        "Create body part failed",
                        e
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
            throw new ValidationException("Body part id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                BodyPart bodyPart = em.find(BodyPart.class, id);

                if (bodyPart != null) {
                    return bodyPart;
                }

                throw new ResourceNotFoundException(
                        "Body part not found"
                );

            } catch (PersistenceException e) {
                throw new DatabaseException(
                        "Get body part failed",
                        e
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
                throw new DatabaseException(
                        "Get body parts failed",
                        e
                );
            }
        }
    }

    @Override
    public BodyPart update(BodyPart bodyPart) {
        if (bodyPart == null || bodyPart.getId() == null) {
            throw new ValidationException(
                    "Body part id is required"
            );
        }

        BodyPart updated;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                BodyPart existing =
                        em.find(BodyPart.class, bodyPart.getId());

                if (existing == null) {
                    throw new ResourceNotFoundException(
                            "Body part not found"
                    );
                }

                updated = em.merge(bodyPart);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Update body part failed",
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
                    "Body part id is required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                BodyPart bodyPart = em.find(BodyPart.class, id);

                if (bodyPart == null) {
                    throw new ResourceNotFoundException(
                            "Body part not found"
                    );
                }

                em.remove(bodyPart);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Delete body part failed",
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