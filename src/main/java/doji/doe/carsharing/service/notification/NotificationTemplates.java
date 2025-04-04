package doji.doe.carsharing.service.notification;

import doji.doe.carsharing.dto.notification.NotificationType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationTemplates {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String getTemplate(NotificationType type, Object... params) {
        return switch (type) {
            case NEW_RENTAL -> formatNewRental(
                    (Long) params[0],
                    (Long) params[1],
                    (Long) params[2],
                    (LocalDateTime) params[3],
                    (LocalDateTime) params[4]
            );
            case PAYMENT_SUCCESS -> formatPaymentSuccess(
                    (Long) params[0],
                    (BigDecimal) params[1],
                    (Long) params[2]
            );
            case PAYMENT_FAILED -> formatPaymentFailed(
                    (Long) params[0],
                    (Long) params[1]
            );
            case OVERDUE_RENTAL -> String.format("Overdue rental created: %s", params);
        };
    }

    private static String formatNewRental(Long rentalId, Long carId, Long userId,
                                          LocalDateTime rentalDate, LocalDateTime returnDate) {
        return """
                🚗 *New Rental Created* 🚗
                ID: %d
                CarId: %d
                UserId: %d
                Rental Date: %s
                Return Date: %s
                """.formatted(rentalId, carId, userId,
                rentalDate.format(DATE_FORMATTER),
                returnDate.format(DATE_FORMATTER));
    }

    private static String formatPaymentSuccess(Long paymentId, BigDecimal amount, Long rentalId) {
        return """
                💰 *Payment Successful* 💰
                Payment ID: %d
                Amount: %s
                For Rental: %d
                """.formatted(paymentId, amount, rentalId);
    }

    private static String formatOverdueRental(Long rentalId, String carInfo, String overduePeriod) {
        return """
                ⚠️ *Overdue Rental* ⚠️
                ID: %d
                Car: %s
                Overdue: %s
                """.formatted(rentalId, carInfo, overduePeriod);
    }

    private static String formatPaymentFailed(Long paymentId, Long rentalId) {
        return """
                ❌ *Payment Failed* ❌
                Payment ID: %d
                Rental ID: %d
                """.formatted(paymentId, rentalId);
    }
}
