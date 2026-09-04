package app.daos;

import app.entities.TrainingProgram;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class TrainingProgramDAO
        implements IDAO<TrainingProgram, Integer> {

    private final EntityManagerFactory emf;

    public TrainingProgramDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public TrainingProgram create(TrainingProgram program) {
        if (program == null) {
            throw new ApiException(400, "Training program is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                em.persist(program);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Create training program failed: " + e.getMessage()
                );

            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw e;
            }
        }

        return program;
    }

    @Override
    public TrainingProgram getById(Integer id) {
        if (id == null) {
            throw new ApiException(400, "Training program id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                TrainingProgram program =
                        em.find(TrainingProgram.class, id);

                if (program != null) {
                    return program;
                }

                throw new ApiException(
                        404,
                        "Training program not found"
                );

            } catch (PersistenceException e) {
                throw new ApiException(
                        500,
                        "Get training program failed: " + e.getMessage()
                );
            }
        }
    }

    @Override
    public List<TrainingProgram> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<TrainingProgram> query =
                        em.createQuery(
                                "SELECT p FROM TrainingProgram p",
                                TrainingProgram.class
                        );

                return query.getResultList();

            } catch (PersistenceException e) {
                throw new ApiException(
                        500,
                        "Get training programs failed: " + e.getMessage()
                );
            }
        }
    }

    @Override
    public TrainingProgram update(TrainingProgram program) {
        if (program == null || program.getId() == null) {
            throw new ApiException(
                    400,
                    "Training program id is required"
            );
        }

        TrainingProgram updated;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                TrainingProgram existing =
                        em.find(TrainingProgram.class, program.getId());

                if (existing == null) {
                    throw new ApiException(
                            404,
                            "Training program not found"
                    );
                }

                updated = em.merge(program);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Update training program failed: " + e.getMessage()
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
                    "Training program id is required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                TrainingProgram program =
                        em.find(TrainingProgram.class, id);

                if (program == null) {
                    throw new ApiException(
                            404,
                            "Training program not found"
                    );
                }

                em.remove(program);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Delete training program failed: " + e.getMessage()
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