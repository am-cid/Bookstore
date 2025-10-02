package com.simple.Bookstore.utils.presentation.Kaomoji;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Random;

public class ErrorMascotFactory {
    private final static List<ErrorMascot> error401 = List.of(
            new ErrorMascot("┬┴┬┴┤ヾ(･ω├┬┴┬┴", 378),
            new ErrorMascot("┬┴┬┴┤( ͡° ͜ʖ├┬┴┬┴", 359),
            new ErrorMascot("┬┴┬┴┤･ω･)ﾉ", 118),
            new ErrorMascot("┬┴┬┴┤_・)", 204)
    );

    private final static List<ErrorMascot> error404 = List.of(
            new ErrorMascot("(￣_￣)・・・", 200),
            new ErrorMascot("(-_-;)・・・", 172),
            new ErrorMascot("┐(￣ヘ￣;)┌", 225),
            new ErrorMascot("╮(￣ω￣;)╭", 204),
            new ErrorMascot("┐('～`;)┌", 187),
            new ErrorMascot("(¯ . ¯٥)", 138),
            new ErrorMascot("(￣～￣;)", 171),
            new ErrorMascot("(・・;)ゞ", 116),
            new ErrorMascot("(•ิ_•ิ)?", 118)
    );

    private final static List<ErrorMascot> error409 = List.of(
            new ErrorMascot("( ￣ω￣)ノﾞ⌒☆ﾐ(o _ _)o", 441),
            new ErrorMascot("(งಠ_ಠ)ง　σ( •̀ ω •́ σ)", 394),
            new ErrorMascot("(o¬‿¬o )...☆ﾐ(*x_x)", 378),
            new ErrorMascot("(｢• ω •)｢ (⌒ω⌒`)", 340),
            new ErrorMascot("(¬_¬'')ԅ(￣ε￣ԅ)", 320)
    );

    private final static List<ErrorMascot> error500 = List.of(
            new ErrorMascot("_:(´ཀ`」 ∠):_", 234),
            new ErrorMascot("☆⌒(> _ <)", 196),
            new ErrorMascot("☆⌒(>。<)", 186),
            new ErrorMascot("(x_x)⌒☆", 163),
            new ErrorMascot("(×﹏×)", 114)
    );

    private final static Random rand = new Random();

    /**
     * @return random mascot from list of error mascots based on the passed in status code
     */
    public static ErrorMascot random(HttpStatus statusCode) {
        return getRandom(switch (statusCode) {
            case UNAUTHORIZED ->
                    error401;
            case NOT_FOUND ->
                    error404;
            case CONFLICT ->
                    error409;
            default ->
                    error500;
        });
    }

    private static ErrorMascot getRandom(List<ErrorMascot> list) {
        return list.get(rand.nextInt(list.size()));
    }
}
