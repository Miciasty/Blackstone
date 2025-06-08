package nsk.nu.blackstone.Entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private UUID uuid;

    public Client() { /* JPA */ }

    public void setId(long id) { this.id = id; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }

    public long getId() { return this.id; }
    public UUID getUuid() { return this.uuid; }

}
