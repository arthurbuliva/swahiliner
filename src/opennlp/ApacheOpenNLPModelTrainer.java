package opennlp;

import opennlp.tools.namefind.*;
import opennlp.tools.util.*;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * This is the actual trainer using the annotated text obtained from Mwanafunzi.java
 */
public class ApacheOpenNLPModelTrainer
{
    public static void main(String[] args)
    {
        ApacheOpenNLPModelTrainer modeller = new ApacheOpenNLPModelTrainer();
        modeller.trainModel();
    }

    public void trainModel()
    {
        try
        {
            InputStreamFactory in = new MarkableFileInputStreamFactory(
                    new File("Swahili.txt")
            );
            ObjectStream sampleStream = new NameSampleDataStream(
                    new PlainTextByLineStream(in, StandardCharsets.UTF_8));

            TrainingParameters params = new TrainingParameters();
//            params.put(TrainingParameters.ITERATIONS_PARAM, 70);
//            params.put(TrainingParameters.CUTOFF_PARAM, 1);

            TokenNameFinderModel nameFinderModel =
                    NameFinderME.train(
                            "sw", null, sampleStream, params,
                            TokenNameFinderFactory.create(
                                    null, null,
                                    Collections.emptyMap(), new BioCodec()
                            )
                    );


            File output = new File("ner-custom-model.bin");
            FileOutputStream outputStream = new FileOutputStream(output);
            nameFinderModel.serialize(outputStream);


            // testing the model and printing the types it found in the input sentence
            TokenNameFinder nameFinder = new NameFinderME(nameFinderModel);

            String[] testSentence =
                    {
                            "Umoja wa Mataifa ni muungano wa nchi 70 duniani. Nchi ya Kenya ni mwanachama"
                    };

            System.out.println("Finding types in the test sentence..");

            Span[] names = nameFinder.find(testSentence);

            for (Span name : names)
            {
                String personName = "";
                for (int i = name.getStart(); i < name.getEnd(); i++)
                {
                    personName += testSentence[i] + " ";
                }
                System.out.println(name.getType() + " : " + personName + "\t [probability=" + name.getProb() + "]");
            }
        }

        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
