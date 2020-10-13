package max;


import max.coreSources.Organization;
import max.database.CollectionModel;
import max.database.Credentials;
import max.database.SQLQuery;
import max.database.UserModel;
import max.exception.AuthorizationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;

public class CollectionController {

    protected static final Logger LOG = LogManager.getLogger(CollectionController.class);
    private final CollectionModel collectionModel;
    private final UserModel userModel;

    public CollectionController(CollectionModel collectionModel, UserModel userModel) {
        this.collectionModel = collectionModel;
        this.userModel = userModel;
    }

    /**
     * Fetch the collection from the max.database
     *
     * @return collection that will be used as the local representation of the max.database
     * @throws SQLException the max.database sent an error
     */
    public HashMap<Integer, Organization> fetchCollectionFromDB() throws SQLException {
        HashMap<Integer, Organization> collection = collectionModel.fetchCollection();
        if (collection == null)
            throw new SQLException("It was not possible to fetch the collection from max.database");
        return collection;
    }

    /**
     *
     * @param credentials to try in the max.database
     * @return the credentials if the user was checked successfully and a str if was failed
     */
    public Object login(Credentials credentials) {
        try {
            int id = userModel.checkUserAndGetID(credentials);
            if (id > 0)
                return new Credentials(id, credentials.username, credentials.password);
            else
                return "User/Password given not found or incorrect";
        } catch (SQLException | NoSuchAlgorithmException ex) {
            LOG.error("logging in", ex);
            return ex.getMessage();
        }
    }

    public Object register(Credentials credentials) {
        try {
            int id = userModel.registerUser(credentials);
            if (id > 0)
                return new Credentials(id, credentials.username, credentials.password);
            else
                return credentials;
        } catch (Throwable ex) {
            LOG.error("registering user", ex);
            return ex.getMessage();
        }
    }

    public String addOrganization(int key, Organization organization, Credentials credentials) {
        try {
            if (assertUserNotExist(credentials))
                throw new AuthorizationException();

            return collectionModel.insert(key, organization, credentials);
        } catch (Throwable ex) {
            LOG.error("inserting organization in db", ex);
            return ex.getMessage();
        }
    }

    public String updateOrganization(int id, Organization organization, Credentials credentials) {
        try {
            if (assertUserNotExist(credentials))
                throw new AuthorizationException();

            return collectionModel.update(id, organization, credentials);
        } catch (Throwable ex) {
            LOG.error("updating organization in db", ex);
            return ex.getMessage();
        }
    }

    public String deleteAllOrganizations(Credentials credentials) {
        try {
            if (assertUserNotExist(credentials))
                throw new AuthorizationException();

            return collectionModel.deleteAll(credentials);
        } catch (Throwable ex) {
            LOG.error("deleting all organizations in db", ex);
            return ex.getMessage();
        }
    }

    public String deleteOrganization(int key, Credentials credentials) {
        try {
            if (assertUserNotExist(credentials))
                throw new AuthorizationException();

            return collectionModel.delete(key, credentials);
        } catch (Throwable ex) {
            LOG.error("deleting organization in db", ex);
            return ex.getMessage();
        }
    }

    public int[] deleteOrganizationsGreaterThanKey(int key, Credentials credentials) throws SQLException, NoSuchAlgorithmException {
        if (assertUserNotExist(credentials))
            throw new AuthorizationException();

        return collectionModel.deleteOnKey(key, credentials, SQLQuery.Delete.ORGANIZATION_WITH_GREATER_KEY);
    }

    public int[] deleteOrganizationsLowerThanKey(int key, Credentials credentials) throws SQLException, NoSuchAlgorithmException {
        if (assertUserNotExist(credentials))
            throw new AuthorizationException();

        return collectionModel.deleteOnKey(key, credentials, SQLQuery.Delete.ORGANIZATIONS_WITH_LOWER_KEY);
    }

    public boolean assertUserNotExist(Credentials credentials) throws SQLException, NoSuchAlgorithmException {
        return userModel.checkUserAndGetID(credentials) == -1;
    }

    public boolean credentialsNotExist(Credentials credentials) {
        try {
            if (assertUserNotExist(credentials))
                return true;
        } catch (SQLException | NoSuchAlgorithmException e) {
            return true;
        }
        return false;
    }


}