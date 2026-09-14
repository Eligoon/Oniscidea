package app.services;

import app.config.HibernateConfig;
import app.daos.UserDAO;
import app.dtos.UserDTO;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserServiceTest {

    private static EntityManagerFactory emf;
    private static UserDAO userDAO;
    private static UserService userService;

    @BeforeAll
    static void setUp() {

        emf =
                HibernateConfig
                        .getEntityManagerFactory();

        userDAO =
                new UserDAO(emf);

        userService =
                new UserService(userDAO);
    }

    @AfterAll
    static void tearDown() {

        if (emf != null) {
            emf.close();
        }
    }

    @Test
    void shouldCreateUser() {

        UserDTO result =
                userService.createUser(
                        "Service User",
                        "service@test.com",
                        "password123"
                );

        assertThat(
                result,
                notNullValue()
        );

        assertThat(
                result.getId(),
                notNullValue()
        );

        assertThat(
                result.getName(),
                is("Service User")
        );

        assertThat(
                result.getEmail(),
                is("service@test.com")
        );

        assertThat(
                result.getPassword(),
                nullValue()
        );
    }

    @Test
    void shouldGetUser() {

        UserDTO created =
                userService.createUser(
                        "Get User",
                        "get@test.com",
                        "password123"
                );

        UserDTO result =
                userService.getUser(
                        created.getId()
                );

        assertThat(
                result.getId(),
                is(created.getId())
        );

        assertThat(
                result.getName(),
                is("Get User")
        );
    }

    @Test
    void shouldLogin() {

        userService.createUser(
                "Login User",
                "login@test.com",
                "password123"
        );

        UserDTO result =
                userService.login(
                        "login@test.com",
                        "password123"
                );

        assertThat(
                result,
                notNullValue()
        );

        assertThat(
                result.getEmail(),
                is("login@test.com")
        );
    }
}