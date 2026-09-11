package app.dtos;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingProgramDTO {
    private Integer id;
    private String name;
    private String description;
    private Integer profileId;
}
