package tgbot.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import tgbot.bot.Bot;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler {
    public void handle(Message message, Bot bot) {
        String command = message.getText();

        if (command.equals("/start")) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId().toString());
            sendMessage.setText("Выберите опцию");

            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

            List<InlineKeyboardButton> row1 = new ArrayList<>();
            row1.add(InlineKeyboardButton.builder().text("🎲 Кинуть кости").callbackData("ROLL_DICE").build());

            List<InlineKeyboardButton> row2 = new ArrayList<>();
            row2.add(InlineKeyboardButton.builder().text("🧙‍♂️ Персонажи").callbackData("VIEW_CHARACTERS").build());

            List<InlineKeyboardButton> row3 = new ArrayList<>();
            row3.add(InlineKeyboardButton.builder().text("➕ Создать персонажа").callbackData("CREATE_CHARACTER").build());
            row3.add(InlineKeyboardButton.builder().text("❌ Удалить персонажа").callbackData("DELETE_CHARACTER").build());

            rowsInline.add(row1);
            rowsInline.add(row2);
            rowsInline.add(row3);
            markupInline.setKeyboard(rowsInline);
            sendMessage.setReplyMarkup(markupInline);

            bot.executeMessage(sendMessage);
        }
    }
}