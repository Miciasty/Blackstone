package nsk.nu.blackstone.Modules.Lifesteal;

import nsk.nu.blackstone.Entity.DTO.DClient;
import nsk.nu.blackstone.Entity.DTO.DScenario;
import nsk.nu.blackstone.Interface.BlackstoneService;
import nsk.nu.blackstone.Modules.Lifesteal.Entity.DLifesteal;
import nsk.nu.blackstone.Modules.Lifesteal.Entity.Lifesteal;
import nsk.nu.blackstone.Service.ClientService;
import nsk.nu.blackstone.Service.ScenarioService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LifestealService implements BlackstoneService<DLifesteal, Long> {

    private final LifestealRepository repository;

    public LifestealService(LifestealRepository repository) {
        this.repository = repository;
    }

    /* ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- */

    public static DLifesteal toDTO(Lifesteal entity) {
        if (entity == null) return null;
        DLifesteal dto = new DLifesteal();

        dto.setId(entity.getId());
        dto.setClient(ClientService.toDTO( entity.getClient()) );
        dto.setScenario(ScenarioService.toDTO( entity.getScenario() ));
        dto.setMaxHealth(entity.getMaxHealth() );
        dto.setHealth(entity.getHealth() );
        dto.setKills(entity.getKills() );
        dto.setDeaths(entity.getDeaths() );

        return dto;
    }

    public static List<DLifesteal> toDTO(List<Lifesteal> entities) {
        return entities.stream().map(LifestealService::toDTO).toList();
    }

    public static Lifesteal toEntity(DLifesteal dto) {
        if (dto == null) return null;
        Lifesteal entity = new Lifesteal();

        entity.setId(dto.getId());
        entity.setClient(ClientService.toEntity( dto.getClient()) );
        entity.setScenario(ScenarioService.toEntity( dto.getScenario() ));
        entity.setMaxHealth(dto.getMaxHealth() );
        entity.setHealth(dto.getHealth() );
        entity.setKills(dto.getKills() );
        entity.setDeaths(dto.getDeaths() );

        return entity;
    }

    public static List<Lifesteal> toEntity(List<DLifesteal> dtos) {
        return dtos.stream().map(LifestealService::toEntity).toList();
    }

    /* ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- */

    @Override
    public CompletableFuture<DLifesteal> getAsync(Long id) {
        return repository.findByIdAsync(id).thenApply(LifestealService::toDTO);
    }

    public CompletableFuture<List<DLifesteal>> getAllByScenarioAsync(String scenarioName) {
        return repository.findAllByScenarioAsync(scenarioName).thenApply(LifestealService::toDTO);
    }

    public CompletableFuture<DLifesteal> getByClientAndScenarioAsync(UUID uuid, String scenarioName) {
        return repository
                .findByClientAndScenarioAsync(uuid, scenarioName)
                .thenApply(LifestealService::toDTO);
    }

    public CompletableFuture<DLifesteal> getByClientAndScenarioAsync(DClient client, DScenario scenario) {
        return repository
                .findByClientAndScenarioAsync(client.getUuid(), scenario.getName())
                .thenApply(LifestealService::toDTO);
    }

    @Override
    public CompletableFuture<List<DLifesteal>> getAllAsync() {
        return repository.findAllAsync().thenApply(LifestealService::toDTO);
    }

    @Override
    public CompletableFuture<Void> saveAsync(DLifesteal entity) {
        return repository.saveAsync(toEntity(entity));
    }

    @Override
    public CompletableFuture<Void> deleteAsync(DLifesteal entity) {
        return repository.deleteAsync(toEntity(entity));
    }

    @Override
    public CompletableFuture<Void> deleteByIdAsync(Long id) {
        return null;
    }

    @Override
    public CompletableFuture<Void> deleteAllAsync() {
        return null;
    }

    /* ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- */

    @Override
    public DLifesteal get(Long id) {
        return toDTO(repository.findById(id));
    }

    public List<DLifesteal> getAllByScenario(String scenarioName) {
        return toDTO(repository.findAllByScenario(scenarioName));
    }

    public DLifesteal getByClientAndScenario(UUID uuid, String scenarioName) {
        return toDTO(repository.findByClientAndScenario(uuid, scenarioName));
    }

    public DLifesteal getByClientAndScenario(DClient client, DScenario scenario) {
        return toDTO(repository.findByClientAndScenario(client.getUuid(), scenario.getName()));
    }
    @Override
    public List<DLifesteal> getAll() {
        return toDTO(repository.findAll());
    }
    @Override
    public boolean save(DLifesteal entity) {
        return repository.save(toEntity(entity));
    }
    @Override
    public boolean delete(DLifesteal entity) {
        return repository.delete(toEntity(entity));
    }
    @Override
    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }
    @Override
    public boolean deleteAll() {
        return repository.deleteAll();
    }
    
}
