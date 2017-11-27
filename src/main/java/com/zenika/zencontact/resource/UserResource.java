package com.zenika.zencontact.resource;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.zenika.zencontact.domain.User;
import com.zenika.zencontact.persistence.UserRepository;
import com.google.gson.Gson;
import com.zenika.zencontact.persistence.datastore.UserDaoDatastore;
import com.zenika.zencontact.persistence.objectify.UserDaoObjectify;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// With @WebServlet annotation the webapp/WEB-INF/web.xml is no longer required.
@WebServlet(name = "UserResource", value = "/api/v0/users")
public class UserResource extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(UserResource.class.getName());
    public static final String CONTACTS_CACHE_KEY = "fr.ssinigaglia.contact.cache";
    public static final MemcacheService cache = MemcacheServiceFactory.getMemcacheService();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<User> contacts = (List<User>) cache.get(CONTACTS_CACHE_KEY);
        if (contacts == null) {
            contacts = UserDaoObjectify.getInstance().getAll();
            //on ne cache qu'une liste avec au moins un élément
            boolean isCached = contacts.size() > 0 &&
                    cache.put(CONTACTS_CACHE_KEY, contacts,
                            Expiration.byDeltaSeconds(240),
                            MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
            LOG.info("Cached:" + isCached);
        } else {
            LOG.info("Cache HITS");
        }
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().println(new Gson().toJsonTree(contacts));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        User user = new Gson().fromJson(request.getReader(), User.class);
        user.id(UserDaoObjectify.getInstance().save(user));
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(201);
        response.getWriter().println(new Gson().toJson(user));
        UserResource.cache.delete(UserResource.CONTACTS_CACHE_KEY);
    }
}
