package app.daos;

import app.entities.GoogleCalendarConnection;
import app.entities.Note;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class GoogleCalendarConnectionDAO implements IDAO<GoogleCalendarConnection, Integer> {

    private final EntityManagerFactory emf;

    public GoogleCalendarConnectionDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public GoogleCalendarConnection create(GoogleCalendarConnection connection) {
        if (connection == null) {
            throw new ApiException(400, "Calendar connection is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                em.persist(connection);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Create calendar connection failed: " + e.getMessage()
                );

            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw e;
            }
        }

        return connection;
    }


    @Override
    public GoogleCalendarConnection getById(Integer id) {
        if (id == null) {
            throw new ApiException(400, "Calendar connection id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                GoogleCalendarConnection connection = em.find(GoogleCalendarConnection.class, id);

                if (connection != null) {
                    return connection;
                }

                throw new ApiException(404, "Calendar connection not found");

            } catch (PersistenceException e) {
                throw new ApiException(
                        500,
                        "Get calendar connection failed: " + e.getMessage()
                );
            }
        }
    }

    @Override
    public List<GoogleCalendarConnection> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<GoogleCalendarConnection> query =
                        em.createQuery(
                                "SELECT n FROM Note n",
                                GoogleCalendarConnection.class
                        );

                return query.getResultList();

            } catch (PersistenceException e) {
                throw new ApiException(
                        500,
                        "Get calendar connections failed: " + e.getMessage()
                );
            }
        }
    }


    @Override
    public GoogleCalendarConnection update(GoogleCalendarConnection connection) {
        if (connection == null || connection.getId() == null) {
            throw new ApiException(400, "Calendar connection id is required");
        }

        GoogleCalendarConnection updated;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                GoogleCalendarConnection existing =
                        em.find(GoogleCalendarConnection.class, connection.getId());

                if (existing == null) {
                    throw new ApiException(404, "Calendar connection not found");
                }

                updated = em.merge(connection);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Update calendar connection failed: " + e.getMessage()
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
            throw new ApiException(400, "Calendar connection id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                GoogleCalendarConnection connection = em.find(GoogleCalendarConnection.class, id);

                if (connection == null) {
                    throw new ApiException(404, "Calendar connection not found");
                }

                em.remove(connection);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Delete calendar connection failed: " + e.getMessage()
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


    public GoogleCalendarConnection getByUserId(Integer userId) {
        if (userId == null) {
            throw new ApiException(400, "User ID cannot be null");
        }

        try (EntityManager em = emf.createEntityManager()) {

            TypedQuery<GoogleCalendarConnection> query =
                    em.createQuery(
                            "SELECT g FROM GoogleCalendarConnection g " +
                                    "WHERE g.user.id = :userId " +
                                    "AND g.deletedAt IS NULL",
                            GoogleCalendarConnection.class
                    );

            query.setParameter("userId", userId);

            List<GoogleCalendarConnection> results = query.getResultList();

            if (results.isEmpty()) {
                throw new ApiException(
                        404,
                        "Google Calendar connection not found"
                );
            }

            return results.get(0);

        } catch (PersistenceException ex) {
            throw new ApiException(
                    500,
                    "Could not retrieve Google Calendar connection"
            );
        }
    }
}
