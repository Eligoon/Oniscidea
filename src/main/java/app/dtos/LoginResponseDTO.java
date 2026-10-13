package app.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    // returns the user's public information and the JWT without exposing the password.

    private Integer id;
    private String name;
    private String email;
    private String token;
}
