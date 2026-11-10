package com.pedromolon.parsahealthbot.bot;

import com.pedromolon.parsahealthbot.service.HabitService;
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
    private final HabitService habitService;

    public TelegramBot(
            @Value("${telegram.bot.username}") String botUsername,
            @Value("${telegram.bot.token}") String botToken,
            UserService userService, HabitService habitService
    ) {
        super(botToken);
        this.botUsername = botUsername;
        this.userService = userService;
        this.habitService = habitService;
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

            menu(messageText, chatId, firstName);
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

    private void menu(String messageText, Long chatId, String firstName) {
        if (messageText.equals("/start")) {
            sendMessage(chatId, "Fala, " + firstName + "! Bot de saúde conectado e pronto para registrar sua rotina. 🚀");

        } else if (messageText.startsWith("/help")) {
            sendMessage(chatId, "Comandos disponíveis:\n" +
                    "/start - Iniciar o bot\n" +
                    "/help - Exibir ajuda\n" +
                    "/createHabit <nomes> - Criar um novo hábito\n" +
                    "/habits - Listas seus hábitos ativos\n" +
                    "/check - Marcar um hábito como realizado");

        } else if (messageText.startsWith("/createHabit")) {
            String habitName = messageText.replaceFirst("/createHabit" , "").trim();
            if (habitName.isEmpty()) {
                sendMessage(chatId, "⚠️ Por favor, informe o nome do hábito. Exemplo: /createHabit Correr");
            } else {
                habitService.createHabit(chatId, firstName, habitName);
                sendMessage(chatId, "✅ Hábito \"" + habitName + "\" criado com sucesso!");
            }

        } else if (messageText.equals("/habits")) {
           var habits = habitService.getActiveHabits(chatId);

           if (habits.isEmpty()) {
               sendMessage(chatId, "⚠️ Você não possui hábitos ativos.");
           } else {
               StringBuilder sb = new StringBuilder("📋 **Seus Hábitos Ativos:**\n\n");
               for (var habit : habits) {
                   sb.append("🔹 ").append(habit.getName()).append("\n");
               }
               sendMessage(chatId, sb.toString());
           }

        } else if (messageText.startsWith("/check")) {
            String habitName = messageText.replaceFirst("/check" , "").trim();

            if (habitName.isEmpty()) {
                sendMessage(chatId, "⚠️ Por favor, informe o nome do hábito. Exemplo: /check Correr");
            } else {
                try {
                    habitService.checkInHabitByName(chatId, habitName);
                    sendMessage(chatId, "🔥 Boa! Hábito \"" + habitName + "\" concluído com sucesso hoje!");
                } catch (IllegalArgumentException e) {
                    sendMessage(chatId, "⚠️ Hábito não encontrado. Verifique o nome e tente novamente.");
                }
            }

        } else {
            sendMessage(chatId, "Comando não reconhecido. Use /help para ver os comandos disponíveis.");
        }
    }

}
