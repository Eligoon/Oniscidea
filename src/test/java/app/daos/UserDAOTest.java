package app.daos;

import app.config.HibernateConfig;
import app.entities.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserDAOTest {

    private static EntityManagerFactory emf;
    private static UserDAO userDAO;

    @BeforeAll
    static void setUp() {

        emf = HibernateConfig.getEntityManagerFactory();

        userDAO = new UserDAO(emf);
    }

    @AfterAll
    static void tearDown() {

        if (emf != null) {
            emf.close();
        }
    }

    @Test
    void shouldCreateAndFindUser() {

        User user = new User(
                "Test User",
                "test@test.com",
                "password"
        );

        User createdUser =
                userDAO.create(user);

        assertThat(
                createdUser.getId(),
                notNullValue()
        );

        User result =
                userDAO.getById(
                        createdUser.getId()
                );

        assertThat(
                result,
                notNullValue()
        );

        assertThat(
                result.getName(),
                is("Test User")
        );

        assertThat(
                result.getEmail(),
                is("test@test.com")
        );
    }

    @Test
    void shouldVerifyCorrectPassword() {

        User user = new User(
                "Test User",
                "password@test.com",
                "secret123"
        );

        assertThat(
                user.verifyPassword("secret123"),
                is(true)
        );

        assertThat(
                user.verifyPassword("wrong-password"),
                is(false)
        );
    }
}