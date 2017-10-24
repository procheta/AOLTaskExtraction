/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LucheseImplementation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;
import org.json.simple.parser.ParseException;
import java.util.Iterator;

/**
 *
 * @author procheta
 */
public class SimScoreCalculation {

    String queryfile;
    ArrayList<String> queryList;
    String scoreFile;
    HashMap<Integer, ArrayList<String>> queryTopDocList;
    double alphaValue;
    String mode;
    HashMap<Integer, HashMap<String, Double>> queryScore;
    String queryloadMode;
    HashMap<String, Integer> queryNumScoreMap;

    public SimScoreCalculation(String propFile, String alpha, String scoreFile) throws FileNotFoundException, IOException {

        Properties prop = new Properties();
        prop.load(new FileReader(new File(propFile)));
        queryloadMode = prop.getProperty("queryLoadMode");

        queryfile = prop.getProperty("query");
        this.scoreFile = scoreFile;
        alphaValue = Double.parseDouble(alpha);
        queryList = new ArrayList<>();
        if (queryloadMode.equals("batch")) {
            System.out.println(prop.getProperty("sessFile"));
            queryNumScoreMap = new Query().loadQuerySessionMap(prop.getProperty("sessFile"));
        } else {
            getQueries();
        }
        mode = prop.getProperty("mode");
        if (mode.equals("query")) {
            queryTopDocList = new Query().readQueryTopDocList();
        } else {

            queryScore = new Query().readwordBasedQueryFile(prop.getProperty("queryVecFile"));

        }
        queryTopDocList = new Query().readQueryTopDocList();

    }

    public void getQueries() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(new File(queryfile));
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        queryList = new ArrayList<>();

        while (line != null) {
            if (!queryloadMode.equals("batch")) {
                queryList.add(line);
            } else {
               // line = line.substring(0, line.indexOf(" ."));
                if (line.startsWith(" ")) {
                    line = line.substring(0, line.indexOf(" ") + 1);
                }
                queryList.add(line);
            }
            line = br.readLine();
        }
    }

    public double calDocLength(HashMap<String, Double> queryScore) {

        double length = 0;
        Iterator it = queryScore.keySet().iterator();

        while (it.hasNext()) {
            String st = (String) it.next();
            double score = queryScore.get(st);
            length += score * score;
        }
        return Math.sqrt(length);
    }

    public double calLexicalScore(String query1, String query2) {
        Query q1 = new Query();
        CharSequence seq1 = new String(query1);
        CharSequence seq2 = new String(query2);
        double jacc = q1.computeJaccardCoefficient(query1, query2);
        double levenestine = q1.computeLevenshteinDistance(seq1, seq2);
        double contentSimilarity = (jacc + levenestine) / 2;

        return contentSimilarity;
    }

    public double calSemanticSim(int querynumber1, int querynumber2) throws IOException, MalformedURLException, ParseException {
        Query q = new Query();
        if (mode.equals("query")) {
            ArrayList<String> doclist1 = queryTopDocList.get(querynumber1);
            ArrayList<String> doclist2 = queryTopDocList.get(querynumber2);

            int initialSize = doclist1.size();
            doclist1.removeAll(doclist2);
            int intersecSize = initialSize - doclist1.size();
            if (doclist1.size() != 0 && doclist2.size() != 0) {
                if (querynumber1 != querynumber2) {
                    return intersecSize / (double) (initialSize + doclist2.size() - intersecSize);
                } else {
                    return 1;
                }
            } else {
                return 0;
            }
        } else {
            HashMap<String, Double> queryScore1 = queryScore.get(querynumber1);
            HashMap<String, Double> queryScore2 = queryScore.get(querynumber2);
            if (queryScore1.size() == 0 || queryScore2.size() == 0) {
                return 0;
            }

            double simScore = 0;
            Iterator it = queryScore1.keySet().iterator();
            while (it.hasNext()) {
                String st = (String) it.next();
                if (queryScore2.containsKey(st)) {
                    simScore += queryScore1.get(st) * queryScore2.get(st);
                }
            }
            double length1 = calDocLength(queryScore1);
            double length2 = calDocLength(queryScore2);
            return simScore / (length1 * length2);
        }

    }

    public void writeSimScore() throws IOException, MalformedURLException, ParseException {
        FileWriter fw = new FileWriter(new File(scoreFile));
        BufferedWriter bw = new BufferedWriter(fw);
        //  System.out.println(queryNumScoreMap);
        int numQueries = queryList.size();
        for (int i = 0; i < numQueries; i++) {
            String query1 = queryList.get(i);
            for (int j = 0; j < numQueries; j++) {
                String query2 = queryList.get(j);
                double sim = 0;
                if (queryloadMode.equals("batch")) {
                    // System.out.println(query1);
                    try {
                        int q1 = queryNumScoreMap.get(query1);
                        int q2 = queryNumScoreMap.get(query2);
                        sim = alphaValue * calLexicalScore(query1, query2) + (1 - alphaValue) * calSemanticSim(q1, q2);
                    } catch (Exception e) {
                        if (queryNumScoreMap.get(query2) == null) {
                            System.out.println(query2);
                        } else {
                            System.out.println(query1);
                        }
                        //  System.out.println(query2);
                    }
                } else {
                    sim = alphaValue * calLexicalScore(query1, query2) + (1 - alphaValue) * calSemanticSim(i, j);
                }
                bw.write(new Double(sim).toString());
                bw.write(" ");
            }
            //  System.out.println("entered " + i);
            bw.newLine();
        }
        bw.close();

    }

    public static void main(String[] args) throws Exception {

        SimScoreCalculation sm = new SimScoreCalculation("/home/procheta/NetBeansProjects/AOLTaskExtraction/src/LucheseImplementation/init.properties", "0.6", "/home/procheta/sessScore/");
        if (sm.queryloadMode.equals("single")) {
            sm.writeSimScore();
        } else {
            String s = sm.queryfile;
            String s1 = sm.scoreFile;
            File dir = new File(sm.queryfile);
            File[] directoryListing = dir.listFiles();
            for (File f : directoryListing) {
                sm.queryfile = s + "/" + f.getName();
                // System.out.println(sm.queryfile);
                sm.getQueries();
                sm.scoreFile = s1 + "/" + f.getName();
                sm.writeSimScore();
            }
        }

    }

}
