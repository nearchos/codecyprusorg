package org.codecyprus.th.db;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import org.codecyprus.th.model.ConfiguredQuestion;

import java.util.ArrayList;
import java.util.logging.Logger;

public class ConfiguredQuestionFactory {

    public static final Logger log = Logger.getLogger("codecyprus-th");

    public static final String KIND = "ConfiguredQuestion";

    public static final String PROPERTY_UUID = "uuid";
    public static final String PROPERTY_TREASURE_HUNT_ID = "cq-th-id";
    public static final String PROPERTY_QUESTION_ID = "cq-q-id";
    public static final String PROPERTY_SEQ_NUMBER = "cq-q-seq-number";
    public static final String PROPERTY_CORRECT_SCORE = "cq-q-correct-score";
    public static final String PROPERTY_WRONG_SCORE = "cq-q-wrong-score";
    public static final String PROPERTY_SKIP_SCORE = "cq-q-skip-score";
    public static final String PROPERTY_CAN_BE_SKIPPED = "cq-q-can-be-skipped";
    public static final String PROPERTY_LATITUDE = "cq-q-latitude";
    public static final String PROPERTY_LONGITUDE = "cq-q-longitude";
    public static final String PROPERTY_DISTANCE_THRESHOLD = "cq-q-distance-threshold";

    private static final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    static public ArrayList<ConfiguredQuestion> getConfiguredQuestionsForTreasureHunt(final String treasureHuntId) {
        final ArrayList<ConfiguredQuestion> configuredQuestions = new ArrayList<>();
        final Query.Filter filterConfiguredQuestions = new Query.FilterPredicate(
                PROPERTY_TREASURE_HUNT_ID,
                Query.FilterOperator.EQUAL,
                treasureHuntId);
        final Query query = new Query(KIND).setFilter(filterConfiguredQuestions).addSort(PROPERTY_SEQ_NUMBER);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        for(final Entity entity : preparedQuery.asIterable()) {
            configuredQuestions.add(getFromEntity(entity));
        }
        return configuredQuestions;
    }

    static public int getNumOfConfiguredQuestionsForTreasureHunt(final String treasureHuntId) {
        final Query.Filter filterConfiguredQuestions = new Query.FilterPredicate(
                PROPERTY_TREASURE_HUNT_ID,
                Query.FilterOperator.EQUAL,
                treasureHuntId);
        final Query query = new Query(KIND).setFilter(filterConfiguredQuestions).addSort(PROPERTY_SEQ_NUMBER);
        return datastoreService.prepare(query).countEntities(FetchOptions.Builder.withDefaults());
    }

    static public ConfiguredQuestion getConfiguredQuestion(final String uuid) {
        try {
            final Entity entity = datastoreService.get(KeyFactory.stringToKey(uuid));
            return getFromEntity(entity);
        } catch (EntityNotFoundException enfe) {
            log.severe("Could not find entity for " + KIND + " with uuid: " + uuid);
            return null;
        }
    }

    static public Key addConfiguredQuestion(final ConfiguredQuestion configuredQuestion) {
        final Entity configuredQuestionEntity = new Entity(KIND);
        configuredQuestionEntity.setProperty(PROPERTY_TREASURE_HUNT_ID, configuredQuestion.getTreasureHuntUuid());
        configuredQuestionEntity.setProperty(PROPERTY_QUESTION_ID, configuredQuestion.getQuestionUuid());
        configuredQuestionEntity.setProperty(PROPERTY_SEQ_NUMBER, configuredQuestion.getSeqNumber());
        configuredQuestionEntity.setProperty(PROPERTY_CORRECT_SCORE, configuredQuestion.getCorrectScore());
        configuredQuestionEntity.setProperty(PROPERTY_WRONG_SCORE, configuredQuestion.getWrongScore());
        configuredQuestionEntity.setProperty(PROPERTY_SKIP_SCORE, configuredQuestion.getSkipScore());
        configuredQuestionEntity.setProperty(PROPERTY_CAN_BE_SKIPPED, configuredQuestion.isCanBeSkipped());
        configuredQuestionEntity.setProperty(PROPERTY_LATITUDE, configuredQuestion.getLatitude());
        configuredQuestionEntity.setProperty(PROPERTY_LONGITUDE, configuredQuestion.getLongitude());
        configuredQuestionEntity.setProperty(PROPERTY_DISTANCE_THRESHOLD, configuredQuestion.getDistanceThreshold());

        return datastoreService.put(configuredQuestionEntity);
    }

    static public void editConfiguredQuestion(final ConfiguredQuestion configuredQuestion) {
        final String uuid = configuredQuestion.getUuid();
        try {
            final Entity configuredQuestionEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            configuredQuestionEntity.setProperty(PROPERTY_TREASURE_HUNT_ID, configuredQuestion.getTreasureHuntUuid());
            configuredQuestionEntity.setProperty(PROPERTY_QUESTION_ID, configuredQuestion.getQuestionUuid());
            configuredQuestionEntity.setProperty(PROPERTY_SEQ_NUMBER, configuredQuestion.getSeqNumber());
            configuredQuestionEntity.setProperty(PROPERTY_CORRECT_SCORE, configuredQuestion.getCorrectScore());
            configuredQuestionEntity.setProperty(PROPERTY_WRONG_SCORE, configuredQuestion.getWrongScore());
            configuredQuestionEntity.setProperty(PROPERTY_SKIP_SCORE, configuredQuestion.getSkipScore());
            configuredQuestionEntity.setProperty(PROPERTY_CAN_BE_SKIPPED, configuredQuestion.isCanBeSkipped());
            configuredQuestionEntity.setProperty(PROPERTY_LATITUDE, configuredQuestion.getLatitude());
            configuredQuestionEntity.setProperty(PROPERTY_LONGITUDE, configuredQuestion.getLongitude());
            configuredQuestionEntity.setProperty(PROPERTY_DISTANCE_THRESHOLD, configuredQuestion.getDistanceThreshold());
            datastoreService.put(configuredQuestionEntity);

            MemcacheServiceFactory.getMemcacheService().delete(uuid); // invalidate cached entry
        } catch (EntityNotFoundException enfe) {
            log.severe("Could not find " + KIND + " with key: " + uuid);
        }
    }

    static public ConfiguredQuestion getFromEntity(final Entity entity) {
        return new ConfiguredQuestion(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_TREASURE_HUNT_ID),
                (String) entity.getProperty(PROPERTY_QUESTION_ID),
                (Long) entity.getProperty(PROPERTY_SEQ_NUMBER),
                (Long) entity.getProperty(PROPERTY_CORRECT_SCORE),
                (Long) entity.getProperty(PROPERTY_WRONG_SCORE),
                (Long) entity.getProperty(PROPERTY_SKIP_SCORE),
                (Boolean) entity.getProperty(PROPERTY_CAN_BE_SKIPPED),
                (Double) entity.getProperty(PROPERTY_LATITUDE),
                (Double) entity.getProperty(PROPERTY_LONGITUDE),
                (Double) entity.getProperty(PROPERTY_DISTANCE_THRESHOLD)
        );
    }
}