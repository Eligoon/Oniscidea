package app.dtos;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseLogDTO {

    private Integer id;
    private Integer trainingSessionId;
    private Integer exerciseId;
}