package fi.invian.codingassignment.pojos;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;


public class MessageParameters {

    private @Positive int senderId;
    private @NotEmpty String title;
    private @NotEmpty String body;
    private @NotBlank String sentAt;

    @Valid @Size(min = 1, max = 5, message = "You must specify 1 to 5 recipients")
    private List<@Positive(message = "Recipient IDs must be positive") Integer> recipientId;

    public MessageParameters() {
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
