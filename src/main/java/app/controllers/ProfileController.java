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

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        ProfileDTO profile = profileService.createProfile(
                request.getName(),
                authenticatedUserId
        );

        ctx.status(201).json(profile);
    }

    public void getAll(Context ctx) {
        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        List<ProfileDTO> profiles =
                profileService.getAllProfilesForUser(authenticatedUserId);

        ctx.json(profiles);
    }
    public void getById(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        ProfileDTO profile =
                profileService.getProfileForUser(
                        id,
                        authenticatedUserId
                );

        ctx.json(profile);
    }

    public void update(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        ProfileDTO request =
                ctx.bodyAsClass(ProfileDTO.class);

        ProfileDTO profile =
                profileService.updateProfileForUser(
                        id,
                        authenticatedUserId,
                        request.getName()
                );

        ctx.json(profile);
    }

    public void delete(Context ctx) {

        Integer id =
                Integer.parseInt(
                        ctx.pathParam("id")
                );

        Integer authenticatedUserId =
                ctx.attribute("authenticatedUserId");

        profileService.deleteProfileForUser(
                id,
                authenticatedUserId
        );

        ctx.status(204);
    }
}