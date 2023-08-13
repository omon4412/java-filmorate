package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService service;
    @Mock
    private UserStorage userStorage;

    @Mock
    private FriendStorage friendStorage;

    private User testUser;

    @BeforeEach
    void initBeforeTest() {
        testUser = new User("admin@email.test", "admin",
                LocalDate.now().minusMonths(10));
        testUser.setBirthday(LocalDate.ofYearDay(2000, 256));
    }

    @Test
    void addUser_whenUserIsNotExist_thenAddedUser() {
        User expectedUser = new User(testUser);
        expectedUser.setId(1);

        when(userStorage.add(testUser)).thenReturn(expectedUser);
        when(userStorage.getUserByEmail(testUser.getEmail())).thenReturn(null);
        when(userStorage.getUserByLogin(testUser.getLogin())).thenReturn(null);

        User addedUser = service.addUser(testUser);
        expectedUser.setName(addedUser.getName());

        assertEquals(expectedUser, addedUser);
        verify(userStorage).add(testUser);
    }

    @Test
    void addUser_whenUserIsExistByEmail_thenUserAlreadyExistExceptionThrown() {
        User expectedUser = new User(testUser);
        expectedUser.setId(1);
        when(userStorage.getUserByEmail(expectedUser.getEmail())).thenReturn(expectedUser);

        assertThrows(UserAlreadyExistException.class, () -> service.addUser(testUser));
    }

    @Test
    void addUser_whenUserIsExistByLogin_thenUserAlreadyExistExceptionThrown() {
        User expectedUser = new User(testUser);
        expectedUser.setId(1);
        when(userStorage.getUserByLogin(expectedUser.getLogin())).thenReturn(expectedUser);

        assertThrows(UserAlreadyExistException.class, () -> service.addUser(testUser));
    }

    @Test
    void updateUser_whenIdIsExists_thenUpdatedUser() {
        User expectedUser = new User(testUser);
        expectedUser.setId(1);
        expectedUser.setName("New name");

        when(userStorage.add(testUser)).thenReturn(expectedUser);
        service.addUser(testUser);

        testUser.setId(1);
        testUser.setName("New name");

        when(userStorage.update(testUser)).thenReturn(expectedUser);
        when(userStorage.checkForExists(testUser.getId())).thenReturn(true);

        User updatedUser = service.updateUser(testUser);

        assertEquals(expectedUser, updatedUser);
        verify(userStorage).update(testUser);
        verify(userStorage).checkForExists(testUser.getId());
    }

    @Test
    void updateUser_whenIdIsNotExists_thenUserNotFoundExceptionThrown() {
        testUser.setId(1);
        //testUser.setName("New name");
        when(userStorage.checkForExists(testUser.getId())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> service.updateUser(testUser));

        verify(userStorage).checkForExists(testUser.getId());
    }

    @Test
    void deleteUser_whenExists_thenDeleteUser_andUserNotFoundExceptionThrownAfterCallGetUserById() {
        testUser.setId(1);
        when(userStorage.add(testUser)).thenReturn(testUser);
        User addedUser = service.addUser(testUser);
        when(userStorage.checkForExists(addedUser.getId())).thenReturn(true);
        when(userStorage.delete(addedUser.getId())).thenReturn(testUser);

        service.deleteUser(addedUser.getId());

        when(userStorage.checkForExists(addedUser.getId())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> service.getUserById(addedUser.getId()));
    }

    @Test
    void getUserById_whenUserIsExists_thenReturnUser() {
        User expectedUser = new User(testUser);
        expectedUser.setId(1);
        expectedUser.setName("New name");

        when(userStorage.add(testUser)).thenReturn(expectedUser);
        User addedUser = service.addUser(testUser);
        when(userStorage.checkForExists(addedUser.getId())).thenReturn(true);
        when(userStorage.getUserById(addedUser.getId())).thenReturn(expectedUser);

        User getUser = service.getUserById(addedUser.getId());

        assertEquals(expectedUser, getUser);
    }

    @Test
    void shouldGetAllUserList() {
        List<User> expectedList = List.of(testUser, testUser);
        when(userStorage.getAllObjList()).thenReturn(expectedList);

        Collection<User> userList = service.getAllUserList();

        assertEquals(expectedList, userList);
    }
}