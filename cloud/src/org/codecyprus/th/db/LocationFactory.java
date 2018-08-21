package org.codecyprus.th.db;

import com.google.appengine.api.datastore.*;
import org.codecyprus.th.model.Location;

import java.util.List;
import java.util.logging.Logger;

public class LocationFactory {

    public static final Logger log = Logger.getLogger("codecyprus-th");

    public static final String KIND = "Location";

    public static final String PROPERTY_UUID = "uuid";
    public static final String PROPERTY_SESSION_UUID = "l-session-uuid";
    public static final String PROPERTY_TIMESTAMP = "l-timestamp";
    public static final String PROPERTY_LATITUDE = "l-latitude";
    public static final String PROPERTY_LONGITUDE = "l-longitude";

    private static final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    static public Location getLocation(final String keyAsString) {
        try {
            final Entity locationEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));
            final Location location = getFromEntity(locationEntity);
            return location;
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            log.warning("Could not find " + KIND + " with key: " + keyAsString);
            return null;
        }
    }

    static public Location getLatestLocation(final String sessionUuid) {
        final Query query = new Query(KIND).addSort(PROPERTY_TIMESTAMP, Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final List<Entity> entities = preparedQuery.asList(FetchOptions.Builder.withLimit(1));
        return entities.isEmpty() ? null : getFromEntity(entities.get(0));
    }

    static public Key addLocation(Location location) {
        final Entity questionEntity = new Entity(KIND);
        questionEntity.setProperty(PROPERTY_SESSION_UUID, location.getSessionUuid());
        questionEntity.setProperty(PROPERTY_TIMESTAMP, location.getTimestamp());
        questionEntity.setProperty(PROPERTY_LATITUDE, location.getLatitude());
        questionEntity.setProperty(PROPERTY_LONGITUDE, location.getLongitude());

        return datastoreService.put(questionEntity);
    }

    static public Location getFromEntity(final Entity entity) {
        return new Location(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_SESSION_UUID),
                (Long) entity.getProperty(PROPERTY_TIMESTAMP),
                (Double) entity.getProperty(PROPERTY_LATITUDE),
                (Double) entity.getProperty(PROPERTY_LONGITUDE)
        );
    }
}
