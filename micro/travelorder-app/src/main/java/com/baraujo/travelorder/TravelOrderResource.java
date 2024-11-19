package com.baraujo.travelorder;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("travelorder")
public class TravelOrderResource {

    @Inject
    @RestClient
    FlightService flightService;

    @Inject
    @RestClient
    HotelService hotelService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TravelOrderDTO> orders() {
        return TravelOrder.<TravelOrder>listAll().stream()
                .map(
                    travelOrder -> TravelOrderDTO.of(
                        flightService.findByTravelOrderId(travelOrder.id),
                        hotelService.findByTravelOrderId(travelOrder.id)
                    )    
                ).collect(Collectors.toList());
    }

    @GET
    @Path("findById")
    @Produces(MediaType.APPLICATION_JSON)
    public TravelOrder findById(@QueryParam("id") Long id) {
        return TravelOrder.findById(id);
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TravelOrder newTravelOrder(TravelOrderDTO travelOrderDTO) {
        TravelOrder travelOrder = new TravelOrder();
        travelOrder.id = null;
        travelOrder.persist();

        Flight flight = new Flight();
        flight.setTravelOrderId(travelOrder.id);
        flight.setFromAirport(travelOrderDTO.getFromAirport());
        flight.setToAirport(travelOrderDTO.getToAirport());
        flightService.newFlight(flight);

        Hotel hotel = new Hotel();
        hotel.setTravelOrderId(travelOrder.id);
        hotel.setNights(travelOrderDTO.getNights());
        hotelService.newHotel(hotel);

        return travelOrder;
    }
}
