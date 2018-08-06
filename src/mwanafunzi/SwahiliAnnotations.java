package mwanafunzi;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SwahiliAnnotations
{
    private static Connection connection = null;

    public SwahiliAnnotations()
    {
        try
        {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mwanafunzi?" +
                            "autoReconnect=true&verifyServerCertificate=false&useSSL=true&serverTimezone=UTC",
                    "root",
                    "Lemonade");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) throws SQLException
    {
        SwahiliAnnotations swahiliAnnotations = new SwahiliAnnotations();

        HashMap<Integer, HashMap> corpus_entries = swahiliAnnotations.loadEnglishAnnotations();
        HashMap<String, String> translations = swahiliAnnotations.loadTranslations();

        for (Map.Entry<Integer, HashMap> corpus_entry : corpus_entries.entrySet())
        {
            int key = corpus_entry.getKey();
            HashMap<String, String> entityMap = corpus_entry.getValue();

            for (Map.Entry<String, String> entityCorpus : entityMap.entrySet())
            {
                String entity = entityCorpus.getKey();
                String englishWord = entityCorpus.getValue();

//                System.out.println(key);
//                System.out.println(entityMap);
//                System.out.println(entity);
//                System.out.println(englishWord);

                String translation = translations.get(englishWord);

                if (translation != null && !translation.isEmpty())
                {
                    System.out.println(String.format("%s : %s : %s", entity, englishWord, translation));

                    // Add these to the known annotations

                    String ner = "INSERT INTO annotations (identifier, value) VALUES (?, ?)";

                    PreparedStatement insertStatement = connection.prepareStatement(ner);
                    insertStatement.setString(1, entity);
                    insertStatement.setString(2, translation);

                    try
                    {
                        insertStatement.executeUpdate();
                    }
                    catch (java.sql.SQLIntegrityConstraintViolationException duplicate)
                    {
                        System.out.println(duplicate.getMessage());
                    }
                }
            }
        }

        swahiliAnnotations.closeConnection();
    }

    /**
     * Load the corpus
     */
    private HashMap<Integer, HashMap> loadEnglishAnnotations() throws SQLException
    {
        HashMap<Integer, HashMap> english_corpus = new HashMap();


        String query = "SELECT id, identifier, value FROM annotations ORDER BY length(`value`) DESC";

        PreparedStatement statement = connection.prepareStatement(query);

        ResultSet englishEntriesResultSet = statement.executeQuery();

        while (englishEntriesResultSet.next())
        {
            HashMap<String, String> translations = new HashMap();

            translations.put
                    (
                            englishEntriesResultSet.getString("identifier"),

                            englishEntriesResultSet.getString("value")
                    );

            english_corpus.put
                    (
                            englishEntriesResultSet.getInt("id"),
                            translations
                    );
        }

        return english_corpus;
    }

    /**
     * Load the corpus
     * TODO: KamusiProject data should be loaded here
     */
    private HashMap<String, String> loadTranslations() throws SQLException
    {
        HashMap<String, String> translations = new HashMap();

        String query = "SELECT EnglishWord, SwahiliWord FROM dict";

        PreparedStatement statement = connection.prepareStatement(query);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            translations.put(resultSet.getString("EnglishWord"),
                    resultSet.getString("SwahiliWord"));
        }

        return translations;
    }

    private void closeConnection()
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
