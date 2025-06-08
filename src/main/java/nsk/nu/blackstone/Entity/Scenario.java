package nsk.nu.blackstone.Entity;

import jakarta.persistence.*;

@Entity(name = "scenario")
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    public Scenario() { /* JPA */ }

    public void setId(long id) { this.id = id; }
    public long getId() { return this.id; }

    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }
}
