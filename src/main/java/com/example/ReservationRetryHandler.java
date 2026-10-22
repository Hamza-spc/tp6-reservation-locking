package com.example;

import com.example.model.Reservation;
import com.example.service.ReservationService;

import javax.persistence.OptimisticLockException;
import java.time.LocalDateTime;

public class ReservationRetryHandler {

    private static final int MAX_RETRIES = 3;
    private ReservationService service;

    public ReservationRetryHandler(ReservationService service) {
        this.service = service;
    }

    public Reservation modifierAvecRetry(Reservation reservation) {
        Long id = reservation.getId();
        String salle = reservation.getSalle();
        LocalDateTime dateDebut = reservation.getDateDebut();
        LocalDateTime dateFin = reservation.getDateFin();

        int tentative = 0;
        Reservation courante = reservation;
        while (tentative < MAX_RETRIES) {
            try {
                return service.modifierReservation(courante);
            } catch (OptimisticLockException e) {
                tentative++;
                System.out.println("Conflit detecte, tentative de retry " + tentative);
                courante = service.trouverParId(id);
                courante.setSalle(salle);
                courante.setDateDebut(dateDebut);
                courante.setDateFin(dateFin);
            }
        }
        throw new OptimisticLockException("Echec apres " + MAX_RETRIES + " tentatives");
    }
}
