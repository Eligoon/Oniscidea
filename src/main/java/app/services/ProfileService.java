package app.services;

import app.daos.ProfileDAO;
import app.daos.UserDAO;
import app.dtos.ProfileDTO;
import app.entities.Profile;
import app.entities.User;
import app.exceptions.ApiException;

public class ProfileService {

    private final ProfileDAO profileDAO;
    private final UserDAO userDAO;

    public ProfileService(
            ProfileDAO profileDAO,
            UserDAO userDAO
    ) {
        this.profileDAO = profileDAO;
        this.userDAO = userDAO;
    }

    public ProfileDTO createProfile(
            String name,
            Integer userId
    ) {

        if (name == null || name.isBlank()) {
            throw new ApiException(
                    400,
                    "Profile name is required"
            );
        }

        if (userId == null) {
            throw new ApiException(
                    400,
                    "User id is required"
            );
        }

        User user =
                userDAO.getById(userId);

        Profile profile =
                new Profile(
                        name,
                        user
                );

        Profile created =
                profileDAO.create(profile);

        return toDTO(created);
    }

    private ProfileDTO toDTO(Profile profile) {

        return new ProfileDTO(
                profile.getId(),
                profile.getName(),
                profile.getUser().getId()
        );
    }
}