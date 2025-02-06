package tgbot.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import tgbot.bot.Bot;
import tgbot.service.CharacterService;
import tgbot.service.DiceService;
import tgbot.service.InventoryService;
import tgbot.service.SkillService;
import tgbot.model.Character;

import java.util.ArrayList;
import java.util.List;

public class CallbackHandler {
    private final DiceService diceService = new DiceService();
    private final CharacterService characterService = new CharacterService();
    private final InventoryService inventoryService = new InventoryService();
    private final SkillService skillService = new SkillService();

    public void handle(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        switch (callbackData) {
            case "ROLL_DICE":
                handleRollDice(callbackQuery, bot);
                break;
            case "VIEW_CHARACTERS":
                handleViewCharacters(callbackQuery, bot);
                break;
            case "CREATOR":
                handleCreator(callbackQuery, bot);
                break;
            default:
                if (callbackData.startsWith("CHARACTER_")) {
                    handleManageCharacter(callbackQuery, bot, callbackData);
                } else if (callbackData.startsWith("STATS_")) {
                    handleShowStats(callbackQuery, bot, callbackData);
                } else if (callbackData.startsWith("CHANGE_STAT_")) {
                    handleChangeStat(callbackQuery, bot, callbackData);
                } else if (callbackData.startsWith("EDIT_STAT_")) {
                    handleEditStat(callbackQuery, bot, callbackData);
                } else if (callbackData.startsWith("ADD_") || callbackData.startsWith("SUBTRACT_")) {
                    handleModifyStat(callbackQuery, bot, callbackData);
                } else if (callbackData.startsWith("CONFIRM_DELETE_")) {
                    handleConfirmDelete(callbackQuery, bot, callbackData);
                }
                break;
        }
    }

    private void handleRollDice(CallbackQuery callbackQuery, Bot bot) {
        int result = diceService.rollDice();
        SendMessage message = new SendMessage();
        message.setChatId(callbackQuery.getMessage().getChatId().toString());
        message.setText("🎲 Выпал кубик: " + result);
        bot.executeMessage(message);
    }

