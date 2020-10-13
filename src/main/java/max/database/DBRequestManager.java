package max.database;

import com.sun.org.apache.xpath.internal.operations.Or;
import max.coreSources.Organization;
import max.exception.AuthorizationException;
import max.exception.NotPermissionsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.util.PSQLException;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.ResourceBundle;

public class DBRequestManager {

    protected static final Logger LOG = LogManager.getLogger(DBRequestManager.class);
    private final CollectionModel collectionModel;
    private final UserModel userModel;

    public DBRequestManager(CollectionModel collectionModel, UserModel userModel) {
        this.collectionModel = collectionModel;
        this.userModel = userModel;
    }

    /**
     * Fetch the collection from the database
     *
     * @return collection that will be used as the local representation of the database
     * @throws SQLException the database sent an error
     */
    public HashMap<Integer, Organization> fetchCollectionFromDB() throws SQLException {
        HashMap<Integer, Organization> collection = collectionModel.fetchCollection();
        if (collection == null)
            throw new SQLException("It was not possible to fetch the collection from database");
        return collection;
    }

    /**
     *
     * @param credentials to try in the database
     * @return the credentials if the user was checked successfully and a str if was failed
     */
    public Object login(Credentials credentials, ResourceBundle bundle) {
        try {
            int id = userModel.checkUserAndGetID(credentials);
            if (id > 0)
                return new Credentials(id, credentials.username, credentials.password);
            else
                return bundle.getString("server.response.login.error.notposible");
        } catch (SQLException | NoSuchAlgorithmException ex) {
            LOG.error("logging in", ex);
            return getSQLErrorString("log in", bundle, ex);
        }
    }

    public Object register(Credentials credentials, ResourceBundle bundle) {
        try {
            int id = userModel.registerUser(credentials);
            if (id > 0)
                return new Credentials(id, credentials.username, credentials.password);
            else
                return credentials;
        } catch (Throwable ex) {
            LOG.error("registering user", ex);
            if (ex instanceof PSQLException)
                if (((PSQLException)ex).getSQLState().equalsIgnoreCase("23505"))
                    return bundle.getString("server.response.register.error.duplicate");
            return getSQLErrorString("register user", bundle, ex);
        }
    }

    public String addOrganization(int key, Organization organization, Credentials credentials, ResourceBundle bundle) {
        try {
            if (assertUserNotExist(credentials))
                throw new AuthorizationException();

            return collectionModel.insert(key, organization, credentials);
        } catch (Throwable ex) {
            LOG.error("inserting organization in db", ex);
            return getSQLErrorString("insert organization", bundle, ex);
        }
    }

    public String updateOrganization(int id, Organization organization, Credentials credentials, ResourceBundle bundle) {
        try {
            if (assertUserNotExist(credentials))
                throw new AuthorizationException();

            return collectionModel.update(id, organization, credentials);
        } catch (NotPermissionsException ex) {
            return bundle.getString("server.response.error.not.permissions");
        } catch (Throwable ex) {
            LOG.error("updating organization in db", ex);
            return getSQLErrorString("update organization", bundle, ex);
        }
    }

    public String deleteAllOrganization(Credentials credentials, ResourceBundle bundle) {
        try {
            if (assertUserNotExist(credentials))
                throw new AuthorizationException();

            return collectionModel.deleteAll(credentials);
        } catch (NotPermissionsException ex) {
            return bundle.getString("server.response.error.not.permissions");
        } catch (Throwable ex) {
            LOG.error("deleting all organizations in db", ex);
            return getSQLErrorString("delete ALL organizations", bundle, ex);
        }
    }

    public String deleteOrganization(int key, Credentials credentials, ResourceBundle bundle) {
        try {
            if (assertUserNotExist(credentials))
                throw new AuthorizationException();

            return collectionModel.delete(key, credentials);
        } catch (NotPermissionsException ex) {
            return bundle.getString("server.response.error.not.permissions");
        } catch (Throwable ex) {
            LOG.error("deleting organization in db", ex);
            return getSQLErrorString("delete organization", bundle, ex);
        }
    }

    public int[] deleteOrganizationsGreaterThanKey(int key, Credentials credentials) throws SQLException, NoSuchAlgorithmException {
        if (assertUserNotExist(credentials))
            throw new AuthorizationException();

        return collectionModel.deleteOnKey(key, credentials, SQLQuery.Delete.ORGANIZATION_WITH_GREATER_KEY);
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

    public String getSQLErrorString(String methodName, ResourceBundle bundle, Throwable ex) {
        return MessageFormat.format(
                bundle.getString("server.response.error.sqlexception"),
                methodName,
                ex.getMessage());
    }
}
