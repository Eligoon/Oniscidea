package app.daos;

import app.entities.GoogleCalendarConnection;
import app.exceptions.DatabaseException;
import app.exceptions.ResourceNotFoundException;
import app.exceptions.ValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;

import java.util.List;

public class GoogleCalendarConnectionDAO
        implements IDAO<GoogleCalendarConnection, Integer> {

    private final EntityManagerFactory emf;

    public GoogleCalendarConnectionDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }


    @Override
    public GoogleCalendarConnection create(
            GoogleCalendarConnection connection
    ) {

        if (connection == null) {
            throw new ValidationException(
                    "Google Calendar connection cannot be null"
            );
        }

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(connection);

            em.getTransaction().commit();

            return connection;

        } catch (PersistenceException e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new DatabaseException(
                    "Could not create Google Calendar connection",
                    e
            );

        } catch (RuntimeException e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } finally {
            em.close();
        }
    }

    @Override
    public GoogleCalendarConnection getById(Integer id) {

        if (id == null) {
            throw new ValidationException(
                    "Google Calendar connection ID cannot be null"
            );
        }

        EntityManager em = emf.createEntityManager();

        try {

            GoogleCalendarConnection connection =
                    em.find(GoogleCalendarConnection.class, id);

            if (connection == null) {
                throw new ResourceNotFoundException(
                        "Google Calendar connection not found"
                );
            }

            return connection;

        } catch (PersistenceException e) {

            throw new DatabaseException(
                    "Could not get Google Calendar connection",
                    e
            );

        } finally {
            em.close();
        }
    }

    @Override
    public List<GoogleCalendarConnection> getAll() {

        EntityManager em = emf.createEntityManager();

        try {

            return em.createQuery(
                    "SELECT g FROM GoogleCalendarConnection g",
                    GoogleCalendarConnection.class
            ).getResultList();

        } catch (PersistenceException e) {

            throw new DatabaseException(
                    "Could not get Google Calendar connections",
                    e
            );

        } finally {
            em.close();
        }
    }

    @Override
    public GoogleCalendarConnection update(
            GoogleCalendarConnection connection
    ) {

        if (connection == null || connection.getId() == null) {
            throw new ValidationException(
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
                throw new ResourceNotFoundException(
                        "Google Calendar connection not found"
                );
            }

            GoogleCalendarConnection updated =
                    em.merge(connection);

            em.getTransaction().commit();

            return updated;

        } catch (PersistenceException e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new DatabaseException(
                    "Could not update Google Calendar connection",
                    e
            );

        } catch (RuntimeException e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } finally {
            em.close();
        }
    }

    @Override
    public boolean delete(Integer id) {

        if (id == null) {
            throw new ValidationException(
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
                throw new ResourceNotFoundException(
                        "Google Calendar connection not found"
                );
            }

            em.remove(connection);

            em.getTransaction().commit();

            return true;

        } catch (PersistenceException e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new DatabaseException(
                    "Could not delete Google Calendar connection",
                    e
            );

        } catch (RuntimeException e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } finally {
            em.close();
        }
    }

    public GoogleCalendarConnection getByUserId(Integer userId) {

        if (userId == null) {
            throw new ValidationException(
                    "User ID cannot be null"
            );
        }

        EntityManager em = emf.createEntityManager();

        try {

            List<GoogleCalendarConnection> connections =
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
                return null;
            }

            return connections.get(0);

        } catch (PersistenceException e) {

            throw new DatabaseException(
                    "Could not get Google Calendar connection by user ID",
                    e
            );

        } finally {
            em.close();
        }
    }
}