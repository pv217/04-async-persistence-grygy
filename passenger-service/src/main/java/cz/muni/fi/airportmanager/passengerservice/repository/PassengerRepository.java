package cz.muni.fi.airportmanager.passengerservice.repository;


import cz.muni.fi.airportmanager.passengerservice.entity.Notification;
import cz.muni.fi.airportmanager.passengerservice.entity.Passenger;
import cz.muni.fi.airportmanager.passengerservice.model.NotificationDto;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
// TODO make this class a Panache repository
public class PassengerRepository implements PanacheRepository<Passenger> {


    /**
     * Find all notifications for a passenger
     *
     * @param passengerId passenger id
     * @return list of notifications
     */
    @WithTransaction
    public Uni<List<Notification>> findNotificationsForPassenger(Long passengerId) {
        // TODO implement this method
        return findById(passengerId).onItem().transform(passenger -> {
            if (passenger == null) {
                return List.of();
            }
            return passenger.getNotifications();
        });
    }


    /**
     * Add notification to a passengers with given flight id
     *
     * @param flightId     flight id
     * @param notification notification to add
     */
    @WithTransaction
    public Uni<Void> addNotificationByFlightId(Long flightId, Notification notification) {
        // TODO implement this method
        // Find passengers by flightId
        return find("flightId", flightId).list()
                .onItem().transformToUni(passengers -> {
                    // Add notification to each passenger
                    for (Passenger passenger : passengers) {
                        passenger.addNotification(notification);
                    }
                    // Persist changes
                    return this.persist(passengers);
                })
                .replaceWithVoid();
    }

    /**
     * Find all passengers for a flight
     *
     * @param flightId flight id
     * @return list of passengers
     */
    public Uni<List<Passenger>> findPassengersForFlight(Long flightId) {
        // TODO implement this method
        return find("flightId", flightId).list();
    }

    /**
     * Get list of notifications with passenger's email
     *
     * @return list of notifications with passenger email
     */
    public Uni<List<NotificationDto>> findNotificationsWithEmail() {
        // TODO implement this method
        return listAll()
                .onItem().transform(passengers -> passengers.stream()
                        .flatMap(passenger -> passenger.getNotifications().stream()
                                .map(notification -> new NotificationDto(notification.id, notification.message, passenger.getEmail()))
                        ).toList()
                );
    }
}
