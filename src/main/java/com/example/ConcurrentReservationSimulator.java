package com.example;

import com.example.model.Reservation;
import com.example.service.ReservationService;
import com.example.service.ReservationServiceImpl;

import javax.persistence.OptimisticLockException;
import java.time.LocalDateTime;

public class ConcurrentReservationSimulator {

    public static void main(String[] args) {
        ReservationService service = new ReservationServiceImpl();

        Reservation reservation = new Reservation("Salle A",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2));
        reservation = service.creerReservation(reservation);
        System.out.println("Reservation creee : id=" + reservation.getId()
                + ", salle=" + reservation.getSalle() + ", version=" + reservation.getVersion());

        Reservation tx1 = service.trouverParId(reservation.getId());
        Reservation tx2 = service.trouverParId(reservation.getId());
        System.out.println("tx1 et tx2 ont charge la version " + tx1.getVersion());

        tx1.setSalle("Salle B");
        service.modifierReservation(tx1);
        System.out.println("Transaction 1 : modification reussie.");

        try {
            tx2.setSalle("Salle C");
            service.modifierReservation(tx2);
            System.out.println("Transaction 2 : modification reussie.");
        } catch (OptimisticLockException e) {
            System.err.println("Transaction 2 : conflit detecte - " + e.getMessage());
        }

        Reservation finale = service.trouverParId(reservation.getId());
        System.out.println("Etat final : salle=" + finale.getSalle() + ", version=" + finale.getVersion());

        service.fermer();
    }
}
