package org.codecyprus.th.db;

import com.google.appengine.api.datastore.*;
import org.codecyprus.th.model.User;

import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

public class UserFactory {

    public static final Logger log = Logger.getLogger("codecyprus-th");

    public static final String KIND = "User";

    public static final String PROPERTY_UUID = "uuid";
    public static final String PROPERTY_EMAIL = "u-email";
    public static final String PROPERTY_NICKNAME = "u-nickname";
    public static final String PROPERTY_IS_ADMIN = "u-is-admin";

    private static final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    static public User getUserByEmail(final String email) {
        final Query query = new Query(KIND);
        query.setFilter(new Query.FilterPredicate(PROPERTY_EMAIL, Query.FilterOperator.EQUAL, email));
        final Entity userEntity = datastoreService.prepare(query).asSingleEntity();
        return userEntity == null ? null : getFromEntity(userEntity);
    }

    static public Vector<User> getAllUsers() {
        final Query query = new Query(KIND);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<User> users = new Vector<>();
        for(final Entity entity : preparedQuery.asIterable()) {
            users.add(getFromEntity(entity));
        }
        return users;
   }

    static public Key addUser(User user) {
        final Entity userEntity = new Entity(KIND);
        userEntity.setProperty(PROPERTY_EMAIL, user.getEmail());
        userEntity.setProperty(PROPERTY_NICKNAME, user.getNickname());
        userEntity.setProperty(PROPERTY_IS_ADMIN, user.isAdmin());
        return datastoreService.put(userEntity);
    }

    static public User getFromEntity(final Entity entity) {
        return new User(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_EMAIL),
                (String) entity.getProperty(PROPERTY_NICKNAME),
                (Boolean) entity.getProperty(PROPERTY_IS_ADMIN)
        );
    }
}