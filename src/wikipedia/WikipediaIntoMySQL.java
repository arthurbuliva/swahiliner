/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Fetches the first paragraphs of titles in Wikipedia and populates a MongoDB
 * instance with the data
 * <p>
 * Warning: This file may take a long while to run to completion.
 *
 * @author arthur
 */
public class WikipediaIntoMySQL
{

    private static final Logger logger = Logger.getLogger("global");
    private String[] wikiSentences;
    private String englishText;
    private String swahiliText;
    private long numberOfLines;
    private long currentLine = 0;
    private WikipediaDataFetcher wikiFetcher;
    private PreparedStatement statement;
    private Connection connection;

    public WikipediaIntoMySQL()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            connection = DriverManager.getConnection("jdbc:mysql://localhost/mwanafunzi", "root", "Lemonade");

            String query = "INSERT INTO corpus(english_title, swahili_title, english, swahili) VALUES (?,?,?,?)";

            statement = connection.prepareStatement(query);
        }
        catch (IllegalAccessException
                | InstantiationException
                | ClassNotFoundException
                | SQLException ex)
        {
            logger.log(Level.WARNING, ex.getMessage());
        }
    }

    public static void main(String[] args) throws Exception
    {
        WikipediaIntoMySQL wpt = new WikipediaIntoMySQL();
        wpt.fetchWikis();
    }

    /**
     * Fetch the Wikipedia titles from the dumped text file
     */
    public void fetchWikis()
    {
        wikiFetcher = new WikipediaDataFetcher();

        try
        {
            File file = new File("Titles.txt");

            Map<String, String> titlesMap = new LinkedHashMap<>();

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;

            while ((line = bufferedReader.readLine()) != null)
            {
                String[] titlesArray = line.split("\\|\\|\\|");

                titlesMap.put(titlesArray[1].trim(), titlesArray[0].trim());
            }

            Stream<String> lines = Files.lines(file.toPath(), Charset.defaultCharset());
            numberOfLines = lines.count();

            for (Map.Entry<String, String> entry : titlesMap.entrySet())
            {
                String englishTitle = entry.getKey();
                String swahiliTitle = entry.getValue();

                currentLine++;

                System.out.print("Record " + currentLine + " of " + numberOfLines + "\t");
                System.out.print((currentLine * 100) / numberOfLines);
                System.out.println("%");

                englishText = wikiFetcher.fetchData("en", englishTitle);
                swahiliText = wikiFetcher.fetchData("sw", swahiliTitle);

                logger.log(Level.INFO, englishTitle);

                System.out.println(englishText);
                System.out.println(swahiliText);

                statement.setString(1, englishTitle);
                statement.setString(2, swahiliTitle);
                statement.setString(3, englishText);
                statement.setString(4, swahiliText);

                statement.executeUpdate();
            }
            ;
        }
        catch (SQLException | IOException ex)
        {
            logger.log(Level.WARNING, ex.getMessage());
        }
        finally
        {
            try
            {
                connection.close();
            }
            catch (SQLException ex)
            {
                logger.log(Level.SEVERE, ex.getMessage());
            }
        }
    }
}
