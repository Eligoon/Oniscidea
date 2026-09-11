package app.dtos;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodyPartDTO {
    private Integer id;
    private String name;
    private String color;
}
