package org.codecyprus.th.db;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import org.codecyprus.th.model.TreasureHunt;
import org.codecyprus.th.model.Visibility;

import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

public class TreasureHuntFactory {

    public static final Logger log = Logger.getLogger("codecyprus-th");

    public static final String KIND = "TreasureHunt";

    public static final String PROPERTY_UUID = "uuid";
    public static final String PROPERTY_NAME = "th-name";
    public static final String PROPERTY_DESCRIPTION = "th-description";
    public static final String PROPERTY_OWNER_EMAIL = "th-owner-email";
    public static final String PROPERTY_SECRET_CODE = "th-secret-code";
    public static final String PROPERTY_SALT = "th-salt";
    public static final String PROPERTY_VISIBILITY = "th-visibility";
    public static final String PROPERTY_STARTS_ON = "th-starts-on";
    public static final String PROPERTY_ENDS_ON = "th-ends-on";
    public static final String PROPERTY_MAX_DURATION = "th-max-duration";
    public static final String PROPERTY_SHUFFLED = "th-shuffled";
    public static final String PROPERTY_REQUIRES_AUTHENTICATION = "th-requires-authentication";
    public static final String PROPERTY_HAS_PRIZE = "th-has-prize";
    public static final String PROPERTY_EMAIL_RESULTS = "th-email-results";

