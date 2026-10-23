package app.daos;

import app.entities.Exercise;
import app.exceptions.DatabaseException;
import app.exceptions.ResourceNotFoundException;
import app.exceptions.ValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ExerciseDAO implements IDAO<Exercise, Integer> {

    private final EntityManagerFactory emf;

    public ExerciseDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Exercise create(Exercise exercise) {
        if (exercise == null) {
            throw new ValidationException("Exercise is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                em.persist(exercise);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Create exercise failed",
                        e
                );

            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw e;
            }
        }

        return exercise;
    }

    @Override
    public Exercise getById(Integer id) {
        if (id == null) {
            throw new ValidationException("Exercise id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                Exercise exercise = em.find(Exercise.class, id);

                if (exercise != null) {
                    return exercise;
                }

                throw new ResourceNotFoundException(
                        "Exercise not found"
                );

            } catch (PersistenceException e) {
                throw new DatabaseException(
                        "Get exercise failed",
                        e
                );
            }
        }
    }

    @Override
    public List<Exercise> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<Exercise> query =
                        em.createQuery(
                                "SELECT e FROM Exercise e",
                                Exercise.class
                        );

                return query.getResultList();

            } catch (PersistenceException e) {
                throw new DatabaseException(
                        "Get exercises failed",
                        e
                );
            }
        }
    }

    @Override
    public Exercise update(Exercise exercise) {
        if (exercise == null || exercise.getId() == null) {
            throw new ValidationException(
                    "Exercise id is required"
            );
        }

        Exercise updated;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                Exercise existing =
                        em.find(Exercise.class, exercise.getId());

                if (existing == null) {
                    throw new ResourceNotFoundException(
                            "Exercise not found"
                    );
                }

                updated = em.merge(exercise);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Update exercise failed",
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
                    "Exercise id is required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                Exercise exercise = em.find(Exercise.class, id);

                if (exercise == null) {
                    throw new ResourceNotFoundException(
                            "Exercise not found"
                    );
                }

                em.remove(exercise);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Delete exercise failed",
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


    public List<Exercise> getAvailableExercises(Integer userId) {
        if (userId == null) {
            throw new ValidationException("User id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<Exercise> query = em.createQuery(
                        "SELECT DISTINCT e FROM Exercise e " +
                                "LEFT JOIN FETCH e.bodyParts " +
                                "WHERE e.owner IS NULL OR e.owner.id = :userId",
                        Exercise.class
                );

                query.setParameter("userId", userId);

                return query.getResultList();

            } catch (PersistenceException e) {
                throw new DatabaseException(
                        "Get available exercises failed",
                        e
                );
            }
        }
    }


    public Exercise updateOwned(
            Integer id,
            Integer userId,
            String name,
            String description
    ) {
        if (id == null || userId == null) {
            throw new ValidationException(
                    "Exercise id and user id are required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                TypedQuery<Exercise> query = em.createQuery(
                        "SELECT e FROM Exercise e " +
                                "WHERE e.id = :id AND e.owner.id = :userId",
                        Exercise.class
                );

                query.setParameter("id", id);
                query.setParameter("userId", userId);

                List<Exercise> results = query.getResultList();

                if (results.isEmpty()) {
                    throw new ResourceNotFoundException(
                            "Custom exercise not found"
                    );
                }

                Exercise existing = results.get(0);

                existing.setName(name);
                existing.setDescription(description);

                em.getTransaction().commit();

                return existing;

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Update exercise failed",
                        e
                );

            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw e;
            }
        }
    }

    public boolean deleteOwned(Integer id, Integer userId) {
        if (id == null || userId == null) {
            throw new ValidationException(
                    "Exercise id and user id are required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                TypedQuery<Exercise> query = em.createQuery(
                        "SELECT e FROM Exercise e " +
                                "WHERE e.id = :id AND e.owner.id = :userId",
                        Exercise.class
                );

                query.setParameter("id", id);
                query.setParameter("userId", userId);

                List<Exercise> results = query.getResultList();

                if (results.isEmpty()) {
                    throw new ResourceNotFoundException(
                            "Custom exercise not found"
                    );
                }

                em.remove(results.get(0));
                em.getTransaction().commit();

                return true;

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Delete exercise failed",
                        e
                );

            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw e;
            }
        }
    }
}