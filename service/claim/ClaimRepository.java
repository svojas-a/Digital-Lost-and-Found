package service.claim;

import model.claim.Claim;
import model.Item;
import java.util.List;
import java.util.Optional;

/**
 * Repository Interface for Claim persistence.
 * Abstraction layer - allows swapping storage backends (in-memory, DB, JSON).
 * 
 * RESPONSIBILITIES:
 *  - CRUD operations on Claim objects
 *  - Querying claims by various criteria
 *  - Ensuring data consistency
 */
public interface ClaimRepository {

    /**
     * Save a claim (insert or update).
     * If claim.claimId exists and claim is already stored → update
     * If claim.claimId is new → insert
     * 
     * @param claim The claim to save
     * @return The saved claim (may have generated ID)
     */
    Claim save(Claim claim);

    /**
     * Find a claim by its ID.
     * 
     * @param claimId The claim ID
     * @return Optional containing the claim, or empty if not found
     */
    Optional<Claim> findById(int claimId);

    /**
     * Find all claims for a specific item.
     * Useful for checking if an item already has an active claim.
     * 
     * @param item The item
     * @return List of claims involving this item (may be empty)
     */
    List<Claim> findByItem(Item item);

    /**
     * Find all claims for a given item (by item ID).
     * Useful when only item ID is available.
     * 
     * @param itemId The item ID
     * @return List of claims involving this item
     */
    List<Claim> findByItemId(int itemId);

    /**
     * Find all claims with a specific status.
     * Useful for verifier queues (e.g., all UNDER_VERIFICATION claims).
     * 
     * @param status The status to filter by
     * @return List of claims with that status
     */
    List<Claim> findByStatus(String status);

    /**
     * Get all claims in the repository.
     * 
     * @return List of all claims
     */
    List<Claim> findAll();

    /**
     * Delete a claim by ID.
     * Note: Should typically not delete claims (audit trail), but interface allows it.
     * 
     * @param claimId The claim ID to delete
     * @return true if deleted, false if not found
     */
    boolean deleteById(int claimId);

    /**
     * Get the next available claim ID for new claims.
     * Implementation-dependent (counter, UUID, etc.).
     * 
     * @return Next available ID
     */
    int getNextClaimId();

    /**
     * Generate a human-readable claim reference (e.g., "CLM-2024-001").
     * 
     * @return Unique reference string
     */
    String generateClaimReference();
}
