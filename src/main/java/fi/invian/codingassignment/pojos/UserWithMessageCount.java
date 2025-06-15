package fi.invian.codingassignment.pojos;

public class UserWithMessageCount {

    private User user;
    private int messageCount;

    public UserWithMessageCount(User user, int messageCount) {
        this.user = user;
        this.messageCount = messageCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }
}
