package app.dtos;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingSessionDTO {
    private Integer id;
    private Integer calendarId;
    private Integer trainingProgramId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean completed;
    private String googleEventId;
}