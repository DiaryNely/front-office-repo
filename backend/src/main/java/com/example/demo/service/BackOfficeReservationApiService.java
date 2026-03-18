package com.example.demo.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.ReservationView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BackOfficeReservationApiService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public BackOfficeReservationApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<ReservationView> listReservations(String dateDebut, String dateFin) {
        String body = fetchReservationsRaw(dateDebut, dateFin);
        try {
            return parseReservations(body);
        } catch (IOException e) {
            throw new IllegalStateException("Réponse JSON back-office invalide", e);
        }
    }

    public String fetchReservationsRaw(String dateDebut, String dateFin) {
        String baseUrl = getBackOfficeBaseUrl();
        String endpoint = buildEndpoint(baseUrl, dateDebut, dateFin);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Réponse back-office invalide: HTTP " + response.statusCode());
            }

            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Impossible de contacter l'API back-office", e);
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de contacter l'API back-office", e);
        }
    }

    private List<ReservationView> parseReservations(String body) throws IOException {
        JsonNode root = objectMapper.readTree(body);

        JsonNode dataNode = root;
        if (root.has("data")) {
            dataNode = root.get("data");
        } else if (root.has("result")) {
            dataNode = root.get("result");
        }

        if (dataNode == null || dataNode.isNull()) {
            return Collections.emptyList();
        }

        if (!dataNode.isArray()) {
            return Collections.emptyList();
        }

        List<ReservationView> reservations = new ArrayList<>();
        for (JsonNode itemNode : dataNode) {
            ReservationView item = new ReservationView();
            item.setId(itemNode.path("id").isInt() ? itemNode.get("id").asInt() : null);
            item.setClientId(itemNode.path("clientId").asText(null));
            item.setNombrePassager(
                    itemNode.path("nombrePassager").isInt() ? itemNode.get("nombrePassager").asInt() : null);
            item.setDateHeureArrivee(itemNode.path("dateHeureArrivee").asText(null));
            item.setIdHotel(itemNode.path("idHotel").isInt() ? itemNode.get("idHotel").asInt() : null);
            reservations.add(item);
        }

        return reservations;
    }

    private String buildEndpoint(String baseUrl, String dateDebut, String dateFin) {
        StringBuilder endpoint = new StringBuilder(baseUrl);
        if (!baseUrl.endsWith("/")) {
            endpoint.append('/');
        }
        endpoint.append("api/reservations");

        boolean hasQuery = false;

        if (dateDebut != null && !dateDebut.isBlank()) {
            endpoint.append(hasQuery ? '&' : '?');
            endpoint.append("dateDebut=").append(URLEncoder.encode(dateDebut, StandardCharsets.UTF_8));
            hasQuery = true;
        }

        if (dateFin != null && !dateFin.isBlank()) {
            endpoint.append(hasQuery ? '&' : '?');
            endpoint.append("dateFin=").append(URLEncoder.encode(dateFin, StandardCharsets.UTF_8));
        }

        return endpoint.toString();
    }

    private String getBackOfficeBaseUrl() {
        return "http://localhost:8080/back-office";
    }
}
