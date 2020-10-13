package max.database;

public class SQLQuery {
    public static class Get {
        //ORGANIZATIONS
        public static final String ORGANIZATIONS = "SELECT organizations.id, organizations.name, " +
                "coordinates.x, coordinates.y, dragon_colors.color, organizations.annualTurnover, " +
                "organizations.creation_date, organizations.key, organizations.fullName, " +
                "organization_types.type, organization_address.officialAddress \n" +

                "FROM organizations\n" +
                "    INNER JOIN coordinates ON organizations.id = coordinates.organization_id\n" +
                "    INNER JOIN dragon_colors ON dragons.color = dragon_colors.id\n" +
                "    INNER JOIN organization_address ON organizations.officialAddress = organization_address.id\n" +
                "    INNER JOIN organization_types ON organizations.type = organization_types.id\n";

        public static final String ORGANIZATION_BY_KEY = "SELECT id FROM organizations where key = ?";

        public static final String ORGANIZATIONS_WITH_USER = "SELECT organizations.id, organizations.key, organizations.name, organizations.annualTurnover, organizations.creation_date, organizations.key, coordinates.x, coordinates.y, organization_types.type, official_Address.character, user_id\n" +
                "FROM organizations\n" +
                "    INNER JOIN coordinates ON organizations.id = coordinates.organization_id\n" +
                "    INNER JOIN organization_colors ON dragons.color = organization_colors.id\n" +
                "    INNER JOIN organization_types ON organizations.type = organization_types.id\n" +
                "    INNER JOIN organization_characters ON organizations.character = organization_characters.id\n" +
                "    INNER JOIN users_organizations ud on organizations.id = uo.organizations_id";

        //USERS
        public static final String USERS = "SELECT * FROM users";
        public static final String PASS_USING_USERNAME = "SELECT password, id FROM users WHERE username = ?";
        public static final String ID_USING_USERNAME = "SELECT id FROM users WHERE username = ?";

        public static final String USER_HAS_PERMISSIONS = "" +
                "SELECT exists(SELECT 1 from users_organizations where user_id = ? AND organization_id = ?)";
    }

    public static class Add {
        public static final String ORGANIZATION = "" +
                "INSERT INTO organizations(name, creation_date, annualTurnover, type, officialAddress, key) " +
                "VALUES(?, ?, ?, ?, ?, ?) RETURNING id";
        public static final String COORDINATE = "" +
                "INSERT INTO coordinates(x, y, organization_id) " +
                "VALUES(?, ?, ?)";

        public static final String USER = "" +
                "INSERT INTO users(username, password) VALUES(?, ?)";

        public static final String ORGANIZATION_USER_RELATIONSHIP = "" +
                "INSERT INTO users_organizations VALUES (?, ?)";
    }

    public static class Update {
        public static final String ORGANIZATION = "" +
                "UPDATE organizations SET name = ?, creation_date = ?, annualTurnover = ?, fullName = ?, type = ?, officialAddress = ? \n" +
                "WHERE organizations.id = ?";
        public static final String COORDINATE = "" +
                "UPDATE coordinates SET x = ?, y = ? WHERE organization_id = ?";
    }

    public static class Delete {
        public static final String ALL_ORGANIZATIONS = "DELETE FROM ORGANIZATIONS";
        public static final String ORGANIZATION_BY_KEY = "DELETE FROM organizations where key = ?";
        public static final String ORGANIZATION_WITH_GREATER_KEY = "" +
                "DELETE FROM organization \n" +
                "WHERE id IN (SELECT o.id FROM organizations o, users u, users_organizations uo\n" +
                "             WHERE o.key > ?\n" +
                "               AND uo.organization_id = o.id\n" +
                "               AND uo.user_id in (select id from users where id = ?)) RETURNING key";
        public static final String ORGANIZATIONS_WITH_LOWER_KEY = "" +
                "DELETE FROM organizations \n" +
                "WHERE id IN (SELECT o.id FROM organizations o, users u, users_organizations uo\n" +
                "             WHERE o.key < ?\n" +
                "               AND uo.organization_id = o.id\n" +
                "               AND uo.user_id in (select id from users where id = ?)) RETURNING key";

        public static final String USER = "DELETE FROM users where username = ?";
    }
}