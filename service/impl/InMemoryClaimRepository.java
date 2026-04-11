package service.impl;

import service.claim.ClaimRepository;
import model.claim.Claim;
import model.Item;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of ClaimRepository.
 * Stores claims in a HashMap (suitable for MVP/testing).
 * All data is lost when application restarts.
 * 
 * Future: Replace with DatabaseClaimRepository for production.
 */
public class InMemoryClaimRepository implements ClaimRepository {

    // Storage: claimId → Claim
    private final Map<Integer, Claim> storage = new HashMap<>();

    // Counter for generating sequential claim IDs
    private int nextClaimId = 1;

    // Counter for generating sequential claim references
    private int claimReferenceCounter = 1;

    @Override
    public Claim save(Claim claim) {
        if (claim == null) {
            throw new IllegalArgumentException("Claim cannot be null");
        }
        storage.put(claim.getClaimId(), claim);
        return claim;
    }

    @Override
    public Optional<Claim> findById(int claimId) {
        return Optional.ofNullable(storage.get(claimId));
    }

    @Override
    public List<Claim> findByItem(Item item) {
        if (item == null) {
            return new ArrayList<>();
        }

        return storage.values().stream()
            .filter(claim -> 
                claim.getLostItem().getItemId() == item.getItemId() ||
                claim.getFoundItem().getItemId() == item.getItemId()
            )
            .collect(Collectors.toList());
    }

    @Override
    public List<Claim> findByItemId(int itemId) {
        return storage.values().stream()
            .filter(claim ->
                claim.getLostItem().getItemId() == itemId ||
                claim.getFoundItem().getItemId() == itemId
            )
            .collect(Collectors.toList());
    }

    @Override
    public List<Claim> findByStatus(String status) {
        if (status == null || status.isEmpty()) {
            return new ArrayList<>();
        }

        return storage.values().stream()
            .filter(claim -> claim.getStatus().getDisplayName().equalsIgnoreCase(status))
            .collect(Collectors.toList());
    }

    @Override
    public List<Claim> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean deleteById(int claimId) {
        return storage.remove(claimId) != null;
    }

    @Override
    public int getNextClaimId() {
        return nextClaimId++;
    }

    @Override
    public String generateClaimReference() {
        return String.format("CLM-%04d", claimReferenceCounter++);
    }
}
