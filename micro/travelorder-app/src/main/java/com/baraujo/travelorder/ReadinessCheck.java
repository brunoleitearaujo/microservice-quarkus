package com.baraujo.travelorder;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.inject.Inject;

@Readiness
public class ReadinessCheck implements HealthCheck {

    @RestClient
    @Inject
    FlightService flightService;

    @RestClient
    @Inject
    HotelService hotelService;

    @Override
    public HealthCheckResponse call() {
        if (flightService.findById(1L) != null && hotelService.findById(1L) != null) {
            return HealthCheckResponse.up("estou pronto");
        } else {
            return HealthCheckResponse.down("não estou pronto");
        }
    }
}
