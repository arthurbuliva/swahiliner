/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mwanafunzi;

import opennlp.ApacheOpenNLPEntityFinder;
import opennlp.StanfordEntityFinder;

/**
 * @author Arthur Buliva
 */
public class Test
{
    public static void main(String[] args)
    {
        String text = "Mbunge wa jimbo la Monduli kwa Tiketi ya Chama cha Demokrasia na Maendeleo (CHADEMA), Julius Kalanga Laizer amejiuzulu na kujiunga na CCM kwa kile alichoeleza kuwa ni kuunga mkono juhudi za Rais Magufuli.";
//        String searchExpression = "(?i)(?<!\\w(?=\\w))(" + Pattern.quote("log") +
//                ")(?!(?<=\\w)\\w)";

//        System.out.println( text.replaceAll(searchExpression, "not digital"));

        System.out.println(new StanfordEntityFinder().anotate(text));
        System.out.println(new ApacheOpenNLPEntityFinder().getEntities(text));

    }
}
