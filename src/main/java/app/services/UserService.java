package app.services;

import app.daos.UserDAO;
import app.dtos.UserDTO;
import app.entities.User;
import app.exceptions.ApiException;

import java.util.List;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserDTO createUser(
            String name,
            String email,
            String password
    ) {

        if (name == null || name.isBlank()) {
            throw new ApiException(
                    400,
                    "Name is required"
            );
        }

        if (email == null || email.isBlank()) {
            throw new ApiException(
                    400,
                    "Email is required"
            );
        }

        if (password == null || password.isBlank()) {
            throw new ApiException(
                    400,
                    "Password is required"
            );
        }

        User user =
                userDAO.createUser(
                        name,
                        email,
                        password
                );

        return toDTO(user);
    }

    public UserDTO getUser(Integer id) {

        if (id == null) {
            throw new ApiException(
                    400,
                    "User ID is required"
            );
        }

        User user =
                userDAO.getById(id);

        return toDTO(user);
    }

    public List<UserDTO> getAllUsers() {

        List<User> users =
                userDAO.getAll();

        return users.stream()
                .map(this::toDTO)
                .toList();
    }

    public UserDTO updateUser(
            Integer id,
            String name,
            String email,
            String password
    ) {

        if (id == null) {
            throw new ApiException(
                    400,
                    "User id is required"
            );
        }

        if (name == null || name.isBlank()) {
            throw new ApiException(
                    400,
                    "Name is required"
            );
        }

        if (email == null || email.isBlank()) {
            throw new ApiException(
                    400,
                    "Email is required"
            );
        }

        if (password == null || password.isBlank()) {
            throw new ApiException(
                    400,
                    "Password is required"
            );
        }

        User user =
                userDAO.getById(id);

        user.update(
                name,
                email,
                password
        );

        User updatedUser =
                userDAO.update(user);

        return toDTO(updatedUser);
    }

    public UserDTO login(
            String email,
            String password
    ) {

        if (email == null || email.isBlank()) {
            throw new ApiException(
                    400,
                    "Email is required"
            );
        }

        if (password == null || password.isBlank()) {
            throw new ApiException(
                    400,
                    "Password is required"
            );
        }

        User user =
                userDAO.getVerifiedUser(
                        email,
                        password
                );

        return toDTO(user);
    }

    private UserDTO toDTO(User user) {

        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                null
        );
    }
}