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
import java.util.Iterator;
import java.util.Properties;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Procheta
 */
public class SimScoreCalCulationTfIdf {

    String queryfile;
    ArrayList<String> queryList;
    String scoreFile;
    HashMap<Integer, ArrayList<String>> queryTopDocList;
    String tfIdfScoreFile;
    double alphaValue;
    double[][] scorematrix;
    HashMap<Integer, HashMap<String, Double>> queryScore;
    String clValue;
    public SimScoreCalCulationTfIdf(String propFile,String alphaValue,String clvalue) throws FileNotFoundException, IOException {

        Properties prop = new Properties();
        prop.load(new FileReader(new File(propFile)));
        queryfile = prop.getProperty("query");
        scoreFile = prop.getProperty("score");
        this.alphaValue = Double.parseDouble(alphaValue);
       clValue = clvalue;  
    //    System.out.println(prop.getProperty("tfIdfFile"));
        //tfIdfScoreFile = prop.getProperty("tfIdfFile")+clValue+".txt";
          tfIdfScoreFile = prop.getProperty("tfIdfFile");
    //   System.out.println(tfIdfScoreFile); 
         queryList = new ArrayList<>();
        queryScore = new Query().readwordBasedQueryFile(prop.getProperty("queryVecFile"));
        getQueries();
       clValue = clvalue; 
        readTfIdfScore();
        
    }

    public void getQueries() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(new File(queryfile));
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();

        while (line != null) {
            queryList.add(line);
            line = br.readLine();
        }
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

    public double calSemanticSim(int querynumber1, int querynumber2) throws IOException, MalformedURLException, ParseException {
        Query q = new Query();
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

    public void readTfIdfScore() throws FileNotFoundException, IOException {

        FileReader fr = new FileReader(new File(tfIdfScoreFile));
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        scorematrix = new double[1424][1424];
        int i = 0;
        while (line != null) {
            String st[] = line.split(" ");
            for (int j = 0; j < st.length; j++) {
                scorematrix[i][j] = Double.parseDouble(st[j]);
            }
            i++;
            line = br.readLine();
        }

    }

    public void writeSimScore() throws IOException, MalformedURLException, ParseException {
    //    FileWriter fw = new FileWriter(new File(scoreFile)+"_clValue_"+clValue+"_alpha_"+new Double(alphaValue).toString()+".txt");
         FileWriter fw = new FileWriter(new File(scoreFile+new Double(alphaValue).toString()+".txt")); 
        BufferedWriter bw = new BufferedWriter(fw);

        int numQueries = queryList.size();
        for (int i = 0; i < numQueries; i++) {
            String query1 = queryList.get(i);
            for (int j = 0; j < numQueries; j++) {
                String query2 = queryList.get(j);
                double sim = alphaValue * scorematrix[i][j] + (1 - alphaValue) * calSemanticSim(i, j);
                bw.write(new Double(sim).toString());
                bw.write(" ");
            }
           // System.out.println("entered " + i);
            bw.newLine();
        }
        bw.close();

    }
     public void writeSimScoreWithLexical() throws IOException, MalformedURLException, ParseException {
        FileWriter fw = new FileWriter(new File(scoreFile)+new Double(alphaValue).toString()+".txt");
        BufferedWriter bw = new BufferedWriter(fw);

        int numQueries = queryList.size();
        for (int i = 0; i < numQueries; i++) {
            String query1 = queryList.get(i);
            for (int j = 0; j < numQueries; j++) {
                String query2 = queryList.get(j);
                double sim = alphaValue * (scorematrix[i][j] + calLexicalScore(query1, query2))/2 + (1 - alphaValue) * calSemanticSim(i, j);
                bw.write(new Double(sim).toString());
                bw.write(" ");
            }
            //System.out.println("entered " + i);
            bw.newLine();
        }
        bw.close();

    }
    public static void main(String[] args) throws Exception {

        SimScoreCalCulationTfIdf sm = new SimScoreCalCulationTfIdf("/home/procheta/NetBeansProjects/AOLTaskExtraction/src/LucheseImplementation/init.properties","0","0");
        sm.writeSimScore();

    }

}
