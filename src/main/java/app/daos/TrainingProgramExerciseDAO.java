package app.daos;

import app.entities.TrainingProgramExercise;
import app.exceptions.DatabaseException;
import app.exceptions.ResourceNotFoundException;
import app.exceptions.ValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class TrainingProgramExerciseDAO
        implements IDAO<TrainingProgramExercise, Integer> {

    private final EntityManagerFactory emf;

    public TrainingProgramExerciseDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public TrainingProgramExercise create(TrainingProgramExercise entity) {
        if (entity == null) {
            throw new ValidationException(
                    "Training program exercise is required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                em.persist(entity);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Create training program exercise failed",
                        e
                );

            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw e;
            }
        }

        return entity;
    }

    @Override
    public TrainingProgramExercise getById(Integer id) {
        if (id == null) {
            throw new ValidationException(
                    "Training program exercise id is required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                TrainingProgramExercise entity =
                        em.find(TrainingProgramExercise.class, id);

                if (entity != null) {
                    return entity;
                }

                throw new ResourceNotFoundException(
                        "Training program exercise not found"
                );

            } catch (PersistenceException e) {
                throw new DatabaseException(
                        "Get training program exercise failed",
                        e
                );
            }
        }
    }

    @Override
    public List<TrainingProgramExercise> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<TrainingProgramExercise> query =
                        em.createQuery(
                                "SELECT e FROM TrainingProgramExercise e",
                                TrainingProgramExercise.class
                        );

                return query.getResultList();

            } catch (PersistenceException e) {
                throw new DatabaseException(
                        "Get training program exercises failed",
                        e
                );
            }
        }
    }

    @Override
    public TrainingProgramExercise update(
            TrainingProgramExercise entity) {

        if (entity == null || entity.getId() == null) {
            throw new ValidationException(
                    "Training program exercise id is required"
            );
        }

        TrainingProgramExercise updated;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                TrainingProgramExercise existing =
                        em.find(
                                TrainingProgramExercise.class,
                                entity.getId()
                        );

                if (existing == null) {
                    throw new ResourceNotFoundException(
                            "Training program exercise not found"
                    );
                }

                updated = em.merge(entity);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Update training program exercise failed",
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
                    "Training program exercise id is required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                TrainingProgramExercise entity =
                        em.find(TrainingProgramExercise.class, id);

                if (entity == null) {
                    throw new ResourceNotFoundException(
                            "Training program exercise not found"
                    );
                }

                em.remove(entity);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Delete training program exercise failed",
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