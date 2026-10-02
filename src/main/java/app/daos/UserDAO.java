package app.daos;

import app.entities.User;
import app.exceptions.ApiException;
import app.exceptions.DatabaseException;
import app.exceptions.ResourceNotFoundException;
import app.exceptions.ValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class UserDAO implements IDAO<User, Integer> {

    private final EntityManagerFactory emf;

    public UserDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public User create(User user) {
        if (user == null) {
            throw new ValidationException("User is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                em.persist(user);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Create user failed",
                        e
                );

            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw e;
            }
        }

        return user;
    }

    /*
     * Used when registering a new user.
     *
     * The User constructor hashes the password with BCrypt,
     * so the plain password never gets stored in the database.
     */
    public User createUser(String name, String email, String password) {

        if (name == null || name.isBlank()) {
            throw new ValidationException("Name is required");
        }

        if (email == null || email.isBlank()) {
            throw new ValidationException("Email is required");
        }

        if (password == null || password.isBlank()) {
            throw new ValidationException("Password is required");
        }

        User user = new User(name, email, password);

        return create(user);
    }

    /*
     * Used when logging in.
     *
     * First finds the user by email.
     * Then User.verifyPassword() checks the password using BCrypt.
     */
    public User getVerifiedUser(String email, String password) {

        if (email == null || email.isBlank()) {
            throw new ValidationException("Email is required");
        }

        if (password == null || password.isBlank()) {
            throw new ValidationException("Password is required");
        }

        try (EntityManager em = emf.createEntityManager()) {

            try {
                TypedQuery<User> query = em.createQuery(
                        "SELECT u FROM User u WHERE u.email = :email",
                        User.class
                );

                query.setParameter("email", email);

                List<User> users = query.getResultList();

                if (users.isEmpty()) {
                    throw new ApiException(
                            401,
                            "Invalid email or password"
                    );
                }

                User user = users.get(0);

                if (!user.verifyPassword(password)) {
                    throw new ApiException(
                            401,
                            "Invalid email or password"
                    );
                }

                return user;

            } catch (PersistenceException e) {
                throw new DatabaseException(
                        "Verify user failed",
                        e
                );
            }
        }
    }

    @Override
    public User getById(Integer id) {
        if (id == null) {
            throw new ValidationException("User id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                User user = em.find(User.class, id);

                if (user != null) {
                    return user;
                }

                throw new ResourceNotFoundException(
                        "User not found"
                );

            } catch (PersistenceException e) {
                throw new DatabaseException(
                        "Get user failed",
                        e
                );
            }
        }
    }

    @Override
    public List<User> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<User> query =
                        em.createQuery(
                                "SELECT u FROM User u",
                                User.class
                        );

                return query.getResultList();

            } catch (PersistenceException e) {
                throw new DatabaseException(
                        "Get users failed",
                        e
                );
            }
        }
    }

    @Override
    public User update(User user) {
        if (user == null || user.getId() == null) {
            throw new ValidationException(
                    "User id is required"
            );
        }

        User updated;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                User existing = em.find(
                        User.class,
                        user.getId()
                );

                if (existing == null) {
                    throw new ResourceNotFoundException(
                            "User not found"
                    );
                }

                updated = em.merge(user);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Update user failed",
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
                    "User id is required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                User user = em.find(User.class, id);

                if (user == null) {
                    throw new ResourceNotFoundException(
                            "User not found"
                    );
                }

                em.remove(user);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Delete user failed",
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