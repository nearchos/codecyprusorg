package org.codecyprus.th.db;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.codecyprus.th.model.Question;
import org.codecyprus.th.model.QuestionType;

import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Logger;

public class QuestionFactory {

    public static final Logger log = Logger.getLogger("codecyprus-th");

    public static final String KIND = "Question";

    public static final String PROPERTY_UUID = "uuid";
    public static final String PROPERTY_QUESTION_TEXT = "q-question-text";
    public static final String PROPERTY_QUESTION_TYPE = "q-question-type";
    public static final String PROPERTY_CORRECT_ANSWER = "q-correct-answer";
    public static final String PROPERTY_CREATOR_EMAIL = "q-creator-email";
    public static final String PROPERTY_CREATION_TIMESTAMP = "q-creation-timestamp";
    public static final String PROPERTY_SHARED = "q-shared";

    private static final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
    private static final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    static public Question getQuestion(final String keyAsString) {
        try {
            final Entity questionEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));
            final Question question = getFromEntity(questionEntity);
            return question;
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            log.warning("Could not find " + KIND + " with key: " + keyAsString);
            return null;
        }
    }

    static public Vector<Question> getQuestions(final boolean showAlsoShared) {
        final Query query = new Query(KIND).addSort(PROPERTY_CREATION_TIMESTAMP);
        final UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();
        final String userEmail = user == null ? "Unknown" : user.getEmail();
        final Query.Filter filterUser = new Query.FilterPredicate(PROPERTY_CREATOR_EMAIL, Query.FilterOperator.EQUAL, userEmail);
        final Query.Filter filterShared = new Query.FilterPredicate(PROPERTY_SHARED, Query.FilterOperator.EQUAL, true);
        if(showAlsoShared) { // if selected, add filter to also return entities set as 'shared' by others
            query.setFilter(new Query.CompositeFilter(Query.CompositeFilterOperator.OR, Arrays.asList(
                    filterUser, // filter for selecting user questions
                    filterShared // filter for selecting shared questions
            )));
        } else { // otherwise select only user questions
            query.setFilter(filterUser);
        }
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<Question> questions = new Vector<>();
        for(final Entity entity : preparedQuery.asIterable()) {
            questions.add(getFromEntity(entity));
        }

        return questions;
    }

    static public Key addQuestion(Question question) {
        final Entity questionEntity = new Entity(KIND);
        questionEntity.setProperty(PROPERTY_QUESTION_TEXT, question.getQuestionText());
        questionEntity.setProperty(PROPERTY_QUESTION_TYPE, question.getQuestionType().name());
        questionEntity.setProperty(PROPERTY_CORRECT_ANSWER, question.getCorrectAnswer());
        questionEntity.setProperty(PROPERTY_CREATOR_EMAIL, question.getCreatorEmail());
        questionEntity.setProperty(PROPERTY_CREATION_TIMESTAMP, question.getCreationTimestamp());
        questionEntity.setProperty(PROPERTY_SHARED, question.isShared());

        return datastoreService.put(questionEntity);
    }

    static public void editQuestion(final Question question) {
        final String uuid = question.getUuid();
        try {
            final Entity questionEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            questionEntity.setProperty(PROPERTY_QUESTION_TEXT, question.getQuestionText());
            questionEntity.setProperty(PROPERTY_QUESTION_TYPE, question.getQuestionType().name());
            questionEntity.setProperty(PROPERTY_CORRECT_ANSWER, question.getCorrectAnswer());
            questionEntity.setProperty(PROPERTY_CREATOR_EMAIL, question.getCreatorEmail());
            questionEntity.setProperty(PROPERTY_CREATION_TIMESTAMP, question.getCreationTimestamp());
            questionEntity.setProperty(PROPERTY_SHARED, question.isShared());
            datastoreService.put(questionEntity);

            MemcacheServiceFactory.getMemcacheService().delete(uuid); // invalidate cached entry
        } catch (EntityNotFoundException enfe) {
            log.severe("Could not find " + KIND + " with key: " + uuid);
        }
    }

    static public Question getFromEntity(final Entity entity) {
        return new Question(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_QUESTION_TEXT),
                QuestionType.valueOf((String) entity.getProperty(PROPERTY_QUESTION_TYPE)),
                (String) entity.getProperty(PROPERTY_CORRECT_ANSWER),
                (String) entity.getProperty(PROPERTY_CREATOR_EMAIL),
                (Long) entity.getProperty(PROPERTY_CREATION_TIMESTAMP),
                (Boolean) entity.getProperty(PROPERTY_SHARED)
        );
    }
}