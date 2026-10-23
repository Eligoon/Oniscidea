package app.dtos;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseDTO {
    private Integer id;
    private String name;
    private String description;
    private List<BodyPartDTO> bodyParts;
}
