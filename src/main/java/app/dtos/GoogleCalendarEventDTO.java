package app.dtos;

import lombok.*;

import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleCalendarEventDTO {

    private Integer userId;
    private String title;
    private String description;
    private ZonedDateTime start;
    private ZonedDateTime end;
}