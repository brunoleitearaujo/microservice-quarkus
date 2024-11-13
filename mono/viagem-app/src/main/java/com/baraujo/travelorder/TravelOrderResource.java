package com.baraujo.travelorder;

import java.util.List;
import java.util.stream.Collectors;

import com.baraujo.flight.Flight;
import com.baraujo.flight.FlightResource;
import com.baraujo.hotel.Hotel;
import com.baraujo.hotel.HotelResource;

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
    FlightResource flightResource;

    @Inject
    HotelResource hotelResource;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TravelOrderDTO> orders() {
        return TravelOrder.<TravelOrder>listAll().stream()
                .map(
                    travelOrder -> TravelOrderDTO.of(
                        flightResource.findByTravelOrderId(travelOrder.id),
                        hotelResource.findByTravelOrderId(travelOrder.id)
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
        flight.travelOrderId = travelOrder.id;
        flight.fromAirport = travelOrderDTO.getFromAirport();
        flight.toAirport = travelOrderDTO.getToAirport();
        flightResource.newFlight(flight);

        Hotel hotel = new Hotel();
        hotel.travelOrderId = travelOrder.id;
        hotel.nights = travelOrderDTO.getNights();
        hotelResource.newHotel(hotel);

        return travelOrder;
    }
}
