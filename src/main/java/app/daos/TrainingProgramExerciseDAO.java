package app.daos;

import app.entities.TrainingProgramExercise;
import app.exceptions.ApiException;
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
            throw new ApiException(
                    400,
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

                throw new ApiException(
                        500,
                        "Create training program exercise failed: "
                                + e.getMessage()
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
            throw new ApiException(
                    400,
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

                throw new ApiException(
                        404,
                        "Training program exercise not found"
                );

            } catch (PersistenceException e) {
                throw new ApiException(
                        500,
                        "Get training program exercise failed: "
                                + e.getMessage()
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
                throw new ApiException(
                        500,
                        "Get training program exercises failed: "
                                + e.getMessage()
                );
            }
        }
    }

    @Override
    public TrainingProgramExercise update(
            TrainingProgramExercise entity) {

        if (entity == null || entity.getId() == null) {
            throw new ApiException(
                    400,
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
                    throw new ApiException(
                            404,
                            "Training program exercise not found"
                    );
                }

                updated = em.merge(entity);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Update training program exercise failed: "
                                + e.getMessage()
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
            throw new ApiException(
                    400,
                    "Training program exercise id is required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                TrainingProgramExercise entity =
                        em.find(TrainingProgramExercise.class, id);

                if (entity == null) {
                    throw new ApiException(
                            404,
                            "Training program exercise not found"
                    );
                }

                em.remove(entity);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Delete training program exercise failed: "
                                + e.getMessage()
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
