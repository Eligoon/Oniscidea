package app.services;

import app.daos.ProfileDAO;
import app.daos.UserDAO;
import app.dtos.ProfileDTO;
import app.entities.Profile;
import app.entities.User;
import app.exceptions.ApiException;

import java.util.List;

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

    public ProfileDTO getProfile(Integer id) {

        if (id == null) {
            throw new ApiException(
                    400,
                    "Profile id is required"
            );
        }

        Profile profile =
                profileDAO.getById(id);

        return toDTO(profile);
    }

    public List<ProfileDTO> getAllProfiles() {

        List<Profile> profiles =
                profileDAO.getAll();

        return profiles.stream()
                .map(this::toDTO)
                .toList();
    }

    public ProfileDTO updateProfile(
            Integer id,
            String name
    ) {

        if (id == null) {
            throw new ApiException(
                    400,
                    "Profile id is required"
            );
        }

        if (name == null || name.isBlank()) {
            throw new ApiException(
                    400,
                    "Profile name is required"
            );
        }

        Profile profile =
                profileDAO.getById(id);

        profile.update(name);

        Profile updated =
                profileDAO.update(profile);

        return toDTO(updated);
    }

    public void deleteProfile(Integer id) {

        if (id == null) {
            throw new ApiException(
                    400,
                    "Profile id is required"
            );
        }

        profileDAO.delete(id);
    }

    private ProfileDTO toDTO(Profile profile) {

        return new ProfileDTO(
                profile.getId(),
                profile.getName(),
                profile.getUser().getId()
        );
    }
}