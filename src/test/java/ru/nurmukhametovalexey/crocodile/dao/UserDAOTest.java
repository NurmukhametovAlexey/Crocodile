package ru.nurmukhametovalexey.crocodile.dao;

import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.nurmukhametovalexey.crocodile.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;


//@RunWith(SpringRunner.class)
//@ActiveProfiles("test")
@DataJdbcTest
@Sql({"classpath:user_schema.sql", "classpath:user_test_data.sql"})
class UserDAOTest {


    private JdbcTemplate jdbcTemplate;
    private UserDAO userDAO;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserDAOTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        userDAO = new UserDAO(jdbcTemplate);
        passwordEncoder = new BCryptPasswordEncoder();;
    }

    @Test
    void getAll() {
        List<User> allUsers = userDAO.getAll();
        System.out.println(allUsers);
        User user1 = new User("admin","admin","admin@mail.com","Admin",16,true);
        User user2 = new User("qwe","qwe","qwe@mail.ru","User",15,true);
        assertThat(allUsers.toArray()).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(user1, user2);
    }

    /*@Disabled
    @Test
    void getAllScoresDesc() {
    }

    @Test
    void getUserByLogin() {
    }

    @Test
    void save() {
    }

    @Test
    void update() {
    }*/
}