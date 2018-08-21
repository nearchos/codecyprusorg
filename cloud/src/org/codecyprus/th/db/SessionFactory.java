package org.codecyprus.th.db;

import com.google.appengine.api.datastore.*;
import org.codecyprus.th.model.Session;

import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

public class SessionFactory {

    public static final Logger log = Logger.getLogger("codecyprus-th");

    public static final String KIND = "Session";

    public static final String PROPERTY_UUID = "uuid";
    public static final String PROPERTY_TREASURE_HUNT_ID = "s-th-id";
    public static final String PROPERTY_PLAYER_NAME = "s-player-name";
    public static final String PROPERTY_APP_NAME = "s-app-name";
    public static final String PROPERTY_START_TIME = "s-start-time";
    public static final String PROPERTY_END_TIME = "s-end-time";
    public static final String PROPERTY_SCORE = "s-score";
    public static final String PROPERTY_COMPLETION_TIME = "s-completion-time";
    public static final String PROPERTY_CONFIGURED_QUESTION_IDS = "s-cq-ids";
    public static final String PROPERTY_CURRENT_CONFIGURED_QUESTION_INDEX = "s-current-cq-index";

    private static final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    static public Session getSession(final String keyAsString) {
        try {
            final Entity sessionEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));
            return getFromEntity(sessionEntity);
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            log.warning("Could not find " + KIND + " with key: " + keyAsString);
            return null;
        }
    }

    static public Vector<Session> getAllSessions() {
        final Query query = new Query(KIND).addSort(PROPERTY_START_TIME);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<Session> sessions = new Vector<>();
        for(final Entity entity : preparedQuery.asIterable()) {
            sessions.add(getFromEntity(entity));
        }
        return sessions;
    }

    static public Vector<Session> getSessionsByTreasureHuntId(final String treasureHuntId) {
        final Query query = new Query(KIND).addSort(PROPERTY_TREASURE_HUNT_ID).addSort(PROPERTY_START_TIME);
        query.setFilter(new Query.FilterPredicate(PROPERTY_TREASURE_HUNT_ID, Query.FilterOperator.EQUAL, treasureHuntId));
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<Session> sessions = new Vector<>();
        for(final Entity entity : preparedQuery.asIterable()) {
            sessions.add(getFromEntity(entity));
        }
        return sessions;
    }

    /**
     * Returns the {@link Key} of the newly created {@link Session} entity, or <code>null</code> if a session with the
     * same {@link #PROPERTY_APP_NAME} and {@link #PROPERTY_TREASURE_HUNT_ID} already exist.
     * The action takes place in a transaction to ensure that at any moment, at most one session can be added with the
     * specified {@link Session} properties.
     *
     * @param session
     * @return
     * @throws IllegalArgumentException
     */
    static public Key synchronouslyAddSession(final Session session) throws IllegalArgumentException {

        // transaction used to check if the playerName is already used
        final Transaction transaction = datastoreService.beginTransaction();

        try {
            // first check if a session exists for this playerName/categoryUUID
            final Query.Filter filterPlayerName = new Query.FilterPredicate(
                    PROPERTY_PLAYER_NAME, Query.FilterOperator.EQUAL, session.getPlayerName());
            final Query.Filter filterCategoryUUID = new Query.FilterPredicate(
                    PROPERTY_TREASURE_HUNT_ID, Query.FilterOperator.EQUAL, session.getTreasureHuntUuid());

            final Query query = new Query(KIND);
            final Query.Filter compositeFilter = Query.CompositeFilterOperator.and(filterPlayerName, filterCategoryUUID);
            query.setFilter(compositeFilter);

            final PreparedQuery preparedQuery = datastoreService.prepare(query);
            final Entity entity = preparedQuery.asSingleEntity();

            // there should be exactly 0 or 1 sessions available
            if(entity == null) { // no existing session with given playerName/categoryUUID; create new session
                final Key key = addSession(session);
                transaction.commit();
                return key;
            } else { // there must be at most 1 session available
                transaction.rollback();
                return null;
            }
        } finally {
            if(transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    static private Key addSession(final Session session) {
        final Entity sessionEntity = new Entity(KIND);
        sessionEntity.setProperty(PROPERTY_TREASURE_HUNT_ID, session.getTreasureHuntUuid());
        sessionEntity.setProperty(PROPERTY_PLAYER_NAME, session.getPlayerName());
        sessionEntity.setProperty(PROPERTY_APP_NAME, session.getAppName());
        sessionEntity.setProperty(PROPERTY_START_TIME, session.getStartTime());
        sessionEntity.setProperty(PROPERTY_END_TIME, session.getEndTime());
        sessionEntity.setProperty(PROPERTY_SCORE, session.getScore());
        sessionEntity.setProperty(PROPERTY_COMPLETION_TIME, session.getCompletionTime());
        sessionEntity.setProperty(PROPERTY_CONFIGURED_QUESTION_IDS, session.getConfiguredQuestionUuids());
        sessionEntity.setProperty(PROPERTY_CURRENT_CONFIGURED_QUESTION_INDEX, session.getCurrentConfiguredQuestionIndex());

        return datastoreService.put(sessionEntity);
    }

    static public void editSession(final Session session) {
        final String uuid = session.getUuid();
        try {
            final Entity sessionEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            sessionEntity.setProperty(PROPERTY_TREASURE_HUNT_ID, session.getTreasureHuntUuid());
            sessionEntity.setProperty(PROPERTY_PLAYER_NAME, session.getPlayerName());
            sessionEntity.setProperty(PROPERTY_APP_NAME, session.getAppName());
            sessionEntity.setProperty(PROPERTY_START_TIME, session.getStartTime());
            sessionEntity.setProperty(PROPERTY_END_TIME, session.getEndTime());
            sessionEntity.setProperty(PROPERTY_SCORE, session.getScore());
            sessionEntity.setProperty(PROPERTY_COMPLETION_TIME, session.getCompletionTime());
            sessionEntity.setProperty(PROPERTY_CONFIGURED_QUESTION_IDS, session.getConfiguredQuestionUuids());
            sessionEntity.setProperty(PROPERTY_CURRENT_CONFIGURED_QUESTION_INDEX, session.getCurrentConfiguredQuestionIndex());
            datastoreService.put(sessionEntity);
        } catch (EntityNotFoundException enfe) {
            log.severe("Could not find " + KIND + " with key: " + uuid);
        }
    }

    static public void updateSession(final Session session, final int scoreAdjustment) {
        final String uuid = session.getUuid();
        try {
            final Entity sessionEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            sessionEntity.setProperty(PROPERTY_SCORE, session.getScore() + scoreAdjustment);
            datastoreService.put(sessionEntity);
        } catch (EntityNotFoundException enfe) {
            log.severe("Could not find " + KIND + " with key: " + uuid);
        }
    }

    /**
     *
     * @param session
     * @param scoreAdjustment
     * @return true iff the session is now completed after this advancement (does not check if it has ended time-wise)
     */
    static public boolean updateSessionAndAdvance(final Session session, final int scoreAdjustment) {
        final String uuid = session.getUuid();
        try {
            final long nextConfiguredQuestionIndex = session.getCurrentConfiguredQuestionIndex() + 1;
            // completed if nextConfiguredQuestionIndex >= session.getConfiguredQuestionUuids().size();
            final boolean completed = nextConfiguredQuestionIndex >= session.getConfiguredQuestionUuids().size();
            final Entity sessionEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            sessionEntity.setProperty(PROPERTY_SCORE, session.getScore() + scoreAdjustment);
            sessionEntity.setProperty(PROPERTY_CURRENT_CONFIGURED_QUESTION_INDEX, nextConfiguredQuestionIndex);
            if(completed) {
                sessionEntity.setProperty(PROPERTY_COMPLETION_TIME, System.currentTimeMillis());
            }
            datastoreService.put(sessionEntity);

            return completed;
        } catch (EntityNotFoundException enfe) {
            log.severe("Could not find " + KIND + " with key: " + uuid);
            return false;
        }
    }

    @SuppressWarnings("unchecked") // Cast can't verify generic type (used for ArrayList<>)
    static public Session getFromEntity(final Entity entity) {
        return new Session(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_TREASURE_HUNT_ID),
                (String) entity.getProperty(PROPERTY_PLAYER_NAME),
                (String) entity.getProperty(PROPERTY_APP_NAME),
                (Long) entity.getProperty(PROPERTY_START_TIME),
                (Long) entity.getProperty(PROPERTY_END_TIME),
                (Long) entity.getProperty(PROPERTY_SCORE),
                (Long) entity.getProperty(PROPERTY_COMPLETION_TIME),
                (ArrayList<String>) entity.getProperty(PROPERTY_CONFIGURED_QUESTION_IDS),
                (Long) entity.getProperty(PROPERTY_CURRENT_CONFIGURED_QUESTION_INDEX)
        );
    }
}