package helpers.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.user.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreds {
    private String email;
    private String password;
    private String name;

    public static UserCreds from(User user) {
        return new UserCreds(user.getEmail(), user.getPassword(), user.getName());
    }
}
