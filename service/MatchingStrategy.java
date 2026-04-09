package service;

import model.Item;
import model.MatchResult;

import java.util.List;

/**
 * Strategy Pattern — defines the contract for any matching algorithm.
 *
 * New matching strategies (e.g., ML-based, image-based) can be added later
 * without changing the SearchService — Open/Closed Principle.
 */
public interface MatchingStrategy {

    /**
     * Given a list of lost items and a list of found items,
     * return all detected matches with confidence scores.
     */
    List<MatchResult> findMatches(List<Item> lostItems, List<Item> foundItems);
}