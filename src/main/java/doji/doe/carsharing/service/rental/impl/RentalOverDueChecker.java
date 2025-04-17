package doji.doe.carsharing.service.rental.impl;

import doji.doe.carsharing.dto.notification.NotificationType;
import doji.doe.carsharing.model.Rental;
import doji.doe.carsharing.repository.rental.RentalRepository;
import doji.doe.carsharing.service.notification.NotificationTemplates;
import doji.doe.carsharing.service.notification.TelegramNotificationService;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalOverDueChecker {
    private final RentalRepository rentalRepository;
    private final TelegramNotificationService notificationService;

    @Scheduled(cron = "0 0 8 * * *")
    private void checkOverDueRental() {
        LocalDate currentDate = LocalDate.now();
        List<Rental> overDueRentals = rentalRepository.findOverDueRentals(currentDate);
        if (!overDueRentals.isEmpty()) {
            overDueRentals.forEach(rental -> {
                String message = NotificationTemplates.getTemplate(NotificationType.OVERDUE_RENTAL,
                        rental.getId(),
                        rental.getCar().getBrand(),
                        ChronoUnit.DAYS.between(rental.getRentalDate(), rental.getReturnDate()));
                notificationService.notifyAdmin(message);
            });
        } else {
            notificationService.notifyAdmin("No rentals overdue today!");
        }
    }
}
