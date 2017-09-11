package net.sokontokoro_factory.lovelive.type;

import lombok.Getter;

public enum GameType {
    HONOCAR,
    SHAKARIN,
    MARUTEN,
    YAMIDORI
    ;

    public static boolean contains(String checkValue){
        for (GameType gameType : GameType.values()) {
            if (gameType.name().equals(checkValue)) {
                return true;
            }
        }
        return false;
    }
}