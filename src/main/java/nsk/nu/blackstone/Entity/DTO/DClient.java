package nsk.nu.blackstone.Entity.DTO;

import java.util.UUID;

public class DClient {
    private long id;
    private UUID uuid;

    public DClient() { };

    public long getId() { return this.id; }
    public void setId(long id) { this.id = id; }

    public void setUuid(UUID uuid) { this.uuid = uuid; }
    public UUID getUuid() { return this.uuid; }
}
