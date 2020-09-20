package javaProject.database;

import javaProject.coreSources.Coordinates;
import javaProject.coreSources.Organization;
import javaProject.coreSources.OrganizationType;
import javafx.scene.paint.Color;
import sun.security.krb5.internal.CredentialsUtil;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CollectionModel {
    private final Connection connection;
    private final Lock mainLock;

    public CollectionModel(Connection connection) {
        this.connection = connection;
        mainLock = new ReentrantLock();
    }

    public HashMap<Integer, Organization> fetchCollection() throws SQLException {
        HashMap<Integer, Organization> collection = new HashMap<>();
        PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.Get.ORGANIZATIONS);
        ResultSet rs = preparedStatement.executeQuery();
        while(rs.next()) {
            ZonedDateTime creationDate = rs.getTimestamp("creation_date").toLocalDateTime().atZone(ZoneId.of("UTC"));
            Organization organization = new Organization(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("fullName"),
                    rs.getString("address"),
                    new Coordinates(rs.getLong("x")), rs.getFloat("y"),
                    rs.getLong("age"), creationDate,
                    OrganizationType.valueOf(rs.toString("type"))

                    collection.putIfAbsent(rs.getInt("key"), organization));
        }
        return collection;
    }

    public boolean hasPermissions(Credentials credentials, int dragonID) throws SQLException {
        if (credentials.username.equals(UserModel.ROOT_USERNAME))
            return true;

        PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.Get.USER_HAS_PERMISSIONS);
        int pointer = 0;
        preparedStatement.setInt(++pointer, credentials.id);
        preparedStatement.setInt(++pointer, dragonID);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            return rs.getBoolean("exists");
        }
        return false;
    }


    public String insert(int key, Organization organization, Credentials credentials) throws SQLException {
        mainLock.lock();
        final boolean oldAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.Add.ORGANIZATION);
            int pointer = 0;
            preparedStatement.setString(++pointer, organization.getName());
            preparedStatement.setTimestamp(++pointer, Timestamp.valueOf(organization.getCreationDate().toLocalDateTime()));
            preparedStatement.setLong(++pointer, organization.getAge());
            preparedStatement.setInt(++pointer, organization.getColor().ordinal()+1);
            preparedStatement.setInt(++pointer, organization.getType().ordinal()+1);
            preparedStatement.setInt(++pointer, organization.getCharacter().ordinal()+1);
            preparedStatement.setInt(++pointer, key);
            ResultSet rs = preparedStatement.executeQuery();
            int organizationID = 0;
            if (rs.next())
                organizationID = rs.getInt(1);

            preparedStatement = connection.prepareStatement(SQLQuery.Add.COORDINATE);
            pointer = 0;
            preparedStatement.setLong(++pointer, organization.getCoordinates().getX());
            preparedStatement.setFloat(++pointer, organization.getCoordinates().getY());
            preparedStatement.setInt(++pointer, organizationID);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(SQLQuery.Add.DRAGON_HEAD);
            pointer = 0;
            if (organization.getHead().getEyesCount() == null)
                preparedStatement.setNull(++pointer, Types.DOUBLE);
            else
                preparedStatement.setDouble(++pointer, organization.getHead().getEyesCount());
            preparedStatement.setInt(++pointer, organizationID);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(SQLQuery.Add.DRAGON_USER_RELATIONSHIP);
            pointer = 0;
            preparedStatement.setInt(++pointer, credentials.id);
            preparedStatement.setInt(++pointer, organizationID);
            preparedStatement.executeUpdate();

            connection.commit();

            return String.valueOf(organizationID);
        } catch (Throwable e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
            mainLock.unlock();
        }
    }


    public String update(int id, Organization organization, Credentials credentials) throws SQLException {
        if (!hasPermissions(credentials, id))
            return "You have no permissions to edit this organization";

        mainLock.lock();
        final boolean oldAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.Update.DRAGON);
            int pointer = 0;
            preparedStatement.setString(++pointer, organization.getName());
            preparedStatement.setTimestamp(++pointer, Timestamp.valueOf(organization.getCreationDate().toLocalDateTime()));
            preparedStatement.setLong(++pointer, organization.getAge());
            preparedStatement.setInt(++pointer, organization.getColor().ordinal()+1);
            preparedStatement.setInt(++pointer, organization.getType().ordinal()+1);
            preparedStatement.setInt(++pointer, organization.getCharacter().ordinal()+1);
            preparedStatement.setInt(++pointer, id);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(SQLQuery.Update.COORDINATE);
            pointer = 0;
            preparedStatement.setLong(++pointer, organization.getCoordinates().getX());
            preparedStatement.setFloat(++pointer, organization.getCoordinates().getY());
            preparedStatement.setInt(++pointer, id);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(SQLQuery.Update.DRAGON_HEAD);
            pointer = 0;
            if (organization.getHead().getEyesCount() == null)
                preparedStatement.setNull(++pointer, Types.DOUBLE);
            else
                preparedStatement.setDouble(++pointer, organization.getHead().getEyesCount());
            preparedStatement.setInt(++pointer, id);
            preparedStatement.executeUpdate();

            connection.commit();

            return null;
        } catch (Throwable e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
            mainLock.unlock();
        }
    }

    public int getDragonByKey(int key) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.Get.DRAGON_BY_KEY);
        int pointer = 0;
        preparedStatement.setInt(++pointer, key);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next())
            return rs.getInt(1);
        return -1;
    }


    public String deleteAll(Credentials credentials) throws SQLException {
        if (!credentials.username.equals(UserModel.ROOT_USERNAME))
            return "You have no permissions to delete all dragons";

        mainLock.lock();
        final boolean oldAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.Delete.ALL_ORGANIZATIONS);
            preparedStatement.executeUpdate();
            connection.commit();
            return null;
        } catch (Throwable e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
            mainLock.unlock();
        }
    }


    public String delete(int key, Credentials credentials) throws SQLException {
        int dragonID = getDragonByKey(key);
        if (!hasPermissions(credentials, dragonID))
            return "You have no permissions to delete this dragon";

        mainLock.lock();
        final boolean oldAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.Delete.ORGANIZATION_BY_KEY);
            int pointer = 0;
            preparedStatement.setInt(++pointer, key);
            preparedStatement.executeUpdate();

            connection.commit();

            return null;
        } catch (Throwable e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
            mainLock.unlock();
        }
    }

    public int[] deleteOnKey(int key, Credentials credentials, String query) throws SQLException {
        mainLock.lock();
        final boolean oldAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            int pointer = 0;
            preparedStatement.setInt(++pointer, key);
            preparedStatement.setInt(++pointer, credentials.id);
            ResultSet rs = preparedStatement.executeQuery();

            connection.commit();

            return getKeysFromResultSet(rs);
        } catch (Throwable e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
            mainLock.unlock();
        }
    }

    public int[] getKeysFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<Integer> deletedKeys = new ArrayList<>();
        while (rs.next())
            deletedKeys.add(rs.getInt(1));

        int[] keysArr = new int[deletedKeys.size()];
        for (int i=0; i < keysArr.length; i++)
            keysArr[i] = deletedKeys.get(i);

        return keysArr;
    }
}
