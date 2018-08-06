/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mwanafunzi;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;
import opennlp.ApacheOpenNLPEntityFinder;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Arthur Buliva
 */
public class StanfordStudent implements Runnable
{

    private static final Logger LOGGER = Logger.getLogger("global");
    private final ApacheOpenNLPEntityFinder apacheOpenNLPEntityFinder;
    public int limit;
    private PreparedStatement statement;
    private Connection connection;
    private String[] serializedClassifierArray =
            {
                    "lib/stanford/classifiers/english.all.3class.distsim.crf.ser.gz",
                    "lib/stanford/classifiers/english.conll.4class.distsim.crf.ser.gz",
                    "lib/stanford/classifiers/english.muc.7class.distsim.crf.ser.gz"
            };

    public StanfordStudent(int limit)
    {
        this.limit = limit;

        apacheOpenNLPEntityFinder = new ApacheOpenNLPEntityFinder();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        StanfordStudent t1 = new StanfordStudent(5000);
        StanfordStudent t2 = new StanfordStudent(10000);
        StanfordStudent t3 = new StanfordStudent(15000);
        StanfordStudent t4 = new StanfordStudent(20000);
        StanfordStudent t5 = new StanfordStudent(25000);
        StanfordStudent t6 = new StanfordStudent(30000);

        Thread a = new Thread(t1, "t1");
        Thread b = new Thread(t2, "t2");
        Thread c = new Thread(t3, "t3");
        Thread d = new Thread(t4, "t4");
        Thread e = new Thread(t5, "t5");
        Thread f = new Thread(t6, "t6");

        a.start();
        b.start();
        c.start();
        d.start();
        e.start();
        f.start();
    }

    @Override
    public void run()
    {

        try
        {

            String query = "SELECT english_title, english_text, swahili_title, swahili_text FROM corpus LIMIT ?, 5000";

            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mwanafunzi?" +
                            "autoReconnect=true&verifyServerCertificate=false&useSSL=true&serverTimezone=UTC",
                    "root",
                    "Lemonade");
            statement = connection.prepareStatement(query);
            statement.setInt(1, limit);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
            {
                LOGGER.log(Level.INFO,
                        String.format("Thread : %s; %s : %s", (limit / 5),
                                resultSet.getString("english_title"),
                                resultSet.getString("swahili_title"))
                );

                String english_text = resultSet.getString("english_text");

                for (String serializedClassifier : serializedClassifierArray)
                {
                    AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
                    List<Triple<String, Integer, Integer>> entities = classifier.classifyToCharacterOffsets(english_text);

                    for (Triple<String, Integer, Integer> entity : entities)

                    {
                        String identifier = (entity.first);
                        String element = (english_text.substring(entity.second, entity.third));

                        String ner = "INSERT INTO entities (identifier, value) VALUES (?, ?)";

                        PreparedStatement insertStatement = connection.prepareStatement(ner);
                        insertStatement.setString(1, identifier);
                        insertStatement.setString(2, element);

                        LOGGER.log(Level.INFO, String.format("%s: %s", identifier, element));

                        insertStatement.executeUpdate();
                    }
                }
            }
        }
        catch (SQLException ex)
        {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        finally
        {
            try
            {
                connection.close();
            }
            catch (SQLException ex)
            {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }
}
