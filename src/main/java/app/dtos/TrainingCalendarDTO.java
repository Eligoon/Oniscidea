package app.dtos;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingCalendarDTO {
    private Integer id;
    private String name;
    private Integer profileId;
    private String googleCalendarId;
}