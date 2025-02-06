package tgbot.service;

import java.util.Random;

public class DiceService {
    private final Random random = new Random();

    public int rollDice() {
        return random.nextInt(6) + 1;
    }
}