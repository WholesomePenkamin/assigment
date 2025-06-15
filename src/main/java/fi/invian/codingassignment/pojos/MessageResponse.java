package fi.invian.codingassignment.pojos;

import java.util.List;


public class MessageResponse {

    private int id;
    private User sender;
    private String title;
    private String body;
    private String sentAt;

    public MessageResponse(int id, User sender, String title, String body, String sentAt) {
        this.id = id;
        this.sender = sender;
        this.title = title;
        this.body = body;
        this.sentAt = sentAt;
    }

    public MessageResponse() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }
}
