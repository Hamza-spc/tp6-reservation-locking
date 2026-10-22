package com.example.service;

import com.example.model.Reservation;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.OptimisticLockException;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import java.util.List;

public class ReservationServiceImpl implements ReservationService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("reservationPU");

    @Override
    public Reservation creerReservation(Reservation reservation) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(reservation);
            em.getTransaction().commit();
            return reservation;
        } finally {
            em.close();
        }
    }

    /**
     * La fusion de l'entite detachee propage sa version : si une autre transaction a
     * deja modifie la ligne, la version en base ne correspond plus et Hibernate leve
     * une OptimisticLockException au moment du commit.
     */
    @Override
    public Reservation modifierReservation(Reservation reservation) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (em.find(Reservation.class, reservation.getId()) == null) {
                throw new IllegalArgumentException("Reservation non trouvee");
            }
            Reservation managed = em.merge(reservation);
            em.getTransaction().commit();
            return managed;
        } catch (RollbackException e) {
            rollback(em);
            throw asOptimisticLockException(e);
        } catch (OptimisticLockException e) {
            rollback(em);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Reservation trouverParId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Reservation.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Reservation> listerReservations() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void fermer() {
        if (emf.isOpen()) {
            emf.close();
        }
    }

    private void rollback(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    private OptimisticLockException asOptimisticLockException(RollbackException e) {
        Throwable cause = e.getCause();
        while (cause != null) {
            if (cause instanceof OptimisticLockException) {
                return (OptimisticLockException) cause;
            }
            cause = cause.getCause();
        }
        throw e;
    }
}
