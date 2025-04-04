package doji.doe.carsharing.service.notification;

import doji.doe.carsharing.config.TelegramConfig;
import doji.doe.carsharing.exception.TelegramNotificationException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramConfig telegramConfig;

    public TelegramBot(TelegramConfig telegramConfig) {
        super(telegramConfig.getBotToken());
        this.telegramConfig = telegramConfig;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Received message: " + update.getMessage().getText()
                + " from chat ID: " + update.getMessage().getChatId());
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            startCommendReceived(chatId);
        }
    }

    private void startCommendReceived(long chatId) {
        String response = """
            Hi! I'm your notification bot
            I'll provide notifications about:
            - New rentals
            - Overdue rentals
            - Successful payments""";
        sendMessage(chatId, response);
    }

    public void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new TelegramNotificationException(
                    "Failed to send message to chat " + chatId + ": " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return telegramConfig.getBotName();
    }

}
