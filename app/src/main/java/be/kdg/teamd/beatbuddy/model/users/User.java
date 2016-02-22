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

    public void setId(long id)
    {
        this.id = id;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public void setRoles(Map<Organisation, Role> roles)
    {
        this.roles = roles;
    }
}
