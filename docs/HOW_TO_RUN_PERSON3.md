# How to Run Person 3 Claim + Verification System

## Quick Start

### Compile Everything
```bash
javac -d . \
  model/claim/*.java \
  model/validator/*.java \
  service/claim/*.java \
  service/impl/*.java \
  service/observer/*.java \
  controller/ClaimController.java \
  view/ClaimView.java \
  ClaimMain.java
```

### Run the Complete Demo
```bash
java ClaimMain
```

### Run Person 2's Search Demo (Auto-triggers Claims)
```bash
java SearchMain
```

---

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     USER / ADMIN                             │
└────────────┬────────────────────────────────────────────────┘
             │
             ↓
┌─────────────────────────────────────────────────────────────┐
│                  ClaimController (MVC)                       │
│  - handleRequestClaim()                                      │
│  - handleStartVerification()                                 │
│  - handleApproveDecision()                                   │
│  - handleRejectDecision()                                    │
└────────────┬────────────────────────────────────────────────┘
             │
             ↓
┌─────────────────────────────────────────────────────────────┐
│         ClaimService (Business Logic)                        │
│  - createClaimFromMatch()                                    │
│  - requestClaim()                                            │
│  - startVerification()                                        │
│  - submitVerification()                                      │
│  - approveClaim()                                            │
│  - rejectClaim()                                             │
└────────────┬────────────────────────────────────────────────┘
             │
             ├─→ ClaimRepository (Persistence)
             │   - InMemoryClaimRepository (MVP)
             │   - Can be replaced with DB implementation
             │
             └─→ Validators
                 - ClaimValidator (business rules)
                 - ItemStatusValidator (state transitions)

OBSERVER INTEGRATION (Auto-Claim Creation):
┌─────────────────────────────────────────────────────────────┐
│         SearchService.findAllMatches()                       │
│  (Person 2 - runs matching engine)                           │
└────────────┬────────────────────────────────────────────────┘
             │
             ├─→ 1. Run MatchingStrategy
             │
             ├─→ 2. Update items to "Matched" status ⭐
             │
             ├─→ 3. notifyObservers(matches) ⭐
             │      │
             │      └─→ ClaimCreationObserver
             │          ├─ For each match:
             │          ├─ Call createClaimFromMatch()
             │          └─ Auto-create Claim (status=MATCHED)
             │
             └─→ 4. Return results

ITEM STATUS FLOW:
  Reported → Searching → [Matched] → Claimed → Closed
             (Person 2)  (Person 3) (Person 3)
```

---

## Complete Workflow

### 1. **Matching Phase** (Person 2 - already implemented)
   - SearchService finds matches between lost/found items
   - MatchingStrategy scores pairs (keyword, location, description overlap)
   - Threshold: score ≥ 30 to be a potential match

### 2. **Auto-Claim Creation** (Person 3 - NEW)
   - ClaimCreationObserver triggers on match detection
   - For each MatchResult:
     - `createClaimFromMatch()` called
     - Claim created with status = MATCHED
     - Stored in ClaimRepository
     - Items transitioned to "Matched" status

### 3. **Claim Request** (Person 3)
   - Claimant views matched claims (status = MATCHED)
   - Clicks "Request this claim"
   - `requestClaim()` → status = CLAIM_REQUESTED
   - Stored for verifier review

### 4. **Verification Assignment** (Admin/System)
   - Admin/system assigns verifier to claim
   - `startVerification()` → status = UNDER_VERIFICATION
   - Verifier receives notification (Person 4)

### 5. **Verification Process** (Verifier)
   - Verifier examines:
     - Item descriptions
     - Match confidence score
     - Claimant provided evidence
     - Submitted documents
   - Creates Verification record with:
     - Method (MANUAL, DOCUMENT, WITNESS, VIDEO)
     - Confidence level (0-100)
     - Detailed notes
     - Decision (APPROVED or REJECTED)

### 6a. **Approval Path**
   - `submitVerification(decision=APPROVED)`
   - `approveClaim()` called:
     - Lost item owner → Finder name/contact
     - Items transitioned to "Claimed"
     - Claim transitioned to CLOSED
     - **Ownership transfer complete**
   - Notification sent (Person 4)

### 6b. **Rejection Path**
   - `submitVerification(decision=REJECTED)`
   - `rejectClaim()` called:
     - Items reverted to "Reported"
     - Claim transitioned to CLAIM_REJECTED (terminal)
     - Items available for re-matching
   - Rejection reason stored for audit trail
   - Notification sent (Person 4)

---

## Demo Output Example

Running `java ClaimMain` produces output like:

```
╔════════════════════════════════════════════════════════════╗
║  CLAIM + VERIFICATION SYSTEM DEMO (Person 3)               ║
╚════════════════════════════════════════════════════════════╝

