package app.dtos;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDTO {
    private Integer id;
    private String name;
    private Integer userId;
}
