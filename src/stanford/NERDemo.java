package stanford;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;

import java.util.List;


/**
 * This is a demo of calling CRFClassifier programmatically.
 * <p>
 * Usage: {@code java -mx400m -cp "*" NERDemo [serializedClassifier [fileName]] }
 * <p>
 * If arguments aren't specified, they default to
 * classifiers/english.all.3class.distsim.crf.ser.gz and some hardcoded sample text.
 * If run with arguments, it shows some of the ways to get k-best labelings and
 * probabilities out with CRFClassifier. If run without arguments, it shows some of
 * the alternative output formats that you can get.
 * <p>
 * To use CRFClassifier from the command line:
 * </p><blockquote>
 * {@code java -mx400m edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier [classifier] -textFile [file] }
 * </blockquote><p>
 * Or if the file is already tokenized and one word per line, perhaps in
 * a tab-separated value format with extra columns for part-of-speech tag,
 * etc., use the version below (note the 's' instead of the 'x'):
 * </p><blockquote>
 * {@code java -mx400m edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier [classifier] -testFile [file] }
 * </blockquote>
 *
 * @author Jenny Finkel
 * @author Christopher Manning
 */

public class NERDemo
{

    public static void main(String[] args) throws Exception
    {

        String serializedClassifier = "lib/stanford/classifiers/english.all.3class.distsim.crf.ser.gz";

        if (args.length > 0)
        {
            serializedClassifier = args[0];
        }

        AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);


      /* For the hard-coded String, it shows how to run it on a single
         sentence, and how to do this and produce several formats, including
         slash tags and an inline XML output format. It also shows the full
         contents of the {@code CoreLabel}s that are constructed by the
         classifier. And it shows getting out the probabilities of different
         assignments and an n-best list of classifications with probabilities.
      */

        String[] example = {
//                "Good afternoon Rajat Raina, how are you today?",
//                "I go to school at Stanford University, which is located in California.",
                "Addis Ababa (Amharic: ??? ???, Addis Abäba IPA: [ad?dis ?ab?ba] ( listen), \\\"new flower\\\"; or Addis Abeba (the spelling used by the official Ethiopian Mapping Authority); Oromo: Finfinne, \\\"natural spring\\\"), is the capital and largest city of Ethiopia. It is the seat of the Ethiopian federal government. According to the 2007 population census, the city has a total population of 2,739,551 inhabitants.\\n\" +\n" +
                        "                \"As a chartered city (ras gez astedader), Addis Ababa has the status of both a city and a state. It is where the African Union is and its predecessor the OAU was based. It also hosts the headquarters of the United Nations Economic Commission for Africa (ECA) and numerous other continental and international organizations. Addis Ababa is therefore often referred to as \\\"the political capital of Africa\\\" for its historical, diplomatic and political significance for the continent.\\n\" +\n" +
                        "                \"The city is populated by people from different regions of Ethiopia. It is home to Addis Ababa University."
        };

//      for (String str : example) {
//        System.out.println(classifier.classifyToString(str));
//      }
//      System.out.println("---");
//
//      for (String str : example) {
//        // This one puts in spaces and newlines between tokens, so just print not println.
//        System.out.print(classifier.classifyToString(str, "slashTags", false));
//      }
//      System.out.println("---");

//      for (String str : example) {
        // This one is best for dealing with the output as a TSV (tab-separated column) file.
        // The first column gives entities, the second their classes, and the third the remaining text in a document

//        System.out.print(classifier.classifyToString(str, "tabbedEntities", true));
//      }
//      System.out.println("---");
////
        for (String str : example)
        {
//                System.out.println(classifier.classifyToString(str));
//
//                System.out.println(classifier.classifyWithInlineXML(str));
//
//                System.out.println(classifier.classifyToString(str, "xml", true));


            List<Triple<String, Integer, Integer>> entities = classifier.classifyToCharacterOffsets(str);

            for (Triple<String, Integer, Integer> entity : entities)

            {
                System.out.println(entity.first);
                System.out.println(str.substring(entity.second, entity.third));
            }

        }
//      System.out.println("---");
//
//      for (String str : example) {
//        System.out.println(classifier.classifyToString(str, "xml", true));
//      }
//      System.out.println("---");
//
//        for (String str : example)
//        {
//            System.out.print(classifier.classifyToString(str, "tsv", false));
//        }
//      System.out.println("---");
//
//      // This gets out entities with character offsets
//      int j = 0;
//      for (String str : example) {
//        j++;
//        List<Triple<String,Integer,Integer>> triples = classifier.classifyToCharacterOffsets(str);
//        for (Triple<String,Integer,Integer> trip : triples) {
//          System.out.printf("%s over character offsets [%d, %d) in sentence %d.%n",
//                  trip.first(), trip.second(), trip.third, j);
//        }
//      }
//      System.out.println("---");
//
//      // This prints out all the details of what is stored for each token
//      int i=0;
//      for (String str : example) {
//        for (List<CoreLabel> lcl : classifier.classify(str)) {
//          for (CoreLabel cl : lcl) {
//            System.out.print(i++ + ": ");
//            System.out.println(cl.toShorterString());
//          }
//        }
//      }
//
//      System.out.println("---");

    }
}
