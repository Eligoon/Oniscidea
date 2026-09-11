package app.daos;

import app.entities.User;
import app.exceptions.ApiException;
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
            throw new ApiException(400, "User is required");
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

                throw new ApiException(
                        500,
                        "Create user failed: " + e.getMessage()
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
            throw new ApiException(400, "Name is required");
        }

        if (email == null || email.isBlank()) {
            throw new ApiException(400, "Email is required");
        }

        if (password == null || password.isBlank()) {
            throw new ApiException(400, "Password is required");
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
            throw new ApiException(400, "Email is required");
        }

        if (password == null || password.isBlank()) {
            throw new ApiException(400, "Password is required");
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
                throw new ApiException(
                        500,
                        "Verify user failed: " + e.getMessage()
                );
            }
        }
    }

    @Override
    public User getById(Integer id) {
        if (id == null) {
            throw new ApiException(400, "User id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                User user = em.find(User.class, id);

                if (user != null) {
                    return user;
                }

                throw new ApiException(404, "User not found");

            } catch (PersistenceException e) {
                throw new ApiException(
                        500,
                        "Get user failed: " + e.getMessage()
                );
            }
        }
    }

    @Override
    public List<User> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<User> query =
                        em.createQuery("SELECT u FROM User u", User.class);

                return query.getResultList();

            } catch (PersistenceException e) {
                throw new ApiException(
                        500,
                        "Get users failed: " + e.getMessage()
                );
            }
        }
    }

    @Override
    public User update(User user) {
        if (user == null || user.getId() == null) {
            throw new ApiException(400, "User id is required");
        }

        User updated;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                User existing = em.find(User.class, user.getId());

                if (existing == null) {
                    throw new ApiException(404, "User not found");
                }

                updated = em.merge(user);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Update user failed: " + e.getMessage()
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
            throw new ApiException(400, "User id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                User user = em.find(User.class, id);

                if (user == null) {
                    throw new ApiException(404, "User not found");
                }

                em.remove(user);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Delete user failed: " + e.getMessage()
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