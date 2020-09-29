package javaProject.database;

public class SQLQuery {
    public static class Get {
        //ORGANIZATIONS
        public static final String ORGANIZATIONS = "SELECT organization.id, organization.name, organization.age, organizations.creation_date, organizations.key, coordinates.x, coordinates.y, organization_colors.color, organization_types.type, organization_characters.character, organization_heads.num_eyes\n" +
                "FROM organizations\n" +
                "    INNER JOIN coordinates ON organization.id = coordinates.organization_id\n" +
                "    INNER JOIN organization_colors ON organization.color = organization_colors.id\n" +
                "    INNER JOIN organization_types ON organization.type = organization_types.id\n";
        public static final String DRAGON_BY_KEY = "SELECT id FROM organizations where key = ?";

        //USERS
        public static final String USERS = "SELECT * FROM users";
        public static final String PASS_USING_USERNAME = "SELECT password, id FROM users WHERE username = ?";
        public static final String ID_USING_USERNAME = "SELECT id FROM users WHERE username = ?";

        public static final String USER_HAS_PERMISSIONS = "" +
                "SELECT exists(SELECT 1 from users_organizations where user_id = ? AND organization_id = ?)";
    }

    public static class Add {
        public static final String ORGANIZATION = "" +
                "INSERT INTO organizations(name, creation_date, fullName, official address, type, character, key) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?) RETURNING id";
        public static final String COORDINATE = "" +
                "INSERT INTO coordinates(x, y, organization_id) " +
                "VALUES(?, ?, ?)";
//!!!!!!
        public static final String USER = "" +
                "INSERT INTO users(username, password) VALUES(?, ?)";

        public static final String ORGANIZATION_USER_RELATIONSHIP = "" +
                "INSERT INTO users_organization VALUES (?, ?)";
    }

    public static class Update {
        public static final String ORGANIZATION = "" +
                "UPDATE organizations SET name = ?, creation_date = ?, fullName = ?, officialAddress = ?, type = ?, character = ?\n" +
                "WHERE organizations.id = ?";
        public static final String COORDINATE = "" +
                "UPDATE coordinates SET x = ?, y = ? WHERE organization_id = ?";
    }

    public static class Delete {
        public static final String ALL_ORGANIZATIONS = "DELETE FROM ORGANIZATIONS";
        public static final String ORGANIZATION_BY_KEY = "DELETE FROM organizations where key = ?";
        public static final String ORGANIZATION_WITH_GREATER_KEY = "" +
                "DELETE FROM organization \n" +
                "WHERE id IN (SELECT o.id FROM organizations o, users u, users_dragons ud\n" +
                "             WHERE o.key > ?\n" +
                "               AND ud.organization_id = d.id\n" +
                "               AND ud.user_id in (select id from users where id = ?)) RETURNING key";
        public static final String ORGANIZATIONS_WITH_LOWER_KEY = "" +
                "DELETE FROM organizations \n" +
                "WHERE id IN (SELECT o.id FROM organizations o, users u, users_organizations uo\n" +
                "             WHERE o.key < ?\n" +
                "               AND ud.organization_id = o.id\n" +
                "               AND ud.user_id in (select id from users where id = ?)) RETURNING key";

        public static final String USER = "DELETE FROM users where username = ?";
    }
}