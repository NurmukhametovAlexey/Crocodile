package ru.nurmukhametovalexey.crocodile.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.nurmukhametovalexey.crocodile.dao.UserDAO;
import ru.nurmukhametovalexey.crocodile.model.User;

@Slf4j
@Component
public class DefaultUsers implements InitializingBean {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DefaultUsers(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void afterPropertiesSet() {
        User user = new User();
        user.setLogin("admin");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setEmail("admin@mail.com");
        user.setRole("Admin");
        user.setScore(0);
        user.setEnabled(true);
        try {
            userDAO.save(user);
        } catch (Exception e) {
            log.warn("DefaultUsers.afterPropertiesSet(): user {} already exists in db", user);
        }

    }
}