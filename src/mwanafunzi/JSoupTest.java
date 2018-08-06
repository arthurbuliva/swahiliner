package mwanafunzi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JSoupTest
{
    private static final Logger LOGGER = Logger.getLogger("global");

    private File denestedCorpusFile;
    private FileWriter fstream;
    private BufferedWriter writer;

    public JSoupTest()
    {
//        try
//        {
//            denestedCorpusFile = new File("DenestedSwahili.txt");
//            fstream = new FileWriter(denestedCorpusFile.getCanonicalPath(), true);
//            writer = new BufferedWriter(fstream);
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//        }
//
//
//        try (BufferedReader br = new BufferedReader(new FileReader("Swahili.txt")))
//        {
//            String line;
//
//            while ((line = br.readLine()) != null)
//            {
//                writer.write(cleanNestedTags(line));
//                writer.newLine();
//
//                writer.flush();
//            }
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//        }
    }

    public static void main(String[] args)
    {
        String text = "Mwaka wa <START:DATE> 2008 <END> umetangazwa kuwa <START:ORGANIZATION> Mwaka wa Lugha wa <START:PERSON> Kimataifa <END> <END> .";

        JSoupTest test = new JSoupTest();

        System.out.println(text);
        System.out.println("+++++++++++++++++++");
        System.out.println(test.cleanNestedTags(text));
    }

    public String cleanNestedTags(String nested)
    {
//        String html = "<ORGANIZATION>*<ORGANIZATION>NSYNC</ORGANIZATION></ORGANIZATION> ni albamu " +
//                "iliyotolewa na <PERSON>Kundi</PERSON> <LOCATION>La</LOCATION> wanamuziki " +
//                "<LOCATION>La</LOCATION> '<MISC>N</MISC> Sync, iliyotolewa mnamo " +
//                "<DATE>24 <DATE>Machi</DATE></DATE> <DATE>1998</DATE> nchini " +
//                "<LOCATION>Marekani</LOCATION>.Ilifika <ORGANIZATION>#</ORGANIZATION>2 " +
//                "kwenye chati na imeuza zaidi ya nakala milioni 10 nchini <LOCATION>Marekani</LOCATION>," +
//                " na kuthibitishwa 10x platinum na kutuzwa <ORGANIZATION>RIAA</ORGANIZATION> " +
//                "<ORGANIZATION>Diamond</ORGANIZATION> <ORGANIZATION>Award</ORGANIZATION>.\n";
//
////
//        System.out.println(html.replaceAll("<DATE>24 <DATE>Machi</DATE></DATE>", "<DATE> 24 Machi </DATE>"));
//
//        System.exit(0);

        String denested = nested;

        Document doc = Jsoup.parse(nested);
        doc.outputSettings().indentAmount(0).prettyPrint(false);

        Elements elements = doc.body().select("*");


        for (Element element : elements)
        {

//            System.out.println(element.ownText());
//            if(element.ownText().equals("Machi"))
            {
//                System.out.println(element.tagName());
//                System.out.println(element.ownText());
//                System.out.println(element.parent());
//                System.out.println(element.parent().tagName());

                if (element.tagName().equals(element.parent().tagName()))
                {

                    // Nested loop
//                    System.out.println(element.ownText());
//                    System.out.println(element.parent().tagName());

//                    System.out.println("=========================");
//                    System.out.println(element.parent().toString().replaceAll(element.parent().tagName(), element.parent().tagName().toUpperCase()));
//                    System.out.println("=========================");

                    String search = element.parent().toString().
                            replaceAll(element.parent().tagName(), element.parent().tagName().toUpperCase());

                    String replacement = String.format(
                            "<%s> %s </%s>",
                            element.parent().tagName().toUpperCase(),
                            element.parent().text(),
                            element.parent().tagName().toUpperCase()
                    );

                    Pattern pattern = Pattern.compile(Pattern.quote(search));
                    Matcher matcher = pattern.matcher(nested);

                    if (matcher.find())
                    {
                        LOGGER.log(Level.INFO, String.format(
                                "Replacing '%s' with '%s'",
                                search, replacement
                        ));
                        try
                        {
                            denested = denested.replace(search, replacement);
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }


                }
                else if (
                        element.parent().toString().toUpperCase().startsWith("<START:")
                        )
                {
//                    LOGGER.log(Level.INFO, String.format(
//                            "Parent element tag name '%s'",
//                            element.parent().tagName().toUpperCase()
//                    ));
//
//                    LOGGER.log(Level.INFO, String.format(
//                            "Element tag name '%s'",
//                            element.tagName().toUpperCase()
//                    ));

                    String replacement = String.format(
                            "<%s> %s </%s>",
                            element.parent().tagName().toUpperCase(),
                            element.parent().text(),
                            element.parent().tagName().toUpperCase()
                    );

                    System.out.println(replacement);

                    String search = element.parent().toString().
                            replaceAll(element.parent().tagName(), element.parent().tagName().toUpperCase());

                    LOGGER.log(Level.INFO, String.format(
                            "Replacing '%s' with '%s'",
                            search, replacement
                    ));

                    Pattern pattern = Pattern.compile(Pattern.quote(search));
                    Matcher matcher = pattern.matcher(nested);

                    if (matcher.find())
                    {
                        LOGGER.log(Level.INFO, String.format(
                                "Replacing '%s' with '%s'",
                                search, replacement
                        ));
                        try
                        {
                            denested = denested.replace(search, replacement);
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }


                }


            }
        }

//        System.out.println(html);
        return denested;

//        System.out.println(doc.select("<START:DATE>"));
//        System.out.println(doc.body().getElementsContainingText("Machi"));
//        System.out.println(doc);
    }
}
