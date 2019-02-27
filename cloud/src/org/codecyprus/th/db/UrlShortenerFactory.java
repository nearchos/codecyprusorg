package org.codecyprus.th.db;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import org.codecyprus.th.model.UrlShortener;

import java.util.Vector;
import java.util.logging.Logger;

public class UrlShortenerFactory {

    public static final Logger log = Logger.getLogger("codecyprus-th");

    public static final String KIND = "UrlShortener";

    public static final String PROPERTY_UUID = "uuid";
    public static final String PROPERTY_KEY = "urls-key";
    public static final String PROPERTY_TARGET = "urls-target";

    private static final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
    private static final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    static private String getCacheKey(final String urlShortenerKey) {
        return "urls-" + urlShortenerKey;
    }

    static public UrlShortener getUrlShortenerByKey(final String keyAsString) {
        if(memcacheService.contains(getCacheKey(keyAsString))) {
            return (UrlShortener) memcacheService.get(getCacheKey(keyAsString));
        } else {
            try {
                final Query query = new Query(KIND);
                final Query.Filter filterCode = new Query.FilterPredicate(
                        PROPERTY_KEY,
                        Query.FilterOperator.EQUAL,
                        keyAsString);
                query.setFilter(filterCode);
                final PreparedQuery preparedQuery = datastoreService.prepare(query);
                final Entity urlShortenerEntity = preparedQuery.asSingleEntity();
                final UrlShortener urlShortener = getFromEntity(urlShortenerEntity);
                memcacheService.put(getCacheKey(keyAsString), urlShortener); // add cached entry
                return urlShortener;
            } catch (IllegalArgumentException e) {
                log.warning("Could not find " + KIND + " with key: " + keyAsString);
                return null;
            }
        }
    }

    static public Vector<UrlShortener> getAllUrlShorteners() {
        final Query query = new Query(KIND).addSort(PROPERTY_KEY);

        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<UrlShortener> urlShorteners = new Vector<>();
        for(final Entity entity : preparedQuery.asIterable()) {
            urlShorteners.add(getFromEntity(entity));
        }

        return urlShorteners;
    }

    static public Key addUrlShortener(UrlShortener urlShortener) {
        final Entity urlShortenerEntity = new Entity(KIND);
        urlShortenerEntity.setProperty(PROPERTY_KEY, urlShortener.getKey());
        urlShortenerEntity.setProperty(PROPERTY_TARGET, urlShortener.getTarget());

        memcacheService.put(getCacheKey(urlShortener.getKey()), urlShortener);
        return datastoreService.put(urlShortenerEntity);
    }

    static public void editUrlShortener(final UrlShortener urlShortener) {
        final String uuid = urlShortener.getUuid();
        try {
            final Entity urlShortenerEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            urlShortenerEntity.setProperty(PROPERTY_KEY, urlShortener.getKey());
            urlShortenerEntity.setProperty(PROPERTY_TARGET, urlShortener.getTarget());
            datastoreService.put(urlShortenerEntity);

            memcacheService.put(getCacheKey(urlShortener.getKey()), urlShortener);
        } catch (EntityNotFoundException enfe) {
            log.severe("Could not find " + KIND + " with key: " + uuid);
        }
    }

    static private UrlShortener getFromEntity(final Entity entity) {
        return new UrlShortener(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_KEY),
                (String) entity.getProperty(PROPERTY_TARGET)
        );
    }
}