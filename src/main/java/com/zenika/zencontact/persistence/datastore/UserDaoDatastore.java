package com.zenika.zencontact.persistence.datastore;

import com.google.appengine.api.datastore.*;
import com.zenika.zencontact.domain.User;
import com.zenika.zencontact.persistence.UserDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ssinigag on 20/11/17.
 */
public class UserDaoDatastore implements UserDao {

    private static UserDaoDatastore INSTANCE = new UserDaoDatastore();

    public DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    public static UserDaoDatastore getInstance() {
        return INSTANCE;
    }

    @Override
    public long save(User contact) {
        Entity e = new Entity("User");
        if (contact.id != null) {
            Key k = KeyFactory.createKey("User", contact.id);
            try {
                e = datastore.get(k);
            } catch (EntityNotFoundException e1) {
                return 0L;
            }
        }
        e.setProperty("firstname", contact.firstName);
        e.setProperty("lastname", contact.lastName);
        e.setProperty("email", contact.email);
        if (contact.birthdate != null) {
            e.setProperty("birthdate", contact.birthdate);
        }
        e.setProperty("notes", contact.notes);

        Key key = datastore.put(e);
        return key.getId();
    }

    @Override
    public void delete(Long id) {
        Key k = KeyFactory.createKey("User", id);
        datastore.delete(k);
    }

    @Override
    public User get(Long id) {
        Entity e;
        try {
            e = datastore.get(KeyFactory.createKey("User", id));
        } catch (EntityNotFoundException e1) {
            return null;
        }
        return createUserFromEntity(e);
    }

    @Override
    public List<User> getAll() {
        List<User> contacts = new ArrayList<>();

        Query q = new Query("User")
                .addProjection(new PropertyProjection("firstname", String.class))
                .addProjection(new PropertyProjection("lastname", String.class))
                .addProjection(new PropertyProjection("email", String.class))
                .addProjection(new PropertyProjection("notes", String.class));

        PreparedQuery pq = datastore.prepare(q);
        pq.asIterable().forEach(entity -> contacts.add(createUserFromEntity(entity)));
        return contacts;
    }

    public User createUserFromEntity(Entity e) {
        return User.create().id(e.getKey().getId())
                .firstName((String) e.getProperty("firstname"))
                .lastName((String) e.getProperty("lastname"))
                .email((String) e.getProperty("email"))
                .birthdate((Date) e.getProperty("birthdate"))
                .notes((String) e.getProperty("notes"));

    }
}
