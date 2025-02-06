package tgbot.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgbot.handlers.CommandHandler;
import tgbot.handlers.CallbackHandler;
import tgbot.handlers.MessageHandler;

public class Bot extends TelegramLongPollingBot {
    private final CommandHandler commandHandler = new CommandHandler();
    private final CallbackHandler callbackHandler = new CallbackHandler();
    private final MessageHandler messageHandler = new MessageHandler();

    @Override
    public String getBotUsername() {
        return BotConfig.BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BotConfig.BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            commandHandler.handle(update.getMessage(), this);
        } else if (update.hasCallbackQuery()) {
            callbackHandler.handle(update.getCallbackQuery(), this);
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            messageHandler.handle(update.getMessage(), this);
        }
    }

    public void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void executeEditMessage(EditMessageText message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}