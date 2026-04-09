package service;

import model.MatchResult;
import java.util.List;

/**
 * Observer Pattern — anyone interested in "matches found" events
 * implements this interface.
 *
 * Person 4 (Notification System) will register a concrete observer here
 * without needing to touch SearchService.
 */
public interface MatchObserver {
    void onMatchesFound(List<MatchResult> matches);
}