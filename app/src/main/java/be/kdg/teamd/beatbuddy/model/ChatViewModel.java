package be.kdg.teamd.beatbuddy.model;

/**
 * Created by Ignace on 11/03/2016.
 */
public class ChatViewModel
{
    private String username;
    private String avatarUrl;
    private String message;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getAvatarUrl()
    {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl)
    {
        this.avatarUrl = avatarUrl;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
