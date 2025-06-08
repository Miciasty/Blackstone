package nsk.nu.blackstone.Service;

import nsk.nu.blackstone.Entity.Scenario;
import nsk.nu.blackstone.Entity.DTO.DScenario;
import nsk.nu.blackstone.Interface.BlackstoneService;
import nsk.nu.blackstone.Repository.ScenarioRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ScenarioService implements BlackstoneService<DScenario, Long> {

    private final ScenarioRepository repository;

    public ScenarioService(ScenarioRepository repository) {
        this.repository = repository;
    }

    /* ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- */

    public static DScenario toDTO(Scenario entity) {
        if (entity == null) return null;
        DScenario dto = new DScenario();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    public static List<DScenario> toDTO(List<Scenario> entities) {
        return entities.stream().map(ScenarioService::toDTO).toList();
    }

    public static Scenario toEntity(DScenario dto) {
        if (dto == null) return null;
        Scenario entity = new Scenario();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }

    public static List<Scenario> toEntity(List<DScenario> dtos) {
        return dtos.stream().map(ScenarioService::toEntity).toList();
    }

    /* ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- */

    @Override
    public CompletableFuture<DScenario> getAsync(Long id) {
        return repository.findByIdAsync(id).thenApply(ScenarioService::toDTO);
    }

    public CompletableFuture<DScenario> getByNameAsync(String name) {
        return repository.findByNameAsync(name).thenApply(ScenarioService::toDTO);
    }

    public CompletableFuture<DScenario> getByNameIgnoreCaseAsync(String name) {
        return repository.findByNameIgnoreCaseAsync(name).thenApply(ScenarioService::toDTO);
    }

    @Override
    public CompletableFuture<List<DScenario>> getAllAsync() {
        return repository.findAllAsync().thenApply(ScenarioService::toDTO);
    }

    @Override
    public CompletableFuture<Void> saveAsync(DScenario entity) {
        return repository.saveAsync(toEntity(entity));
    }

    @Override
    public CompletableFuture<Void> deleteAsync(DScenario entity) {
        return repository.deleteAsync(toEntity(entity));
    }

    @Override
    public CompletableFuture<Void> deleteByIdAsync(Long id) {
        return repository.deleteByIdAsync(id);
    }

    @Override
    public CompletableFuture<Void> deleteAllAsync() {
        return repository.deleteAllAsync();
    }

    /* ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- */

    @Override
    public DScenario get(Long id) {
        return toDTO(repository.findById(id));
    }

    public DScenario getByName(String name) {
        return toDTO(repository.findByName(name));
    }
    public DScenario getByNameIgnoreCase(String name) {
        return toDTO(repository.findByNameIgnoreCase(name));
    }
    @Override
    public List<DScenario> getAll() {
        return toDTO(repository.findAll());
    }
    @Override
    public boolean save(DScenario entity) {
        return repository.save(toEntity(entity));
    }
    @Override
    public boolean delete(DScenario entity) {
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
