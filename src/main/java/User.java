import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String email;
    private String password;
    private String name;

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public static User getRandom() {
        String email = RandomStringUtils.randomAlphanumeric(5) + "@.mail.ru";
        String password = RandomStringUtils.randomAlphanumeric(10);
        String name = RandomStringUtils.randomAlphanumeric(10);

        return new User(email, password, name);
    }

    public static User getUserWithoutPassword() {
        String email = RandomStringUtils.randomAlphanumeric(5) + "@.mail.ru";
        String name = RandomStringUtils.randomAlphanumeric(10);

        return new User(email, name);
    }
}
