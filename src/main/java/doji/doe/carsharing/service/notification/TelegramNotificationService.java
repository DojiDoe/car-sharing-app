package doji.doe.carsharing.service.notification;

import doji.doe.carsharing.config.TelegramConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final TelegramBot bot;
    private final TelegramConfig telegramConfig;

    @Override
    public void sendNotification(long chatId, String message) {
        bot.sendMessage(chatId, message);
    }

    public void notifyAdmin(String message) {
        bot.sendMessage(telegramConfig.getChatId(), message);
    }
}
