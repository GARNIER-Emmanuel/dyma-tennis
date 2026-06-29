package com.dyma.tennis.features.players;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PlayerList {

    public static Player RAFAEL_NADAL = new Player(
            new PlayerDescription(
                    UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef"),
                    "Rafael",
                    "Nadal",
                    LocalDate.of(1986, Month.JUNE, 3),
                    new Rank(1, 5000)
            ),
            Collections.emptySet()
    );

    public static Player NOVAK_DJOKOVIC = new Player(
            new PlayerDescription(
                    UUID.fromString("1b2c3d4e-f5f6-7890-1234-567890abcdef"),
                    "Novak",
                    "Djokovic",
                    LocalDate.of(1987, Month.MAY, 22),
                    new Rank(2, 4000)
            ),
            Collections.emptySet()
    );

    public static Player ROGER_FEDERER = new Player(
            new PlayerDescription(
                    UUID.fromString("2b3c4d5e-f6f7-8901-2345-567890abcdef"),
                    "Roger",
                    "Federer",
                    LocalDate.of(1981, Month.AUGUST, 8),
                    new Rank(3, 3000)
            ),
            Collections.emptySet()
    );

    public static Player ANDY_MURRAY = new Player(
            new PlayerDescription(
                    UUID.fromString("3b4c5d6e-f7f8-9012-3456-567890abcdef"),
                    "Andy",
                    "Murray",
                    LocalDate.of(1987, Month.MAY, 15),
                    new Rank(4, 2000)
            ),
            Collections.emptySet()
    );

    public static List<Player> ALL = Arrays.asList(RAFAEL_NADAL, NOVAK_DJOKOVIC, ROGER_FEDERER, ANDY_MURRAY);
}
