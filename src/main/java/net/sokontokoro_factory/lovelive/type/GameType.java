package net.sokontokoro_factory.lovelive.type;

import lombok.Getter;

/**
 * TODO: Masterデータの検討
 * enum型を毎回更新するの面倒です。
 */
public enum GameType {
    HONOCAR,
    SHAKARIN,
    MARUTEN,
    YAMIDORI,
    OIMO
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