package nsk.nu.blackstone.Service;

import nsk.nu.blackstone.Entity.Client;
import nsk.nu.blackstone.Entity.DTO.DClient;
import nsk.nu.blackstone.Interface.BlackstoneService;
import nsk.nu.blackstone.PluginInstance;
import nsk.nu.blackstone.Repository.ClientRepository;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ClientService implements BlackstoneService<DClient, Long> {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
        this.registeredUUIDs = new HashSet<>();

        PluginInstance.getInstance().getServer().getOnlinePlayers().forEach(player -> {
            this.registerPlayer(player.getUniqueId());
        });
    }

    /* ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- */

    public static DClient toDTO(Client entity) {
        if (entity == null) return null;
        DClient dto = new DClient();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
        return dto;
    }

    public static List<DClient> toDTO(List<Client> entities) {
        return entities.stream().map(ClientService::toDTO).toList();
    }

    public static Client toEntity(DClient dto) {
        if (dto == null) return null;
        Client entity = new Client();
        entity.setId(dto.getId());
        entity.setUuid(dto.getUuid());
        return entity;
    }

    public static List<Client> toEntity(List<DClient> dtos) {
        return dtos.stream().map(ClientService::toEntity).toList();
    }

    /* ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- */

    @Override
    public CompletableFuture<DClient> getAsync(Long id) {
        return repository.findByIdAsync(id).thenApply(ClientService::toDTO);
    }

    public CompletableFuture<DClient> getByClientAsync(UUID uuid) {
        return repository.findByClientAsync(uuid).thenApply(ClientService::toDTO);
    }

    @Override
    public CompletableFuture<List<DClient>> getAllAsync() {
        return repository.findAllAsync().thenApply(ClientService::toDTO);
    }

    @Override
    public CompletableFuture<Void> saveAsync(DClient entity) {
        return repository.saveAsync(toEntity(entity));
    }

    @Override
    public CompletableFuture<Void> deleteAsync(DClient entity) {
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
    public DClient get(Long id) {
        return toDTO(repository.findById(id));
    }
    public DClient getByClient(UUID uuid) {
        return toDTO(repository.findByClient(uuid));
    }
    @Override
    public List<DClient> getAll() {
        return toDTO(repository.findAll());
    }
    @Override
    public boolean save(DClient entity) {
        return repository.save(toEntity(entity));
    }
    @Override
    public boolean delete(DClient entity) {
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

    /* ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- */

    private final Set<UUID> registeredUUIDs;

    public void registerPlayer(UUID player) {
        if (isRegistered(player)) return;
        registeredUUIDs.add(player);
    }

    public void unregisterPlayer(UUID player) {
        if (!isRegistered(player)) return;
        registeredUUIDs.remove(player);
    }

    public boolean isRegistered(UUID player) {
        return registeredUUIDs.contains(player);
    }


}
