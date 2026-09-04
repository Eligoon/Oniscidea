package app.daos;

import app.entities.TrainingCalendar;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class TrainingCalendarDAO
        implements IDAO<TrainingCalendar, Integer> {

    private final EntityManagerFactory emf;

    public TrainingCalendarDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public TrainingCalendar create(TrainingCalendar calendar) {
        if (calendar == null) {
            throw new ApiException(400, "Calendar is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                em.persist(calendar);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Create calendar failed: " + e.getMessage()
                );

            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw e;
            }
        }

        return calendar;
    }

    @Override
    public TrainingCalendar getById(Integer id) {
        if (id == null) {
            throw new ApiException(400, "Calendar id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                TrainingCalendar calendar =
                        em.find(TrainingCalendar.class, id);

                if (calendar != null) {
                    return calendar;
                }

                throw new ApiException(404, "Calendar not found");

            } catch (PersistenceException e) {
                throw new ApiException(
                        500,
                        "Get calendar failed: " + e.getMessage()
                );
            }
        }
    }

    @Override
    public List<TrainingCalendar> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<TrainingCalendar> query =
                        em.createQuery(
                                "SELECT c FROM TrainingCalendar c",
                                TrainingCalendar.class
                        );

                return query.getResultList();

            } catch (PersistenceException e) {
                throw new ApiException(
                        500,
                        "Get calendars failed: " + e.getMessage()
                );
            }
        }
    }

    @Override
    public TrainingCalendar update(TrainingCalendar calendar) {
        if (calendar == null || calendar.getId() == null) {
            throw new ApiException(400, "Calendar id is required");
        }

        TrainingCalendar updated;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                TrainingCalendar existing =
                        em.find(TrainingCalendar.class, calendar.getId());

                if (existing == null) {
                    throw new ApiException(404, "Calendar not found");
                }

                updated = em.merge(calendar);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Update calendar failed: " + e.getMessage()
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
            throw new ApiException(400, "Calendar id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                TrainingCalendar calendar =
                        em.find(TrainingCalendar.class, id);

                if (calendar == null) {
                    throw new ApiException(404, "Calendar not found");
                }

                em.remove(calendar);
                em.getTransaction().commit();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new ApiException(
                        500,
                        "Delete calendar failed: " + e.getMessage()
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