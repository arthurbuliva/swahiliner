/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mwanafunzi;

import opennlp.ApacheOpenNLPEntityFinder;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Arthur Buliva
 */
public class ApacheOpenNLPStudent implements Runnable
{

    private static final Logger LOGGER = Logger.getLogger("global");
    private final ApacheOpenNLPEntityFinder apacheOpenNLPEntityFinder;
    public int limit = 0;
    private PreparedStatement statement;
    private Connection connection;

    public ApacheOpenNLPStudent(int limit)
    {
        this.limit = limit;

        apacheOpenNLPEntityFinder = new ApacheOpenNLPEntityFinder();

        String query = "SELECT english_title, english_text, swahili_title, swahili_text FROM corpus ORDER BY RAND() LIMIT ?, 1";

        try
        {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mwanafunzi?" +
                            "autoReconnect=true&verifyServerCertificate=false&useSSL=true&serverTimezone=UTC",
                    "root",
                    "Lemonade");
            statement = connection.prepareStatement(query);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        ApacheOpenNLPStudent t1 = new ApacheOpenNLPStudent(5000);
//        ApacheOpenNLPStudent t2 = new ApacheOpenNLPStudent(10000);
//        ApacheOpenNLPStudent t3 = new ApacheOpenNLPStudent(15000);
//        ApacheOpenNLPStudent t4 = new ApacheOpenNLPStudent(20000);
//        ApacheOpenNLPStudent t5 = new ApacheOpenNLPStudent(25000);
//        ApacheOpenNLPStudent t6 = new ApacheOpenNLPStudent(30000);

        Thread a = new Thread(t1, "t1");
//        Thread b = new Thread(t2, "t2");
//        Thread c = new Thread(t3, "t3");
//        Thread d = new Thread(t4, "t4");
//        Thread e = new Thread(t5, "t5");
//        Thread f = new Thread(t6, "t6");

        a.start();
//        b.start();
//        c.start();
//        d.start();
//        e.start();
//        f.start();
    }

    @Override
    public void run()
    {
        try
        {
            statement.setInt(1, limit);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
            {
                LOGGER.log(Level.FINE,
                        String.format("Thread : %s; %s : %s", (limit / 5),
                                resultSet.getString("english_title"),
                                resultSet.getString("swahili_title"))
                );

                System.out.println(resultSet.getString("english_text"));
                System.out.println(apacheOpenNLPEntityFinder.getEntities(resultSet.getString("english_text")));
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
