package app.dtos;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleCalendarConnectionDTO {

    private Integer id;
    private Integer userId;
    private String googleUserId;
    private String calendarId;
    private boolean connected;
}