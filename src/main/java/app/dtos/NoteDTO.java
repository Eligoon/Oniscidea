package app.dtos;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteDTO {

    private Integer id;
    private String title;
    private String text;
    private Integer profileId;
}