    private static final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
    private static final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    static public TreasureHunt getTreasureHunt(final String keyAsString) {
        if(memcacheService.contains(keyAsString)) {
            return (TreasureHunt) memcacheService.get(keyAsString);
        } else {
            try {
                final Entity treasureHuntEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));
                final TreasureHunt treasureHunt = getFromEntity(treasureHuntEntity);
                memcacheService.put(keyAsString, treasureHunt); // add cached entry
                return treasureHunt;
            } catch (EntityNotFoundException | IllegalArgumentException e) {
                log.warning("Could not find " + KIND + " with key: " + keyAsString);
                return null;
            }
        }
    }

    static public Vector<TreasureHunt> getAllTreasureHunts() {
        return getAllTreasureHunts(true);
    }

    static public Vector<TreasureHunt> getAllTreasureHunts(final boolean includeFinished) {
        return getTreasureHunts(true, includeFinished);
    }

    static public Vector<TreasureHunt> getPublicTreasureHunts(final boolean includeFinished) {
        return getTreasureHunts(false, includeFinished);
    }

    static private Vector<TreasureHunt> getTreasureHunts(final boolean includeNonPublic, final boolean includeFinished) {
        final Query query = new Query(KIND).addSort(PROPERTY_ENDS_ON).addSort(PROPERTY_VISIBILITY).addSort(PROPERTY_STARTS_ON);

        final ArrayList<Query.Filter> selectedFilters = new ArrayList<>();

        final Query.Filter selectPublicOnlyFilter = new Query.FilterPredicate(PROPERTY_VISIBILITY, Query.FilterOperator.EQUAL, Visibility.PUBLIC.name());
        final long now = System.currentTimeMillis();
        if(!includeNonPublic) selectedFilters.add(selectPublicOnlyFilter);

        final Query.Filter selectUnfinishedOnlyFilter = new Query.FilterPredicate(PROPERTY_ENDS_ON, Query.FilterOperator.GREATER_THAN, now);
        if(!includeFinished) selectedFilters.add(selectUnfinishedOnlyFilter);
        if(selectedFilters.isEmpty()) { // no filters set
            // empty
        } else if(selectedFilters.size() == 1) { // just 1 filter
                query.setFilter(selectedFilters.get(0));
        } else { // 2 or more filters
            query.setFilter(new Query.CompositeFilter(Query.CompositeFilterOperator.AND, selectedFilters));
        }

        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<TreasureHunt> treasureHunts = new Vector<>();
        for(final Entity entity : preparedQuery.asIterable()) {
            treasureHunts.add(getFromEntity(entity));
        }

        return treasureHunts;
    }

    static public Key addTreasureHunt(TreasureHunt treasureHunt) {
        final Entity treasureHuntEntity = new Entity(KIND);
        treasureHuntEntity.setProperty(PROPERTY_NAME, treasureHunt.getName());
        treasureHuntEntity.setProperty(PROPERTY_DESCRIPTION, treasureHunt.getDescription());
        treasureHuntEntity.setProperty(PROPERTY_OWNER_EMAIL, treasureHunt.getOwnerEmail());
        treasureHuntEntity.setProperty(PROPERTY_SECRET_CODE, treasureHunt.getSecretCode());
        treasureHuntEntity.setProperty(PROPERTY_SALT, treasureHunt.getSalt());
        treasureHuntEntity.setProperty(PROPERTY_VISIBILITY, treasureHunt.getVisibility().name());
        treasureHuntEntity.setProperty(PROPERTY_STARTS_ON, treasureHunt.getStartsOn());
        treasureHuntEntity.setProperty(PROPERTY_ENDS_ON, treasureHunt.getEndsOn());
        treasureHuntEntity.setProperty(PROPERTY_MAX_DURATION, treasureHunt.getMaxDuration());
        treasureHuntEntity.setProperty(PROPERTY_MAX_DURATION, treasureHunt.getMaxDuration());
        treasureHuntEntity.setProperty(PROPERTY_SHUFFLED, treasureHunt.isShuffled());
        treasureHuntEntity.setProperty(PROPERTY_REQUIRES_AUTHENTICATION, treasureHunt.isRequiresAuthentication());
        treasureHuntEntity.setProperty(PROPERTY_EMAIL_RESULTS, treasureHunt.isEmailResults());
        treasureHuntEntity.setProperty(PROPERTY_HAS_PRIZE, treasureHunt.isHasPrize());

        return datastoreService.put(treasureHuntEntity);
    }

    static public void editTreasureHunt(final TreasureHunt treasureHunt) {
        final String uuid = treasureHunt.getUuid();
        try {
            final Entity treasureHuntEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            treasureHuntEntity.setProperty(PROPERTY_NAME, treasureHunt.getName());
            treasureHuntEntity.setProperty(PROPERTY_DESCRIPTION, treasureHunt.getDescription());
            treasureHuntEntity.setProperty(PROPERTY_OWNER_EMAIL, treasureHunt.getOwnerEmail());
            treasureHuntEntity.setProperty(PROPERTY_SECRET_CODE, treasureHunt.getSecretCode());
            treasureHuntEntity.setProperty(PROPERTY_SALT, treasureHunt.getSalt());
            treasureHuntEntity.setProperty(PROPERTY_VISIBILITY, treasureHunt.getVisibility().name());
            treasureHuntEntity.setProperty(PROPERTY_STARTS_ON, treasureHunt.getStartsOn());
            treasureHuntEntity.setProperty(PROPERTY_ENDS_ON, treasureHunt.getEndsOn());
            treasureHuntEntity.setProperty(PROPERTY_MAX_DURATION, treasureHunt.getMaxDuration());
            treasureHuntEntity.setProperty(PROPERTY_MAX_DURATION, treasureHunt.getMaxDuration());
            treasureHuntEntity.setProperty(PROPERTY_SHUFFLED, treasureHunt.isShuffled());
            treasureHuntEntity.setProperty(PROPERTY_REQUIRES_AUTHENTICATION, treasureHunt.isRequiresAuthentication());
            treasureHuntEntity.setProperty(PROPERTY_EMAIL_RESULTS, treasureHunt.isEmailResults());
            treasureHuntEntity.setProperty(PROPERTY_HAS_PRIZE, treasureHunt.isHasPrize());
            datastoreService.put(treasureHuntEntity);

            MemcacheServiceFactory.getMemcacheService().delete(uuid); // invalidate cached entry
        } catch (EntityNotFoundException enfe) {
            log.severe("Could not find " + KIND + " with key: " + uuid);
        }
    }

    static public TreasureHunt getFromEntity(final Entity entity) {
        return new TreasureHunt(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_NAME),
                (String) entity.getProperty(PROPERTY_DESCRIPTION),
                (String) entity.getProperty(PROPERTY_OWNER_EMAIL),
                (String) (entity.getProperty(PROPERTY_SECRET_CODE) == null ? "" : entity.getProperty(PROPERTY_SECRET_CODE)),
                (String) (entity.getProperty(PROPERTY_SALT) == null ? "undefined" : entity.getProperty(PROPERTY_SALT)),
                Visibility.valueOf((String) entity.getProperty(PROPERTY_VISIBILITY)),
                (Long) entity.getProperty(PROPERTY_STARTS_ON),
                (Long) entity.getProperty(PROPERTY_ENDS_ON),
                (Long) entity.getProperty(PROPERTY_MAX_DURATION),
                (Boolean) entity.getProperty(PROPERTY_SHUFFLED),
                (Boolean) entity.getProperty(PROPERTY_REQUIRES_AUTHENTICATION),
                (Boolean) entity.getProperty(PROPERTY_EMAIL_RESULTS),
                entity.getProperty(PROPERTY_HAS_PRIZE) != null ? (Boolean) entity.getProperty(PROPERTY_HAS_PRIZE) : false
        );
    }
}