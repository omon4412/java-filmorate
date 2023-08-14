package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaRatingNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingStorage;

import java.util.Collection;

/**
 * Сервис для работы с рейтингами
 */
@Service
@Slf4j
public class MpaRatingService implements BaseEntityService<MpaRating> {
    private final MpaRatingStorage mpaRatingStorage;

    @Autowired
    public MpaRatingService(MpaRatingStorage mpaRatingStorage) {
        this.mpaRatingStorage = mpaRatingStorage;
    }

    @Override
    public int clearEntities() {
        log.debug("Начато удаление всех рейтингов");
        return mpaRatingStorage.clearAll();
    }

    @Override
    public MpaRating deleteEntity(int mpaRatingId) {
        checkUserForExists(mpaRatingId);
        log.debug("Начато удаление рейтинга id=" + mpaRatingId);
        return mpaRatingStorage.delete(mpaRatingId);
    }

    @Override
    public MpaRating getEntityById(int mpaRatingId) {
        checkUserForExists(mpaRatingId);
        return mpaRatingStorage.getMpaRatingById(mpaRatingId);
    }

    @Override
    public Collection<MpaRating> getAllEntities() {
        return mpaRatingStorage.getAllObjList();
    }

    @Override
    public MpaRating addEntity(MpaRating mpaRatingId) {
        log.debug("Начато добаление рейтинга - " + mpaRatingId);
        return mpaRatingStorage.add(mpaRatingId);
    }

    @Override
    public MpaRating updateEntity(MpaRating mpaRatingId) {
        checkUserForExists(mpaRatingId.getId());
        log.debug("Начато обновдение рейтинга - " + mpaRatingId);
        return mpaRatingStorage.update(mpaRatingId);
    }

    private void checkUserForExists(int mpaRatingId) {
        if (!mpaRatingStorage.checkForExists(mpaRatingId)) {
            log.error("рейтинг с id={} не существует", mpaRatingId);
            throw new MpaRatingNotFoundException(
                    "рейтинг с id=" + mpaRatingId + " не существует");
        }
    }
}