[SETUP] Initializing system...
[SETUP] ✓ System ready

═══════════════════════════════════════════════════════════════
  PHASE 1: MATCHING ENGINE
═══════════════════════════════════════════════════════════════

[OBSERVER] ClaimCreationObserver triggered: 3 match(es) detected
[CLAIM] Auto-created: CLM-0001 (Lost Item #1 vs Found Item #101)
[OBSERVER] ✓ Claim created: CLM-0001
...

═══════════════════════════════════════════════════════════════
  PHASE 3: CLAIMANT SUBMITS CLAIM REQUEST
═══════════════════════════════════════════════════════════════

[ACTION] Claimant 'Swathi' requests Claim #1...
[CLAIM] Status updated to CLAIM_REQUESTED: CLM-0001

==========================================================
  ✓ CLAIM REQUEST SUBMITTED
==========================================================
  ID: 1
  Reference: CLM-0001
  Claimant: Swathi (9876543210)
  Finder: Security (9000000001)
  Lost Item: #1 - Black Wallet
  Found Item: #101 - Wallet
  Match Confidence: 85%
  Status: Claim Requested
  Next: Awaiting verifier assignment
==========================================================

[ACTION] Admin assigns 'Officer John' as verifier...
[CLAIM] Verification started by Officer John for claim CLM-0001

[ACTION] Verifier 'Officer John' approves claim after document verification...
[CLAIM] Verification submitted for CLM-0001 - Decision: APPROVED
[CLAIM] APPROVED: CLM-0001 - Item ownership transferred to Security

==========================================================
  ✅ CLAIM APPROVED
==========================================================
  Status: Closed
  Ownership transferred to: Security
  ✓ Case closed
==========================================================
```

---

## Key Classes to Know

### Models
- **Claim**: Core entity with state machine logic
- **Verification**: Immutable record of verifier decision
- **ClaimStatus**: Enum of possible states

### Services
- **ClaimService**: Main business logic (9 public methods)
- **ClaimRepository**: Abstraction for persistence
- **InMemoryClaimRepository**: MVP implementation

### Validators
- **ClaimValidator**: Business rule validation
- **ItemStatusValidator**: Status transition validation

### Observers
- **ClaimCreationObserver**: Auto-creates claims on match

### Controllers/Views
- **ClaimController**: MVC controller (7 handlers)
- **ClaimView**: Console display (10+ display methods)

---

## Integration with Existing Code

### With Person 1 (Item Model)
- Uses `Item.updateStatus()` to transition states
- Uses `Item.setOwnerName()` / `Item.setContactInfo()` for ownership transfer
- Added `Item.getOwnerName()` / `Item.getContactInfo()` getters

### With Person 2 (Search/Matching)
- Listens to `MatchObserver` interface
- Receives `MatchResult` objects on match detection
- Triggered automatically via observer pattern

### Extension Point for Person 4 (Notifications)
- Register observers via `SearchService.addObserver()`
- Listen to `MatchObserver.onMatchesFound()` events
- Can also create `ClaimObserver` for claim status changes

---

## State Machine Diagram

```
     MATCHED
        ↓
   CLAIM_REQUESTED
        ↓
  UNDER_VERIFICATION
      ╱   ╲
     ╱     ╲
    ↓       ↓
APPROVED  REJECTED
    ↓
 CLOSED
(terminal)
```

---

## Next Steps for Person 4 (Notification System)

1. Implement `MatchObserver` for match notifications
   ```java
   public class NotificationObserver implements MatchObserver {
       @Override
       public void onMatchesFound(List<MatchResult> matches) {
           // Send SMS/email notifications
       }
   }
   ```

2. Register with SearchService
   ```java
   searchService.addObserver(new NotificationObserver(...));
   ```

3. Optional: Create `ClaimObserver` for claim status changes
   ```java
   public interface ClaimObserver {
       void onClaimStatusChanged(Claim claim);
   }
   ```

---
