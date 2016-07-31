package net.sokontokoro_factory.lovelive.persistence.master;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum MasterFavorite {
    HONOKA  (1, "honoka"),
    ERI     (2, "eri"),
    KOTORI  (3, "kotori"),
    UMI     (4, "umi"),
    RIN     (5, "rin"),
    MAKI    (6, "maki"),
    NOZOMI  (7, "nozomi"),
    HANAYO  (8, "hanayo"),
    NICO    (9, "nico"),

    CHIKA       (10, "chika"),
    RIKO        (11, "riko"),
    KANAN       (12, "kanan"),
    DAIYA       (13, "daiya"),
    YOU         (14, "you"),
    YOSHIKO     (15, "yoshiko"),
    HANAMARU    (16, "hanamaru"),
    MARI        (17, "mari"),
    RUBY        (18, "ruby");

    @Getter
    private int id;

    @Getter
    private String code;

    MasterFavorite(int id, String code){
        this.id = id;
        this.code = code;
    }
    public static MasterFavorite codeOf(final String code) {
        return codeToEnum.get(code);
    }

    private static final Map<String, MasterFavorite> codeToEnum = new HashMap<String, MasterFavorite>() {{
        for (MasterFavorite favorite : MasterFavorite.values()) put(favorite.getCode(), favorite);
    }};
}