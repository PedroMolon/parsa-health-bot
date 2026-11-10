package com.pedromolon.parsahealthbot.bot;

import com.pedromolon.parsahealthbot.model.UserAccount;
import com.pedromolon.parsahealthbot.model.enums.ActivityLevel;
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

            userService.createUser(chatId, firstName);

            routeCommand(messageText, chatId, firstName);
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

    private void routeCommand(String messageText, Long chatId, String firstName) {
        if (messageText.equals("/start")) {
            handleStart(chatId, firstName);
        } else if (messageText.startsWith("/help")) {
            handleHelp(chatId);
        } else if (messageText.startsWith("/createHabit")) {
            handleCreateHabit(messageText, chatId, firstName);
        } else if (messageText.equals("/habits")) {
            handleListHabits(chatId);
        } else if (messageText.startsWith("/check")) {
            handleCheckHabit(messageText, chatId);
        } else if (messageText.startsWith("/setprofile")) {
            handleSetProfile(messageText, chatId);
        } else if (messageText.equals("/profile")) {
            handleGetProfile(chatId);
        } else {
            sendMessage(chatId, "Comando não reconhecido. Use /help para ver os comandos disponíveis.");
        }
    }

    private void handleStart(Long chatId, String firstName) {
        sendMessage(chatId, "Fala, " + firstName + "! Bot de saúde conectado e pronto para registrar sua rotina. 🚀\n\nUse /setprofile para configurar seus dados e /help para ver os comandos.");
    }

    private void handleHelp(Long chatId) {
        sendMessage(chatId, "🤖 **Comandos Disponíveis do Parsa Health Bot**\n\n" +
                "👤 **Perfil e Saúde:**\n" +
                "• `/setprofile <peso> <altura_cm> <idade> <nivel>` - Configura seus dados\n" +
                "• `/profile` - Exibe seus dados atuais e meta de água\n\n" +
                "📋 **Gestão de Hábitos:**\n" +
                "• `/createHabit <nome>` - Criar um novo hábito\n" +
                "• `/habits` - Listar seus hábitos ativos\n" +
                "• `/check <nome>` - Marcar hábito como realizado hoje\n\n" +
                "⚙️ **Sistema:**\n" +
                "• `/start` - Iniciar o bot\n" +
                "• `/help` - Exibir esta ajuda");
    }

    private void handleCreateHabit(String messageText, Long chatId, String firstName) {
        String habitName = messageText.replaceFirst("/createHabit", "").trim();
        if (habitName.isEmpty()) {
            sendMessage(chatId, "⚠️ Por favor, informe o nome do hábito. Exemplo: `/createHabit Correr`");
        } else {
            habitService.createHabit(chatId, firstName, habitName);
            sendMessage(chatId, "✅ Hábito \"" + habitName + "\" criado com sucesso!");
        }
    }

    private void handleListHabits(Long chatId) {
        var habits = habitService.getActiveHabits(chatId);

        if (habits.isEmpty()) {
            sendMessage(chatId, "⚠️ Você não possui hábitos ativos. Use `/createHabit <nome>` para criar um.");
        } else {
            StringBuilder sb = new StringBuilder("📋 **Seus Hábitos Ativos:**\n\n");
            for (var habit : habits) {
                sb.append("🔹 ").append(habit.getName()).append("\n");
            }
            sendMessage(chatId, sb.toString());
        }
    }

    private void handleCheckHabit(String messageText, Long chatId) {
        String habitName = messageText.replaceFirst("/check", "").trim();

        if (habitName.isEmpty()) {
            sendMessage(chatId, "⚠️ Por favor, informe o nome do hábito. Exemplo: `/check Correr`");
        } else {
            try {
                habitService.checkInHabitByName(chatId, habitName);
                sendMessage(chatId, "🔥 Boa! Hábito \"" + habitName + "\" concluído com sucesso hoje!");
            } catch (IllegalArgumentException exception) {
                sendMessage(chatId, "⚠️ Hábito não encontrado ou inativo. Verifique se o nome está correto e tente novamente.");
            }
        }
    }

    private void handleSetProfile(String messageText, Long chatId) {
        String[] parts = messageText.replaceFirst("/setprofile", "").trim().split("\\s+");

        if (parts.length < 4) {
            sendMessage(chatId, "⚠️ Formato incorreto!\nUse: `/setprofile [peso] [altura_cm] [idade] [nivel]`\n\n" +
                    "Níveis permitidos: `SEDENTARY`, `MODERATE`, `ACTIVE`, `VERY_ACTIVE`\n" +
                    "Exemplo: `/setprofile 75.0 178 24 MODERATE`");
        } else {
            try {
                Double weight = Double.parseDouble(parts[0]);
                Integer height = Integer.parseInt(parts[1]);
                Integer age = Integer.parseInt(parts[2]);

                ActivityLevel activityLevel;
                try {
                    activityLevel = ActivityLevel.valueOf(parts[3].toUpperCase());
                } catch (IllegalArgumentException e) {
                    sendMessage(chatId, "❌ Nível de atividade inválido. Escolha entre: `SEDENTARY`, `MODERATE`, `ACTIVE`, `VERY_ACTIVE`");
                    return;
                }

                UserAccount updatedUser = userService.updateUserProfile(chatId, weight, height, age, activityLevel);

                sendMessage(chatId, "✅ Perfil atualizado com sucesso!\n\n" +
                        "⚖️ Peso: " + weight + " kg\n" +
                        "📏 Altura: " + height + " cm\n" +
                        "👤 Idade: " + age + " anos\n" +
                        "⚡ Nível: " + activityLevel + "\n" +
                        "💧 Meta de Água Calculada: " + updatedUser.getDailyWaterGoalMl() + " ml/dia 🚀");

            } catch (NumberFormatException e) {
                sendMessage(chatId, "❌ Erro nos números informados. Verifique se peso, altura e idade são valores válidos.");
            }
        }
    }

    private void handleGetProfile(Long chatId) {
        UserAccount user = userService.getUser(chatId);

        if (user == null || user.getWeight() == null) {
            sendMessage(chatId, "⚠️ Você ainda não configurou seu perfil de saúde. Use `/setprofile` para começar.");
        } else {
            sendMessage(chatId, "📋 **Seu Perfil de Saúde:**\n\n" +
                    "⚖️ Peso: " + user.getWeight() + " kg\n" +
                    "📏 Altura: " + user.getHeight() + " cm\n" +
                    "👤 Idade: " + user.getAge() + " anos\n" +
                    "⚡ Nível: " + user.getActivityLevel() + "\n" +
                    "💧 Meta de Água: " + user.getDailyWaterGoalMl() + " ml/dia");
        }
    }

}
