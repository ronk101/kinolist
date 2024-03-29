package ru.nsu.kinolist.bot.util;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.nsu.kinolist.bot.util.CallbackQueryType;

/**
 * Format of CALLBACKDATA for playlists - COMMAND|OPERATION|ACK
 * COMMAND - название плейлиста.
 * OPERATION - операция над плейлистом.
 * ACK - подтверждение операции.
 * ----------------------------------------------------------
 * Format of CALLBACKDATA for random - RANDOM|LIST|GENRE_ID
 * RANDOM - команда вызова рандом меню.
 * LIST - откуда будет осуществлён поиск(все фильмы или из фишлиста).
 * GENRE_ID - айди жанра фильма, доступен только в случае поиска фильма по всему интернету
 *
 * @author skpntsv
 */
public class ParseQueryData {
    public static CallbackQueryType parseQueryType(CallbackQuery callbackQuery) {
        return CallbackQueryType.valueOf(callbackQuery.getData().split("\\|")[0]);
    }
    public static String parseListRandom(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[1];
    }
    public static String parseGenreRandom(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[2];
    }
    public static boolean hasRandomList(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|").length > 1;
    }
    public static boolean hasRandomGenre(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|").length > 2;
    }
    public static String parseOperation(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[1];
    }
    public static String parseYesOrNo(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[2];
    }
    public static String createCallbackData(String... parts) {
        return String.join("|", parts);
    }
    public static boolean hasOperation(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|").length > 1;
    }
    public static boolean hasAck(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|").length > 2;
    }
}
