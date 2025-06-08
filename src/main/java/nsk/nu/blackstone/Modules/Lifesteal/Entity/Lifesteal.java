package nsk.nu.blackstone.Modules.Lifesteal.Entity;

import jakarta.persistence.*;
import nsk.nu.blackstone.Entity.Client;
import nsk.nu.blackstone.Entity.Scenario;

@Entity(name = "lifesteal_data")
public class Lifesteal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;

    @Column(nullable = false) private double maxHealth;
    @Column(nullable = false) private double health;
    @Column(nullable = false) private long deaths;
    @Column(nullable = false) private long kills;

    public Lifesteal() { /* JPA */ }

    public void setId(long id) { this.id = id; }
    public long getId() { return this.id; }

    public Client getClient() { return this.client; }
    public Scenario getScenario() { return this.scenario; }

    public void setClient(Client client) { this.client = client; }
    public void setScenario(Scenario scenario) { this.scenario = scenario; }

    public double getMaxHealth() { return this.maxHealth; }
    public void setMaxHealth(double maxHealth) { this.maxHealth = maxHealth; }

    public double getHealth() { return this.health; }
    public void setHealth(double health) { this.health = health; }

    public long getDeaths() { return this.deaths; }
    public void setDeaths(long deaths) { this.deaths = deaths; }

    public long getKills() { return this.kills; }
    public void setKills(long kills) { this.kills = kills; }

}
