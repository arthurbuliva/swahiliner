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
import java.io.InputStream;

/**
 * @author arthur
 */
public class KamusiEntityFinder
{

    public static void main(String[] args) throws Exception
    {

        String text =
                "Hii imewafanya wasimamizi wa maandalizi ya mashindano hayo yanayoendelea mjini Asaba nchini Nigeria, na wakuu wa riadha Tanzania kuhusika kwenye majibizano kufuatia hali ya timu ya Tanzania kukwama nchini mwao.";

        ApacheOpenNLPEntityFinder apacheOpenNLPEntityFinder = new ApacheOpenNLPEntityFinder();

        System.out.println(text);
        System.out.println(apacheOpenNLPEntityFinder.getEntities(text));

//        System.exit(0);


        InputStream is = new FileInputStream("ner-custom-model.bin");

        // load the model from file
        TokenNameFinderModel model = new TokenNameFinderModel(is);
        is.close();

        // feed the model to name finder class
        NameFinderME nameFinder = new NameFinderME(model);

        InputStream tokenStream;
        TokenizerModel tokenModel;
        Tokenizer tokenizer;
        TokenNameFinderModel entityModel;

        tokenStream = new FileInputStream(
                new File("lib/apache-opennlp-1.6.0/models/en-token.bin"));
        tokenModel = new TokenizerModel(tokenStream);
        tokenizer = new TokenizerME(tokenModel);
        String[] sentence = tokenizer.tokenize(text);


        Span nameSpans[] = nameFinder.find(sentence);

        // nameSpans contain all the possible entities detected
        for (Span s : nameSpans)
        {
            System.out.print(s.toString());
            System.out.print("  :  ");
            // s.getStart() : contains the start index of possible name in the input string array
            // s.getEnd() : contains the end index of the possible name in the input string array
            for (int index = s.getStart(); index < s.getEnd(); index++)
            {
                System.out.print(sentence[index] + " ");
            }
            System.out.println();
        }

        System.out.println("========================");
    }
}
