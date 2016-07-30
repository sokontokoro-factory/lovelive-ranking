package net.sokontokoro_factory.lovelive.persistence.master;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum MasterGame {
    HONOCAR     (1, "honocar"),
    SHAKARIN    (2, "shakarin");

    @Getter
    private int id;
    @Getter
    private String code;

    MasterGame(int id, String code){
        this.id = id;
        this.code = code;
    }

    public static MasterGame codeOf(final String code) {
        return codeToEnum.get(code);
    }
    private static final Map<String, MasterGame> codeToEnum = new HashMap<String, MasterGame>() {{
        for (MasterGame game : MasterGame.values()) put(game.getCode(), game);
    }};
}