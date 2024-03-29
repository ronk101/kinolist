package ru.nsu.kinolist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import ru.nsu.kinolist.database.DAO.FilmDAO;
import ru.nsu.kinolist.database.DAO.PersonDAO;
import ru.nsu.kinolist.database.entities.Film;
import ru.nsu.kinolist.service.FilmModificationHandler;
import ru.nsu.kinolist.utils.ListType;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class WishListController extends ListController {

    @Autowired
    public WishListController(FilmDAO filmDAO, FilmModificationHandler filmModificationHandler, PersonDAO personDAO) {
        super(filmModificationHandler, filmDAO, personDAO);
      //  this.filmModificationHandler = filmModificationHandler;

    }

    public Optional<Film> getRandomFilmByUser(String chatId) {
        List<Film> films = personDAO.getAllFilmsByChatIdFromList(chatId, ListType.WISH);
        Random random = new Random();
        if (films.size() == 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(films.get(random.nextInt(films.size())));
    }

    public int moveToViewedListByUser(String chatId, Film film) {
        try {
            filmDAO.deleteByChatIdFromList(chatId, film, ListType.WISH);
            filmDAO.saveByChatIdToList(chatId, film, ListType.VIEWED);
            return 1;
        }
        catch (DataIntegrityViolationException e) {
            return 0;
        }
    }
}
