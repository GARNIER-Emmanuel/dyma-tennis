package com.dyma.tennis.features.players;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.dyma.tennis.features.players.db.PlayerEntity;

public class PlayerEntityList {

        public static PlayerEntity NOVAK_DJOKOVIC = new PlayerEntity(
                        UUID.fromString("1b2c3d4e-f5f6-7890-1234-567890abcdef"), "Novak", "Djokovic",
                        LocalDate.of(1987, Month.MAY, 22), 2, 1);

        public static PlayerEntity RAFAEL_NADAL = new PlayerEntity(
                        UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef"),
                        "Rafael", "Nadal", LocalDate.of(1986, Month.JUNE, 3), 9, 2);

        public static PlayerEntity ROGER_FEDERER = new PlayerEntity(
                        UUID.fromString("2b3c4d5e-f6f7-8901-2345-567890abcdef"),
                        "Roger", "Federer", LocalDate.of(1981, Month.AUGUST, 8),
                        3, 3);

        public static PlayerEntity ANDY_MURRAY = new PlayerEntity(
                        UUID.fromString("3b4c5d6e-f7f8-9012-3456-567890abcdef"),
                        "Andy", "Murray", LocalDate.of(1987, Month.MAY, 15),
                        4, 4);

        public static List<PlayerEntity> ALL = Arrays.asList(ANDY_MURRAY, RAFAEL_NADAL, NOVAK_DJOKOVIC, ROGER_FEDERER);
}
