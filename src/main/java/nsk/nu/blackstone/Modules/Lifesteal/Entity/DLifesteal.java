package nsk.nu.blackstone.Modules.Lifesteal.Entity;

import nsk.nu.blackstone.Entity.DTO.DClient;
import nsk.nu.blackstone.Entity.DTO.DScenario;

public class DLifesteal {

    private long id;

    private DClient client;
    private DScenario scenario;

    private double maxHealth;
    private double health;
    private long deaths;
    private long kills;

    public DLifesteal() { /* */ }

    public long getId() { return this.id; }
    public void setId(long id) { this.id = id; }

    public DClient getClient() { return this.client; }
    public DScenario getScenario() { return this.scenario; }

    public void setClient(DClient client) { this.client = client; }
    public void setScenario(DScenario scenario) { this.scenario = scenario; }

    public double getMaxHealth() { return this.maxHealth; }
    public void setMaxHealth(double maxHealth) { this.maxHealth = maxHealth; }

    public double getHealth() { return this.health; }
    public void setHealth(double health) { this.health = health; }

    public long getDeaths() { return this.deaths; }
    public void setDeaths(long deaths) { this.deaths = deaths; }

    public long getKills() { return this.kills; }
    public void setKills(long kills) { this.kills = kills; }

}
