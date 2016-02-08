package be.kdg.teamd.beatbuddy.model.users;

import java.util.Map;

import be.kdg.teamd.beatbuddy.model.organisations.Organisation;

public class User {
    private long id;
    private String email,
                   firstName,
                   lastName,
                   nickname,
                   imageUrl;
    private Map<Organisation, Role> roles;

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public Map<Organisation, Role> getRoles() {
        return roles;
    }
}
