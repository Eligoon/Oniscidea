package app;


import app.config.HibernateConfig;
import app.daos.UserDAO;
import app.entities.User;
import jakarta.persistence.EntityManagerFactory;



//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        EntityManagerFactory emf =
                HibernateConfig.getEntityManagerFactory();

        UserDAO userDAO = new UserDAO(emf);

        User user = new User(
                "Test User",
                "test@test.com",
                "password"
        );

        userDAO.create(user);

        User result = userDAO.getById(user.getId());

        System.out.println(result);
        }
    }

