package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.model.ReservationView;
import com.example.demo.service.BackOfficeReservationApiService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationApiController {

    private final BackOfficeReservationApiService reservationApiService;

    public ReservationApiController(BackOfficeReservationApiService reservationApiService) {
        this.reservationApiService = reservationApiService;
    }

    @GetMapping
    public List<ReservationView> listReservations(
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin) {
        try {
            return reservationApiService.listReservations(dateDebut, dateFin);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, e.getMessage(), e);
        }
    }

    @GetMapping("/raw")
    public String rawReservations(
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin) {
        try {
            return reservationApiService.fetchReservationsRaw(dateDebut, dateFin);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, e.getMessage(), e);
        }
    }
}