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
}