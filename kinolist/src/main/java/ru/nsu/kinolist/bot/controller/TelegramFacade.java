package ru.nsu.kinolist.bot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nsu.kinolist.bot.cache.UserDataCache;
import ru.nsu.kinolist.bot.handlers.CallbackQueryHandlerFactory;
import ru.nsu.kinolist.bot.util.MessagesService;
import ru.nsu.kinolist.bot.util.BotState;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static ru.nsu.kinolist.bot.util.Constants.*;

@Service
@Slf4j
public class TelegramFacade {
    private final UserDataCache userDataCache;
    private final BotStateContext botStateContext;
    private final CallbackQueryHandlerFactory callbackQueryHandlerFactory;

    public TelegramFacade(UserDataCache userDataCache, BotStateContext botStateContext, CallbackQueryHandlerFactory callBackQueryHandlerFactory) {
        this.userDataCache = userDataCache;
        this.botStateContext = botStateContext;
        this.callbackQueryHandlerFactory = callBackQueryHandlerFactory;
    }

    public List<PartialBotApiMethod<? extends Serializable>> handleUpdate(Update update) {
        List<PartialBotApiMethod<? extends Serializable>> replies = null;

        if (update.hasCallbackQuery()) {
            log.info("New callbackQuery from User[@{}], chatId[{}], with data[{}]",
                    update.getCallbackQuery().getFrom().getUserName(),
                    update.getCallbackQuery().getMessage().getChatId(),
                    update.getCallbackQuery().getData());

            return callbackQueryHandlerFactory.processCallbackQuery(update.getCallbackQuery());
        }

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User[@{}], chatId[{}], with text[{}]",
                    message.getFrom().getUserName(),
                    message.getChatId(),
                    message.getText());
            replies = handleInputMessage(message);
        }

        return replies;
    }

    private List<PartialBotApiMethod<? extends Serializable>> handleInputMessage(Message message) {
        String data = message.getText();
        Long chatId = message.getChatId();
        BotState botState;

        botState = switch (data) {
            case START_COMMAND -> BotState.START;
            case HELP_COMMAND -> BotState.SHOW_HELP;
            case MENU_COMMAND, MAIN_MENU_COMMAND_TEXT -> BotState.SHOW_MAIN_MENU;
            default -> userDataCache.getUsersCurrentBotState(chatId);
        };

        userDataCache.setUsersCurrentBotState(chatId, botState);
        if (botState == BotState.IDLE) {
            log.info("chatId[{}] write message[{}] from state[{}]", chatId, data, botState);
            return Collections.singletonList(MessagesService.createMessageTemplate(chatId, UNKNOWN_MESSAGE));
        }
        return botStateContext.processInputMessage(botState, message);
    }
}
