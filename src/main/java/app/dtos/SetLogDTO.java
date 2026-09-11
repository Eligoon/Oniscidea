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
    private Integer reps;
    private Double weight;
    private Integer rir;
}
