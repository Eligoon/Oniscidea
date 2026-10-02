package app.daos;

import app.config.HibernateConfig;
import app.entities.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManagerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Testcontainers
public class UserDAOTest {

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    private static EntityManagerFactory emf;
    private static UserDAO userDAO;

    @BeforeAll
    static void setUp() {

        emf =
                HibernateConfig.createTestEntityManagerFactory(
                        postgres.getJdbcUrl(),
                        postgres.getUsername(),
                        postgres.getPassword()
                );

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
    void shouldUpdateUser() {

        User user = new User(
                "Original Name",
                "update@test.com",
                "password"
        );

        User createdUser =
                userDAO.create(user);

        createdUser.update(
                "Updated Name",
                "update@test.com",
                "newpassword"
        );

        User updatedUser =
                userDAO.update(createdUser);

        assertThat(
                updatedUser.getName(),
                is("Updated Name")
        );

        assertThat(
                updatedUser.getEmail(),
                is("update@test.com")
        );
    }

    @Test
    void shouldDeleteUser() {

        User user = new User(
                "Delete User",
                "delete@test.com",
                "password"
        );

        User createdUser =
                userDAO.create(user);

        boolean deleted =
                userDAO.delete(
                        createdUser.getId()
                );

        assertThat(
                deleted,
                is(true)
        );
    }
}