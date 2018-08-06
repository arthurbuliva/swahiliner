/*
 * Extracts names of people and places from strings
 */
package opennlp;

/**
 * @author Arthur Buliva
 */

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author arthur
 */
public class ApacheOpenNLPEntityFinder
{

    private static final Logger LOGGER = Logger.getLogger("global");
    private final String[] MODEL_FILES =
            {
                    "lib/apache-opennlp-1.6.0/models/en-ner-date.bin",
                    "lib/apache-opennlp-1.6.0/models/en-ner-location.bin",
                    "lib/apache-opennlp-1.6.0/models/en-ner-money.bin",
                    "lib/apache-opennlp-1.6.0/models/en-ner-organization.bin",
                    "lib/apache-opennlp-1.6.0/models/en-ner-percentage.bin",
                    "lib/apache-opennlp-1.6.0/models/en-ner-person.bin",
                    "lib/apache-opennlp-1.6.0/models/en-ner-time.bin",
            };
    private HashMap<String, ArrayList> entityMap;
    private InputStream tokenStream;
    private TokenizerModel tokenModel;
    private Tokenizer tokenizer;
    private TokenNameFinderModel entityModel;

    public ApacheOpenNLPEntityFinder()
    {
        entityMap = new HashMap<>();

        try
        {
            tokenStream = new FileInputStream(
                    new File("lib/apache-opennlp-1.6.0/models/en-token.bin"));
            tokenModel = new TokenizerModel(tokenStream);
            tokenizer = new TokenizerME(tokenModel);
        }
        catch (IOException ex)
        {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
    }

    public HashMap<String, ArrayList> getEntities(String sentence)
    {
        try
        {
            for (String model : MODEL_FILES)
            {
                entityModel = new TokenNameFinderModel(
                        new FileInputStream(model));

                NameFinderME nameFinder = new NameFinderME(entityModel);

                String[] tokens = tokenizer.tokenize(sentence);

                Span[] nameSpans = nameFinder.find(tokens);

//                When the en-ner-money.bin model is used, the index in the
//                tokens array in the earlier code sequence has to be increased by
//                one. Otherwise, all that is returned is the dollar sign.
                ArrayList<String> elements = new ArrayList<>();

                for (Span span : nameSpans)
                {
                    if (entityMap.containsKey(span.getType()))
                    {
                        elements = entityMap.get(span.getType());

//                        System.out.println(span.getType());
//                        System.out.println(span.getStart());
//                        System.out.println(span.getEnd());

                        elements.add(((model.contains("money.bin"))
                                ? (tokens[span.getStart()] + tokens[span.getStart() + 1])
                                : tokens[span.getStart()]));

                        entityMap.put(span.getType(), elements);
                    }
                    else
                    {
                        elements.add(((model.contains("money.bin"))
                                ? (tokens[span.getStart()] + tokens[span.getStart() + 1])
                                : tokens[span.getStart()]));
                        entityMap.put(span.getType(), elements);
                    }
                }
            }

        }
        catch (Exception ex)
        {
// Handle exceptions
            ex.printStackTrace();
        }

        return entityMap;
    }
}
