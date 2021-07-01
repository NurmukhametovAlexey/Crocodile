package ru.nurmukhametovalexey.crocodile.dao;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.nurmukhametovalexey.crocodile.model.GameUser;
import ru.nurmukhametovalexey.crocodile.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;


@DataJdbcTest
@Sql({"classpath:user_schema.sql", "classpath:user_test_data.sql"})
class UserDAOTest {


    private final JdbcTemplate jdbcTemplate;
    private final UserDAO userDAO;

    @Autowired
    public UserDAOTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        userDAO = new UserDAO(jdbcTemplate);
    }

    @Test
    void getAll() {
        List<User> expected = Arrays.asList(
                new User("admin","admin","admin@mail.com","Admin",16,true),
                new User("qwe","qwe","qwe@mail.ru","User",15,true)
        );
        List<User> result = userDAO.getAll();
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getAll_IfNoUsers_ShouldReturnEmptyList() {
        jdbcTemplate.update("delete from \"User\"");
        List<User> result = userDAO.getAll();
        assertThat(result.toArray()).isEmpty();
    }

    @Test
    void getAllOrderByScoresDesc() {
        List<User> result = userDAO.getAll();
        User user1 = new User("admin","admin","admin@mail.com","Admin",16,true);
        User user2 = new User("qwe","qwe","qwe@mail.ru","User",15,true);
        assertThat(result.toArray()).usingRecursiveFieldByFieldElementComparator().containsExactly(user1, user2);
    }

    @Test
    void getAllOrderByScoresDesc_IfNoUsers_ShouldReturnEmptyList() {
        jdbcTemplate.update("delete from \"User\"");
        List<User> result = userDAO.getAll();
        assertThat(result.toArray()).isEmpty();
    }

    @Test
    void getUserByLogin() {
        User result = userDAO.getUserByLogin("admin");
        User expected = new User("admin","admin","admin@mail.com","Admin",16,true);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getUserByLogin_IfNoLogin_ShouldReturnNull() {
        User result = userDAO.getUserByLogin("no such user");
        assertThat(result).isNull();
    }

    @Test
    void save() {
        User userToSave = new User("test","test","test@mail.com","User",0,true);
        Boolean result = userDAO.save(userToSave);
        assertThat(result).isTrue();
        List<User> newUserList = userDAO.getAll();
        assertThat(newUserList.toArray()).usingRecursiveFieldByFieldElementComparator().contains(userToSave);
    }

    @Test
    void save_IfPrimaryKeyViolated_ShouldThrowDataAccessException() {
        User userToSave = new User("admin","test","test@mail.com","User",0,true);
        assertThatThrownBy(() -> userDAO.save(userToSave))
                .isInstanceOf(DataAccessException.class);

    }

    @Test
    void save_IfFieldsAreNull_ShouldThrowDataAccessException() {
        User userToSave = new User(null,"test","test@mail.com","User",0,true);

        assertThatThrownBy(() -> userDAO.save(userToSave))
                .isInstanceOf(DataAccessException.class);

    }

    @Test
    void update() {
        User userToUpdate = new User("admin","new_admin","a@mail.com","User",0,false);
        Boolean result = userDAO.update(userToUpdate);
        assertThat(result).isTrue();
        List<User> updatedUserList = userDAO.getAll();
        assertThat(updatedUserList.toArray()).usingRecursiveFieldByFieldElementComparator().contains(userToUpdate);
    }

    @Test
    void update_IfNoUserToUpdate_ShouldNotAffectRows() {
        User userToUpdate = new User("no such user","new_admin","a@mail.com","User",0,false);
        Boolean result = userDAO.update(userToUpdate);
        assertThat(result).isFalse();
    }

    @Test
    void update_IfFieldsAreNull_ShouldThrowDataAccessException() {
        User userToUpdate = new User("admin",null,"a@mail.com","User",0,false);
        assertThatThrownBy(() -> userDAO.update(userToUpdate))
                .isInstanceOf(DataAccessException.class);
    }
}