CREATE TABLE tickets (
    id UUID NOT NULL PRIMARY KEY,
    event_id UUID NOT NULL,
    customer_name VARCHAR(150) NOT NULL,
    customer_email VARCHAR(150) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    sold_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,

    CONSTRAINT fk_tickets_event_id FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE RESTRICT
);

CREATE INDEX idx_tickets_event_id ON tickets(event_id);