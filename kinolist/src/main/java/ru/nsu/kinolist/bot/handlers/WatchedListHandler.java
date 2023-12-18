package ru.nsu.kinolist.bot.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.nsu.kinolist.bot.cache.UserDataCache;
import ru.nsu.kinolist.bot.handlers.callbackquery.CallbackQueryType;
import ru.nsu.kinolist.bot.service.PlayListService;
import ru.nsu.kinolist.bot.util.BotState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class WatchedListHandler extends PlayListHandler implements InputMessageHandler {
    @Autowired
    public WatchedListHandler(PlayListService playListService, UserDataCache userDataCache) {
        super(playListService, userDataCache);
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handleMessage(Message message) {
        BotState currentBotState = userDataCache.getUsersCurrentBotState(message.getChatId());
        userDataCache.removeUsersCache(message.getChatId());    // очистка памяти
        Long chatId = message.getChatId();
        List<PartialBotApiMethod<? extends Serializable>> messages = new ArrayList<>();

        switch (currentBotState) {
            case WATCHEDLIST_ADD -> processAddOperation(message.getText(), chatId, messages, CallbackQueryType.WATCHEDLIST);
            case WATCHEDLIST_REMOVE -> messages.add(processRemoveOperation(message.getText(), chatId, CallbackQueryType.WATCHEDLIST));
            default -> messages.add(handleDefaultCase(currentBotState, chatId));
        }
        return messages;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.WATCHEDLIST;
    }

}
