package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.BaseEntity;
import ru.yandex.practicum.filmorate.service.BaseEntityService;
import ru.yandex.practicum.filmorate.util.IdConverter;

import javax.validation.Valid;
import java.util.Collection;

/**
 * Абстрактный контроллер для управления сущностями {@link BaseEntity}.
 *
 * @param <T> Тип сущности, наследующей {@link BaseEntity}
 */
public abstract class BaseEntityController<T extends BaseEntity> {
    /**
     * Сервис для работы с сущностями.
     */
    protected final BaseEntityService<T> service;

    public BaseEntityController(BaseEntityService<T> service) {
        this.service = service;
    }

    /**
     * Удалить сущность по идентификатору.
     *
     * @param id Идентификатор сущности
     * @return Удаленная сущность
     */
    @DeleteMapping("/{id}")
    public T deleteEntity(@PathVariable String id) {
        int idInt = IdConverter.getIdInt(id);
        return service.deleteEntity(idInt);
    }

    /**
     * Получить сущность по идентификатору.
     *
     * @param id Идентификатор сущности
     * @return Найденная сущность
     */
    @GetMapping("/{id}")
    public T getEntityById(@PathVariable String id) {
        int idInt = IdConverter.getIdInt(id);
        return service.getEntityById(idInt);
    }

    /**
     * Получить список всех сущностей.
     *
     * @return Список всех сущностей
     */
    @GetMapping
    public Collection<T> getAllEntities() {
        return service.getAllEntities();
    }

    /**
     * Создать новую сущность.
     *
     * @param entity Новая сущность
     * @return Созданная сущность
     */
    @PostMapping
    public T createEntity(@Valid @RequestBody T entity) {
        return service.addEntity(entity);
    }

    /**
     * Обновлить сущность.
     *
     * @param entity Сущность для обновления
     * @return Обновленная сущность
     */
    @PutMapping
    public T updateEntity(@Valid @RequestBody T entity) {
        return service.updateEntity(entity);
    }

    /**
     * Удалить все сущности.
     *
     * @return Количество удаленных сущностей
     */
    @DeleteMapping()
    public int deleteEntities() {
        return service.clearEntities();
    }
}
