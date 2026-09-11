package app.daos;

import app.entities.GoogleCalendarConnection;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;

public class GoogleCalendarConnectionDAO
        implements IDAO<GoogleCalendarConnection, Integer> {

    private final EntityManagerFactory emf;

    public GoogleCalendarConnectionDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public GoogleCalendarConnection create(GoogleCalendarConnection connection) {

        if (connection == null) {
            throw new ApiException(400, "Google Calendar connection cannot be null");
        }

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(connection);

            em.getTransaction().commit();

            return connection;

        } catch (RuntimeException e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new ApiException(
                    500,
                    "Could not create Google Calendar connection"
            );

        } finally {
            em.close();
        }
    }

    @Override
    public GoogleCalendarConnection getById(Integer id) {

        if (id == null) {
            throw new ApiException(400, "Google Calendar connection ID cannot be null");
        }

        EntityManager em = emf.createEntityManager();

        try {

            GoogleCalendarConnection connection =
                    em.find(GoogleCalendarConnection.class, id);

            if (connection == null) {
                throw new ApiException(
                        404,
                        "Google Calendar connection not found"
                );
            }

            return connection;

        } finally {
            em.close();
        }
    }

    @Override
    public java.util.List<GoogleCalendarConnection> getAll() {

        EntityManager em = emf.createEntityManager();

        try {

            return em.createQuery(
                    "SELECT g FROM GoogleCalendarConnection g",
                    GoogleCalendarConnection.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public GoogleCalendarConnection update(
            GoogleCalendarConnection connection
    ) {

        if (connection == null || connection.getId() == null) {
            throw new ApiException(
                    400,
                    "Google Calendar connection and ID are required"
            );
        }

        EntityManager em = emf.createEntityManager();

        try {

            em.getTransaction().begin();

            GoogleCalendarConnection existing =
                    em.find(
                            GoogleCalendarConnection.class,
                            connection.getId()
                    );

            if (existing == null) {
                throw new ApiException(
                        404,
                        "Google Calendar connection not found"
                );
            }

            GoogleCalendarConnection updated =
                    em.merge(connection);

            em.getTransaction().commit();

            return updated;

        } catch (ApiException e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } catch (RuntimeException e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new ApiException(
                    500,
                    "Could not update Google Calendar connection"
            );

        } finally {
            em.close();
        }
    }

    @Override
    public boolean delete(Integer id) {

        if (id == null) {
            throw new ApiException(
                    400,
                    "Google Calendar connection ID cannot be null"
            );
        }

        EntityManager em = emf.createEntityManager();

        try {

            em.getTransaction().begin();

            GoogleCalendarConnection connection =
                    em.find(
                            GoogleCalendarConnection.class,
                            id
                    );

            if (connection == null) {
                throw new ApiException(
                        404,
                        "Google Calendar connection not found"
                );
            }

            em.remove(connection);

            em.getTransaction().commit();

            return true;

        } catch (ApiException e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } catch (RuntimeException e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new ApiException(
                    500,
                    "Could not delete Google Calendar connection"
            );

        } finally {
            em.close();
        }
    }

    public GoogleCalendarConnection getByUserId(Integer userId) {

        if (userId == null) {
            throw new ApiException(400, "User ID cannot be null");
        }

        EntityManager em = emf.createEntityManager();

        try {

            java.util.List<GoogleCalendarConnection> connections =
                    em.createQuery(
                                    """
                                    SELECT g
                                    FROM GoogleCalendarConnection g
                                    WHERE g.user.id = :userId
                                    """,
                                    GoogleCalendarConnection.class
                            )
                            .setParameter("userId", userId)
                            .getResultList();

            if (connections.isEmpty()) {
                throw new ApiException(
                        404,
                        "Google Calendar connection not found"
                );
            }

            return connections.get(0);

        } finally {
            em.close();
        }
    }
}