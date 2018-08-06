package opennlp;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;

import java.util.HashMap;
import java.util.List;

public class StanfordEntityFinder
{
    private String[] serializedClassifierArray =
            {
                    "lib/stanford/classifiers/english.all.3class.distsim.crf.ser.gz",
                    "lib/stanford/classifiers/english.conll.4class.distsim.crf.ser.gz",
                    "lib/stanford/classifiers/english.muc.7class.distsim.crf.ser.gz"
            };

    private HashMap<String, String> annotations;

    public StanfordEntityFinder()
    {
        annotations = new HashMap();
    }

    public HashMap<String, String> anotate(String sentence)
    {
        for (String serializedClassifier : serializedClassifierArray)
        {
            AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
            List<Triple<String, Integer, Integer>> entities = classifier.classifyToCharacterOffsets(sentence);

            for (Triple<String, Integer, Integer> entity : entities)

            {
                String identifier = (entity.first);
                String element = (sentence.substring(entity.second, entity.third));

                annotations.put(identifier, element);
            }
        }

        return annotations;
    }
}
