package com.pedromolon.parsahealthbot.bot;

import com.pedromolon.parsahealthbot.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final UserService userService;

    public TelegramBot(
            @Value("${telegram.bot.username}") String botUsername,
            @Value("${telegram.bot.token}") String botToken,
            UserService userService
    ) {
        super(botToken);
        this.botUsername = botUsername;
        this.userService = userService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            String firstName = update.getMessage().getChat().getFirstName();

            userService.getOrCreateUser(chatId, firstName);

            if (messageText.equals("/start")) {
                sendMessage(chatId, "Fala, " + firstName + "! Bot de saúde conectado e pronto para registrar sua rotina. 🚀");
            } else if (messageText.startsWith("/help")) {
                sendMessage(chatId, "Comandos disponíveis:\n/start - Iniciar o bot\n/help - Exibir ajuda");
            } else {
                sendMessage(chatId, "Comando não reconhecido. Use /start para começar.");
            }
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