    private void handleViewCharacters(CallbackQuery callbackQuery, Bot bot) {
        List<Character> characters = characterService.getCharacters();
        if (characters.isEmpty()) {
            EditMessageText message = new EditMessageText();
            message.setChatId(callbackQuery.getMessage().getChatId().toString());
            message.setMessageId(callbackQuery.getMessage().getMessageId());
            message.setText("Нет доступных персонажей!");
            bot.executeEditMessage(message);
            return;
        }

        EditMessageText message = new EditMessageText();
        message.setChatId(callbackQuery.getMessage().getChatId().toString());
        message.setMessageId(callbackQuery.getMessage().getMessageId());
        message.setText("Выберите персонажа для управления:");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (Character character : characters) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(InlineKeyboardButton.builder().text(character.getName()).callbackData("CHARACTER_" + character.getName()).build());
            rowsInline.add(rowInline);
        }

        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Назад").callbackData("BACK_TO_MAIN").build()));
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        bot.executeEditMessage(message);
    }

    private void handleManageCharacter(CallbackQuery callbackQuery, Bot bot, String callbackData) {
        String characterName = callbackData.replace("CHARACTER_", "");
        Character character = characterService.getCharacter(characterName);

        EditMessageText message = new EditMessageText();
        message.setChatId(callbackQuery.getMessage().getChatId().toString());
        message.setMessageId(callbackQuery.getMessage().getMessageId());
        message.setText("Управление персонажем " + characterName + ":");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Характеристики").callbackData("STATS_" + characterName).build()));
        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Инвентарь").callbackData("INVENTORY_" + characterName).build()));
        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Назад").callbackData("VIEW_CHARACTERS").build()));
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        bot.executeEditMessage(message);
    }

    private void handleShowStats(CallbackQuery callbackQuery, Bot bot, String callbackData) {
        String characterName = callbackData.replace("STATS_", "");
        Character character = characterService.getCharacter(characterName);

        String statsText = String.format(
                "📜 Профиль:\n" +
                        "Сила: %d\n" +
                        "Ловкость: %d\n" +
                        "Стойкость: %d\n" +
                        "Восприятие: %d\n" +
                        "Выносливость: %d\n" +
                        "Мана: %d\n" +
                        "Магия: %d",
                character.getStrength(),
                character.getAgility(),
                character.getEndurance(),
                character.getPerception(),
                character.getStamina(),
                character.getMana(),
                character.getMagic());

        EditMessageText message = new EditMessageText();
        message.setChatId(callbackQuery.getMessage().getChatId().toString());
        message.setMessageId(callbackQuery.getMessage().getMessageId());
        message.setText(statsText);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Изменить характеристику").callbackData("CHANGE_STAT_" + characterName).build()));
        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Назад").callbackData("CHARACTER_" + characterName).build()));
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        bot.executeEditMessage(message);
    }

    private void handleChangeStat(CallbackQuery callbackQuery, Bot bot, String callbackData) {
        String characterName = callbackData.replace("CHANGE_STAT_", "");
        Character character = characterService.getCharacter(characterName);

        EditMessageText message = new EditMessageText();
        message.setChatId(callbackQuery.getMessage().getChatId().toString());
        message.setMessageId(callbackQuery.getMessage().getMessageId());
        message.setText("Выберите характеристику для изменения:");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Сила").callbackData("EDIT_STAT_" + characterName + "_Сила").build()));
        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Ловкость").callbackData("EDIT_STAT_" + characterName + "_Ловкость").build()));
        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Стойкость").callbackData("EDIT_STAT_" + characterName + "_Стойкость").build()));
        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Восприятие").callbackData("EDIT_STAT_" + characterName + "_Восприятие").build()));
        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Выносливость").callbackData("EDIT_STAT_" + characterName + "_Выносливость").build()));
        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Мана").callbackData("EDIT_STAT_" + characterName + "_Мана").build()));
        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Магия").callbackData("EDIT_STAT_" + characterName + "_Магия").build()));
        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Назад").callbackData("STATS_" + characterName).build()));
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        bot.executeEditMessage(message);
    }

    private void handleEditStat(CallbackQuery callbackQuery, Bot bot, String callbackData) {
        String[] data = callbackData.replace("EDIT_STAT_", "").split("_");
        String characterName = data[0];
        String stat = data[1];

        EditMessageText message = new EditMessageText();
        message.setChatId(callbackQuery.getMessage().getChatId().toString());
        message.setMessageId(callbackQuery.getMessage().getMessageId());
        message.setText("Выберите действие для характеристики " + stat + ":");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Добавить 1").callbackData("ADD_" + characterName + "_" + stat + "_1").build()));
        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Отнять 1").callbackData("SUBTRACT_" + characterName + "_" + stat + "_1").build()));
        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Ввести вручную").callbackData("MANUAL_" + characterName + "_" + stat).build()));
        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Назад").callbackData("CHANGE_STAT_" + characterName).build()));
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        bot.executeEditMessage(message);
    }

    private void handleModifyStat(CallbackQuery callbackQuery, Bot bot, String callbackData) {
        String[] data = callbackData.split("_");
        String action = data[0];
        String characterName = data[1];
        String stat = data[2];
        int value = Integer.parseInt(data[3]);

        Character character = characterService.getCharacter(characterName);
        if (character != null) {
            switch (action) {
                case "ADD":
                    character.modifyStat(stat, value);
                    break;
                case "SUBTRACT":
                    character.modifyStat(stat, -value);
                    break;
            }
            EditMessageText message = new EditMessageText();
            message.setChatId(callbackQuery.getMessage().getChatId().toString());
            message.setMessageId(callbackQuery.getMessage().getMessageId());
            message.setText(stat + " изменена на " + value + "!");
            bot.executeEditMessage(message);
            handleShowStats(callbackQuery, bot, "STATS_" + characterName);
        }
    }

    private void handleConfirmDelete(CallbackQuery callbackQuery, Bot bot, String callbackData) {
        String characterName = callbackData.replace("CONFIRM_DELETE_", "");
        characterService.removeCharacter(characterService.getCharacter(characterName));
        EditMessageText message = new EditMessageText();
        message.setChatId(callbackQuery.getMessage().getChatId().toString());
        message.setMessageId(callbackQuery.getMessage().getMessageId());
        message.setText("Персонаж " + characterName + " успешно удален.");
        bot.executeEditMessage(message);
    }

    private void handleCreator(CallbackQuery callbackQuery, Bot bot) {
        EditMessageText message = new EditMessageText();
        message.setChatId(callbackQuery.getMessage().getChatId().toString());
        message.setMessageId(callbackQuery.getMessage().getMessageId());
        message.setText("Выберите действие:");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Создать персонажа").callbackData("CREATE_PLAYER").build()));
        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Удалить персонажа").callbackData("DELETE_PLAYER").build()));
        rowsInline.add(List.of(InlineKeyboardButton.builder().text("Назад").callbackData("BACK_TO_MAIN").build()));
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        bot.executeEditMessage(message);
    }
}