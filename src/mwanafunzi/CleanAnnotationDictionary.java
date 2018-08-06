package mwanafunzi;

import java.sql.*;
import java.util.StringTokenizer;

public class CleanAnnotationDictionary
{
    public static void main(String[] args)
    {

        Connection connection = null;

        try
        {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mwanafunzi?" +
                            "autoReconnect=true&verifyServerCertificate=false&useSSL=true&serverTimezone=UTC",
                    "root",
                    "Lemonade");


            String query = "SELECT id, identifier, value FROM entities GROUP BY `value` ORDER BY length(`value`) DESC";

            PreparedStatement statement = connection.prepareStatement(
                    query,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
            {
                String typeOfEntity = resultSet.getString("identifier");
                String entity = resultSet.getString("value");

                if (entity.contains("\n"))
                {
                    StringTokenizer tokens = new StringTokenizer(entity, "\n");

                    while (tokens.hasMoreTokens())
                    {
                        // Insert the different entities into different rows then delete this one
                        insertRecord(connection, typeOfEntity, tokens.nextToken());
                    }

                    deleteParentrecord(connection, resultSet.getInt("id"));
                }
                else if (entity.contains("|"))
                {
                    StringTokenizer tokens = new StringTokenizer(entity, "|");

                    while (tokens.hasMoreTokens())
                    {
                        // Insert the different entities into different rows then delete this one
                        insertRecord(connection, typeOfEntity, tokens.nextToken());
                    }

                    deleteParentrecord(connection, resultSet.getInt("id"));
                }
                else
                {

                }
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                connection.close();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private static void deleteParentrecord(Connection connection, int id) throws SQLException
    {
        PreparedStatement del = connection.prepareStatement("DELETE FROM entities WHERE id = ?");
        del.setInt(1, id);

        del.executeUpdate();

        System.out.println(del.getWarnings());

        System.out.println(String.format("Record %s deleted", id));

    }

    private static void insertRecord(Connection connection, String typeOfEntity, String value) throws SQLException
    {
        String query = "INSERT INTO entities(identifier, value) VALUES(?, ?)";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, typeOfEntity);
        statement.setString(2, value);

        statement.executeUpdate();

        System.out.println(statement.getWarnings());

        System.out.println(String.format("Record inserted %s: %s", typeOfEntity, value));
    }
}
