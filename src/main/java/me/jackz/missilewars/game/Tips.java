package me.jackz.missilewars.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tips {
    private final static List<String> tips = new ArrayList<>();
    static {
        tips.add("Use §e/teammsg §7to chat with your team");
        tips.add("Break all the tnt from defused missiles to act as a shield for new missiles");
        tips.add("Learn the key weaknesses of certain missiles to easily defuse them");
        tips.add("You can't spawn missiles deep into enemy base");
        tips.add("Missiles spawn 4 blocks from the block you clicked on.");
    }
    public static String getRandomTip() {
        Random rand = new Random();
        return tips.get(rand.nextInt(tips.size()));
    }
}
