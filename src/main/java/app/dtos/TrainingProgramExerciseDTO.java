package app.dtos;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingProgramExerciseDTO {
    private Integer id;
    private Integer trainingProgramId;
    private Integer exerciseId;
    private Integer exerciseOrder;
    private Integer plannedSets;
    private Integer plannedReps;
    private Integer restSeconds;
}
