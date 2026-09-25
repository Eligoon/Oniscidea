package app.controllers;

import app.dtos.ProfileDTO;
import app.services.ProfileService;
import io.javalin.http.Context;

import java.util.List;

public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    public void create(Context ctx) {

        ProfileDTO request =
                ctx.bodyAsClass(ProfileDTO.class);

        ProfileDTO profile =
                profileService.createProfile(
                        request.getName(),
                        request.getUserId()
                );

        ctx.status(201).json(profile);
    }

    public void getAll(Context ctx) {

        List<ProfileDTO> profiles =
                profileService.getAllProfiles();

        ctx.json(profiles);
    }

    public void getById(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        ProfileDTO profile =
                profileService.getProfile(id);

        ctx.json(profile);
    }

    public void update(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        ProfileDTO request =
                ctx.bodyAsClass(ProfileDTO.class);

        ProfileDTO profile =
                profileService.updateProfile(
                        id,
                        request.getName()
                );

        ctx.json(profile);
    }

    public void delete(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        profileService.deleteProfile(id);

        ctx.status(204);
    }
}