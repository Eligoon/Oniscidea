package app.daos;

import app.entities.Profile;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ProfileDAO implements IDAO<Profile, Integer> {

    private final EntityManagerFactory emf;

    public ProfileDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Profile create(Profile profile) {
        if (profile == null) {
            throw new ApiException(400, "Profile is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                em.persist(profile);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Create profile failed: " + e.getMessage()
                );

            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw e;
            }
        }

        return profile;
    }

    @Override
    public Profile getById(Integer id) {
        if (id == null) {
            throw new ApiException(400, "Profile id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                Profile profile = em.find(Profile.class, id);

                if (profile != null) {
                    return profile;
                }

                throw new ApiException(404, "Profile not found");

            } catch (PersistenceException e) {
                throw new ApiException(
                        500,
                        "Get profile failed: " + e.getMessage()
                );
            }
        }
    }

    @Override
    public List<Profile> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<Profile> query =
                        em.createQuery(
                                "SELECT p FROM Profile p",
                                Profile.class
                        );

                return query.getResultList();

            } catch (PersistenceException e) {
                throw new ApiException(
                        500,
                        "Get profiles failed: " + e.getMessage()
                );
            }
        }
    }

    @Override
    public Profile update(Profile profile) {
        if (profile == null || profile.getId() == null) {
            throw new ApiException(400, "Profile id is required");
        }

        Profile updated;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                Profile existing =
                        em.find(Profile.class, profile.getId());

                if (existing == null) {
                    throw new ApiException(404, "Profile not found");
                }

                updated = em.merge(profile);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Update profile failed: " + e.getMessage()
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
            throw new ApiException(400, "Profile id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                Profile profile = em.find(Profile.class, id);

                if (profile == null) {
                    throw new ApiException(404, "Profile not found");
                }

                em.remove(profile);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Delete profile failed: " + e.getMessage()
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