package app.dtos;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteDTO {
    private Integer id;
    private Integer profileId;
    private Integer trainingSessionId;
    private String text;
}
