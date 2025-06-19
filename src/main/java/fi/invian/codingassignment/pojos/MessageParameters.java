package fi.invian.codingassignment.pojos;

import fi.invian.codingassignment.rest.utils.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Set;


public class MessageParameters {

    private @Positive int senderId;
    private @NotEmpty String title;
    private @NotEmpty String body;

    @DateTimeFormat
    private @NotBlank String sentAt;

    @Valid @Size(min = 1, max = 5, message = "You must specify 1 to 5 recipients")
    private Set<@Positive(message = "Recipient IDs must be positive") Integer> recipientId;

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

    public Set<Integer> getRecipientIds() {
        return recipientId;
    }

    public void setRecipientId(Set<Integer> recipientId) {
        this.recipientId = recipientId;
    }
}
