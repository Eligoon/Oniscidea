package app.dtos;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SetLogDTO {

    private Integer id;
    private Integer exerciseLogId;
    private Integer setNumber;
    private Double weight;
    private Integer reps;
    private Integer rir;
    private boolean completed;
}