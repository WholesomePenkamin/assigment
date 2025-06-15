package fi.invian.codingassignment.pojos;

import java.util.List;


public class MessageParameters {

    private int id;
    private int senderId;
    private String title;
    private String body;
    private String sentAt;
    private List<Integer> recipientId;

    public MessageParameters(int id, int senderId, String title, String body, String sentAt) {
        this.id = id;
        this.senderId = senderId;
        this.title = title;
        this.body = body;
        this.sentAt = sentAt;
    }

    public MessageParameters(int id, int senderId, String title, String body, String sentAt, List<Integer> recipientId) {
        this.id = id;
        this.senderId = senderId;
        this.title = title;
        this.body = body;
        this.sentAt = sentAt;
        this.recipientId = recipientId;
    }

    public MessageParameters() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
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

    public List<Integer> getRecipientIds() {
        return recipientId;
    }

    public void setRecipientId(List<Integer> recipientId) {
        this.recipientId = recipientId;
    }
}
