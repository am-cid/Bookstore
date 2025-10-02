package com.simple.Bookstore.utils.presentation.Kaomoji;

import java.util.List;
import java.util.Random;

public class SiteMascotFactory {
    private final static List<SiteMascot> siteMascots = List.of(
            new SiteMascot(
                    "ヾ(￣ー￣", "(≧ω≦*)ゝ",
                    238, 235,
                    62
            ),
            new SiteMascot(
                    "\\( ˙▿˙ )/", "\\( ˙▿˙ )/",
                    288, 288,
                    76
            ),
            new SiteMascot(
                    "٩(๑･ิᴗ･ิ)۶", "٩(･ิᴗ･ิ๑)۶",
                    270, 270,
                    72
            ),
            new SiteMascot(
                    "☆ヾ(*´・∀・)ﾉ", "ヾ(・∀・`*)ﾉ☆",
                    364, 354,
                    95
            ),
            new SiteMascot(
                    "ヾ(・ω・`)ノ", "ヾ(´・ω・)ノ゛",
                    288, 304,
                    78
            )

    );
    private final static Random rand = new Random();

    /**
     * @return random site mascot from a list of mascots
     */
    public static SiteMascot randomMascot() {
        return siteMascots.get(rand.nextInt(siteMascots.size()));
    }
}
