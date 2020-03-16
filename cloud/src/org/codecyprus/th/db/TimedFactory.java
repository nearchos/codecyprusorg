package org.codecyprus.th.db;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import org.codecyprus.th.model.Timed;

import java.util.ArrayList;
import java.util.logging.Logger;

public class TimedFactory {

    public static final Logger log = Logger.getLogger("codecyprus-th");

    private static final String KIND = "Timed";

    public static final String PROPERTY_UUID = "uuid";
    public static final String PROPERTY_TREASURE_HUNT_ID = "t-th-id";
    public static final String PROPERTY_TITLE_TEXT = "t-th-tt";
    public static final String PROPERTY_BODY_TEXT = "t-th-bt";

    private static final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    static public ArrayList<Timed> getTimedsForTreasureHunt(final String treasureHuntId) {
        final ArrayList<Timed> timeds = new ArrayList<>();
        final Query.Filter filterTimed = new Query.FilterPredicate(
                PROPERTY_TREASURE_HUNT_ID,
                Query.FilterOperator.EQUAL,
                treasureHuntId);
        final Query query = new Query(KIND).setFilter(filterTimed);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        for(final Entity entity : preparedQuery.asIterable()) {
            timeds.add(getFromEntity(entity));
        }
        return timeds;
    }

    static public ArrayList<Timed> getAllTimed() {
        final ArrayList<Timed> timeds = new ArrayList<>();
        final Query query = new Query(KIND).addSort(PROPERTY_TREASURE_HUNT_ID);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        for(final Entity entity : preparedQuery.asIterable()) {
            timeds.add(getFromEntity(entity));
        }
        return timeds;
    }

    static public Timed getTimed(final String uuid) {
        try {
            final Entity entity = datastoreService.get(KeyFactory.stringToKey(uuid));
            return getFromEntity(entity);
        } catch (EntityNotFoundException | IllegalArgumentException  e) {
            log.severe("Could not find entity for " + KIND + " with uuid: " + uuid);
            return null;
        }
    }

    static public Key addTimed(final Timed timed) {
        final Entity configuredQuestionEntity = new Entity(KIND);
        configuredQuestionEntity.setProperty(PROPERTY_TREASURE_HUNT_ID, timed.getTreasureHuntUuid());
        configuredQuestionEntity.setProperty(PROPERTY_TITLE_TEXT, timed.getTitleText());
        configuredQuestionEntity.setProperty(PROPERTY_BODY_TEXT, timed.getBodyText());

        return datastoreService.put(configuredQuestionEntity);
    }

    static public void editTimed(final Timed timed) {
        final String uuid = timed.getUuid();
        try {
            final Entity timedEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            timedEntity.setProperty(PROPERTY_TREASURE_HUNT_ID, timed.getTreasureHuntUuid());
            timedEntity.setProperty(PROPERTY_TITLE_TEXT, timed.getTitleText());
            timedEntity.setProperty(PROPERTY_BODY_TEXT, timed.getBodyText());
            datastoreService.put(timedEntity);

            MemcacheServiceFactory.getMemcacheService().delete(uuid); // invalidate cached entry
        } catch (EntityNotFoundException enfe) {
            log.severe("Could not find " + KIND + " with key: " + uuid);
        }
    }

    static private Timed getFromEntity(final Entity entity) {
        return new Timed(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_TREASURE_HUNT_ID),
                (String) entity.getProperty(PROPERTY_TITLE_TEXT),
                (String) entity.getProperty(PROPERTY_BODY_TEXT)
        );
    }
}