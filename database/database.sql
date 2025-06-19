-- Write your CREATE TABLE statements here and optionally your INSERT statements if you want static test data
CREATE TABLE example_table (
    hello_message TEXT NOT NULL
);

INSERT INTO example_table SET hello_message = 'Congratulations, the environment works';


CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

INSERT INTO users (name) VALUES
    ('Jukka'),
    ('Pekka'),
    ('Olle'),
    ('Kalle'),
    ('Britney');

CREATE TABLE messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    sent_at DATETIME NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES users(id)
);

CREATE TABLE message_recipients (
    message_id INT NOT NULL,
    recipient_id INT NOT NULL,
    PRIMARY KEY (message_id, recipient_id),
    FOREIGN KEY (message_id) REFERENCES messages(id),
    FOREIGN KEY (recipient_id) REFERENCES users(id)
);

CREATE INDEX idx_message_recipients_recipient_id ON message_recipients(recipient_id);
CREATE INDEX idx_messages_sender_id ON messages(sender_id);
CREATE INDEX idx_messages_sent_at ON messages(sent_at);
