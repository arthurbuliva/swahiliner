/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mwanafunzi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Arthur Buliva
 */
public class Mwanafunzi implements Runnable
{

    private static final Logger LOGGER = Logger.getLogger("global");

    private PreparedStatement statement;
    private Connection connection;
    private Map<Integer, String> swahili_entries;
    private Map<String, String> annotations;
    private File annotatedCorpusFile;
    private FileWriter fstream;
    private BufferedWriter writer;

    public Mwanafunzi()
    {
        try
        {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mwanafunzi?" +
                            "autoReconnect=true&verifyServerCertificate=false&useSSL=true&serverTimezone=UTC",
                    "root",
                    "Lemonade");

            LOGGER.log(Level.INFO, "Database connection established successfully");

            // Load annotations to memory
            annotations = loadAnnotations();

            // Load corpus to memory
            swahili_entries = loadCorpus();

            annotatedCorpusFile = new File("Swahili.txt");
            fstream = new FileWriter(annotatedCorpusFile.getCanonicalPath(), true);
            writer = new BufferedWriter(fstream);
        }
        catch (Exception ex)
        {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        Mwanafunzi t1 = new Mwanafunzi();

        Thread a = new Thread(t1, "t1");

        a.start();
    }

    @Override
    public void run()
    {
        long startTime = System.currentTimeMillis();

        // Annotate the corpus
        annotateCorpus();

        long endTime = System.currentTimeMillis();

        double time = (endTime - startTime) / 1000;

        LOGGER.log(Level.INFO,
                String.format("Process took %s seconds",
                        time)
        );
    }

    /**
     * Load the annotation database
     */
    private LinkedHashMap<String, String> loadAnnotations() throws SQLException
    {
        LOGGER.log(Level.INFO, "Loading annotations");

        LinkedHashMap<String, String> annotations = new LinkedHashMap();

        // Load the annotated entities into memory, annotating each entry into the HashMap
        String query = "SELECT id, identifier, value FROM annotations ORDER BY length(`value`) DESC";

        statement = connection.prepareStatement(
                query
        );

        ResultSet entriesResultSet = statement.executeQuery();

        while (entriesResultSet.next())
        {
            String typeOfEntity = entriesResultSet.getString("identifier");
            String entity = entriesResultSet.getString("value");

            // Reverse them because HashMap cannot take duplicate keys
            annotations.put(entity, typeOfEntity);
        }

        LOGGER.log(Level.INFO, String.format("%d Annotations loaded to memory", annotations.size()));

        return annotations;
    }

    /**
     * Load the corpus
     */
    private HashMap<Integer, String> loadCorpus() throws SQLException
    {
        LOGGER.log(Level.INFO, "Loading corpus");

        HashMap<Integer, String> swahili_entries = new HashMap();

        // Load the swahili_entries HashMap into memory
        String query = "SELECT id, swahili_text FROM corpus ORDER BY RAND()";

        statement = connection.prepareStatement(
                query,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
        );

        ResultSet swahili_entriesResultSet = statement.executeQuery();

        while (swahili_entriesResultSet.next())
        {
            swahili_entries.put(swahili_entriesResultSet.getInt("id"), swahili_entriesResultSet.getString("swahili_text"));
        }

        LOGGER.log(Level.INFO, String.format("%d corpus sentences loaded to memory", swahili_entries.size()));

        return swahili_entries;
    }

    private void annotateCorpus()
    {
        int index = 1;
        int size = swahili_entries.size();

        for (Map.Entry<Integer, String> swahili_text : swahili_entries.entrySet())
        {
            int key = swahili_text.getKey();
            String entityText = swahili_text.getValue();

            entityText = entityText.replaceAll("\\p{Punct}", " $0 ");

            System.out.println(String.format(
                    "Row %d of %d: %d%%",
                    index,
                    size,
                    (index * 100) / size
            ));

            LOGGER.log(Level.INFO,
                    String.format(
                            "Row %d of %d: %d%%",
                            index,
                            size,
                            (index * 100) / size
                    )
            );

            String annotatedText = annotateText(entityText);

            try
            {
                writer.write(annotatedText);
                writer.newLine();

                writer.flush();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            index++;
        }
    }

    public String annotateText(String text)
    {
        // A list of tags that we will need to skip
        LinkedList<String> skippedTags = new LinkedList<>();

        // A list of all tags
        LinkedList<String> parentTags = new LinkedList<>();

        // Make the string to be onle long line without any line breaks
        text = text.replaceAll(System.getProperty("line.separator"), "");

        // Loop through the annotations
        for (Map.Entry<String, String> annotation : annotations.entrySet())
        {
            // annotations are in the format of (entity, typeOfEntity)
            // For example (Nairobi National Park, Location)

            String namedEntity = annotation.getKey(); // Nairobi National Park

            String searchExpression = "(?i)(?<!\\w(?=\\w))(" + Pattern.quote(namedEntity) +
                    ")(?!(?<=\\w)\\w)";

            Pattern pattern = Pattern.compile(searchExpression);
            Matcher matcher = pattern.matcher(text);

            // If we find Nairobi National Park in the text we are analyzing...
            if (matcher.find())
            {
                // Add Nairobi National Park to the parent tags
                parentTags.add(namedEntity);
            }
        }

        // For each of the parent tags (Nairobi National Park, Nairobi, Kenya...)
        for (String childTag : parentTags)
        {
            String searchParentTags = "(?i)(?<!\\w(?=\\w))(" + Pattern.quote(childTag) +
                    ")(?!(?<=\\w)\\w)";

            Pattern patternParentTags = Pattern.compile(searchParentTags);

            // Loop back through the parent tags
            for (String parentTag : parentTags)
            {
                Matcher matcherParentTags = patternParentTags.matcher(parentTag);

                // Since the parent tags are arranged by length descending,
                // We need to skip tagging "Nairobi" since "Nairobi National Park"
                // Already takes care of this. That way, we avoid having something like this:
                // <START:LOCATION> <START:LOCATION> Nairobi <END> National Park <END>
                // What we instead need is this:
                // <START:LOCATION> Nairobi National Park <END>
                if (matcherParentTags.find() && !parentTag.equals(childTag))
                {
                    skippedTags.add(childTag);
                }
            }
        }

        LinkedList<String> finalTags = parentTags;

        // Remove the skipped tags from the LinkedHashMap
        for (String skip : skippedTags)
        {
            LOGGER.log(Level.INFO, String.format("Skipping annotation: %s", skip));

            finalTags.remove(skip);
        }

        // Do the actual annotations based on the final list
        for (String namedEntity : finalTags)
        {
            String identifier = (annotations.get(namedEntity));

            String annotatedTextFormat = String.format(
                    " <%s:%s> %s <%s> ",
                    "START",
                    identifier,
                    namedEntity,
                    "END"
            );

            String searchExpression = "(?i)(?<!\\w(?=\\w))(" + Pattern.quote(namedEntity) +
                    ")(?!(?<=\\w)\\w)";

            Pattern pattern = Pattern.compile(searchExpression);
            Matcher matcher = pattern.matcher(text);

            if (matcher.find())
            {
                try
                {
                    text = (text.replaceAll(searchExpression, annotatedTextFormat));
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }

        return text;//.replaceAll("\\s+", " ");
    }
}
