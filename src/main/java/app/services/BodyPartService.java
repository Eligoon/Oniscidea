package app.services;

import app.daos.BodyPartDAO;
import app.dtos.BodyPartDTO;
import app.entities.BodyPart;
import app.exceptions.ApiException;

import java.util.List;

public class BodyPartService {

    private final BodyPartDAO bodyPartDAO;

    public BodyPartService(BodyPartDAO bodyPartDAO) {
        this.bodyPartDAO = bodyPartDAO;
    }

    public BodyPartDTO createBodyPart(
            String name,
            String color
    ) {
        if (name == null || name.isBlank()) {
            throw new ApiException(
                    400,
                    "Body part name is required"
            );
        }

        if (color == null || color.isBlank()) {
            throw new ApiException(
                    400,
                    "Body part color is required"
            );
        }

        BodyPart bodyPart =
                new BodyPart(name, color);

        BodyPart created =
                bodyPartDAO.create(bodyPart);

        return toDTO(created);
    }

    public BodyPartDTO getBodyPart(Integer id) {

        if (id == null) {
            throw new ApiException(
                    400,
                    "Body part id is required"
            );
        }

        BodyPart bodyPart =
                bodyPartDAO.getById(id);

        return toDTO(bodyPart);
    }

    public List<BodyPartDTO> getAllBodyParts() {

        List<BodyPart> bodyParts =
                bodyPartDAO.getAll();

        return bodyParts.stream()
                .map(this::toDTO)
                .toList();
    }

    public BodyPartDTO updateBodyPart(
            Integer id,
            String name,
            String color
    ) {
        if (id == null) {
            throw new ApiException(
                    400,
                    "Body part id is required"
            );
        }

        if (name == null || name.isBlank()) {
            throw new ApiException(
                    400,
                    "Body part name is required"
            );
        }

        if (color == null || color.isBlank()) {
            throw new ApiException(
                    400,
                    "Body part color is required"
            );
        }

        BodyPart existing =
                bodyPartDAO.getById(id);

        BodyPart updatedBodyPart =
                BodyPart.builder()
                        .id(existing.getId())
                        .name(name)
                        .color(color)
                        .build();

        BodyPart updated =
                bodyPartDAO.update(updatedBodyPart);

        return toDTO(updated);
    }

    public void deleteBodyPart(Integer id) {

        if (id == null) {
            throw new ApiException(
                    400,
                    "Body part id is required"
            );
        }

        bodyPartDAO.delete(id);
    }

    private BodyPartDTO toDTO(BodyPart bodyPart) {
        return new BodyPartDTO(
                bodyPart.getId(),
                bodyPart.getName(),
                bodyPart.getColor()
        );
    }
}