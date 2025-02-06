package tgbot.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgbot.bot.Bot;
import tgbot.service.CharacterService;
import tgbot.model.Character;

public class MessageHandler {
    private final CharacterService characterService = new CharacterService();

    public void handle(Message message, Bot bot) {
        if (message.hasText()) {
            String text = message.getText();
            if (text.startsWith("/create ")) {
                String name = text.substring(8).trim();
                if (!name.isEmpty() && characterService.getCharacter(name) == null) {
                    Character character = new Character();
                    character.setName(name);
                    characterService.addCharacter(character);
                    bot.executeMessage(new SendMessage(message.getChatId().toString(), "Персонаж " + name + " успешно создан!"));
                } else {
                    bot.executeMessage(new SendMessage(message.getChatId().toString(), "Персонаж с таким именем уже существует или имя некорректно."));
                }
            } else if (text.startsWith("/delete ")) {
                String name = text.substring(8).trim();
                if (!name.isEmpty() && characterService.getCharacter(name) != null) {
                    characterService.removeCharacter(characterService.getCharacter(name));
                    bot.executeMessage(new SendMessage(message.getChatId().toString(), "Персонаж " + name + " успешно удален!"));
                } else {
                    bot.executeMessage(new SendMessage(message.getChatId().toString(), "Персонаж с таким именем не существует или имя некорректно."));
                }
            }
        }
    }
}