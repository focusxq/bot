package tgbot.service;

import tgbot.model.Character;
import java.util.ArrayList;
import java.util.List;

public class CharacterService {
    private final List<Character> characters = new ArrayList<>();

    public void addCharacter(Character character) {
        characters.add(character);
    }

    public void removeCharacter(Character character) {
        characters.remove(character);
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public Character getCharacter(String name) {
        return characters.stream()
                .filter(character -> character.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}