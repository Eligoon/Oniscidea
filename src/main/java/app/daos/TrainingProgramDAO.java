package app.daos;

import app.entities.TrainingProgram;
import app.exceptions.DatabaseException;
import app.exceptions.ResourceNotFoundException;
import app.exceptions.ValidationException;
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
            throw new ValidationException(
                    "Training program is required"
            );
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

                throw new DatabaseException(
                        "Create training program failed",
                        e
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
            throw new ValidationException(
                    "Training program id is required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                TrainingProgram program =
                        em.find(TrainingProgram.class, id);

                if (program != null) {
                    return program;
                }

                throw new ResourceNotFoundException(
                        "Training program not found"
                );

            } catch (PersistenceException e) {
                throw new DatabaseException(
                        "Get training program failed",
                        e
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
                throw new DatabaseException(
                        "Get training programs failed",
                        e
                );
            }
        }
    }

    @Override
    public TrainingProgram update(TrainingProgram program) {
        if (program == null || program.getId() == null) {
            throw new ValidationException(
                    "Training program id is required"
            );
        }

        TrainingProgram updated;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                TrainingProgram existing =
                        em.find(
                                TrainingProgram.class,
                                program.getId()
                        );

                if (existing == null) {
                    throw new ResourceNotFoundException(
                            "Training program not found"
                    );
                }

                updated = em.merge(program);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Update training program failed",
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
                    "Training program id is required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                TrainingProgram program =
                        em.find(
                                TrainingProgram.class,
                                id
                        );

                if (program == null) {
                    throw new ResourceNotFoundException(
                            "Training program not found"
                    );
                }

                em.remove(program);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException(
                        "Delete training program failed",
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