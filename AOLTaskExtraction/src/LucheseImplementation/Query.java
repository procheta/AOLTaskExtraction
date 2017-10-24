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
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import org.apache.tika.detect.AutoDetectReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Procheta
 */
public class Query {

    String query;

    public ArrayList<String> computeTrigramFeature(String line) {

        ArrayList<String> trigramArray = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(line);

        while (st.hasMoreElements()) {

            String word = st.nextToken();
            char cArray[] = word.toCharArray();

            for (int i = 0; i <= cArray.length - 3; i++) {
                String trigram = Character.toString(cArray[i]) + Character.toString(cArray[i + 1]) + Character.toString(cArray[i + 2]);
                trigramArray.add(trigram);
            }
        }
        return trigramArray;
    }

    public double computeLevenshteinDistance(CharSequence lhs, CharSequence rhs) {
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;

        // the array of distances                                                       
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        // initial cost of skipping prefix in String s0                                 
        for (int i = 0; i < len0; i++) {
            cost[i] = i;
        }

        // dynamically computing the array of distances                                  
        // transformation cost for each letter in s1                                    
        for (int j = 1; j < len1; j++) {
            // initial cost of skipping prefix in String s1                             
            newcost[0] = j;

            // transformation cost for each letter in s0                                
            for (int i = 1; i < len0; i++) {
                // matching current letters in both strings                             
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

                // computing cost for each transformation                               
                int cost_replace = cost[i - 1] + match;
                int cost_insert = cost[i] + 1;
                int cost_delete = newcost[i - 1] + 1;

                // keep minimum cost                                                    
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }

            // swap cost/newcost arrays                                                 
            int[] swap = cost;
            cost = newcost;
            newcost = swap;
        }

        double maxLength = 0;
        if (len0 > len1) {
            maxLength = len0;
        } else {
            maxLength = len1;
        }
        // the distance is the cost for transforming all letters in both strings        
        return (1 - cost[len0 - 1] / maxLength);
    }

    public HashMap<String, Double> perLineProcess(String line) {

        HashMap<String, Double> scoreMap = new HashMap<>();
        String st[] = line.split(" ");
        for (int i = 0; i < st.length; i++) {
            // System.out.println("here"+st[i]+st.length+" "+i); 
            String tokens[] = st[i].split(":");
            try {
                scoreMap.put(tokens[0], Double.parseDouble(tokens[1]));
            } catch (Exception e) {
            }
        }
        return scoreMap;
    }

    public ArrayList<Character> computeCharsequence(String line) {
        ArrayList<Character> charSequence = new ArrayList<Character>();
        StringTokenizer st = new StringTokenizer(line);

        while (st.hasMoreElements()) {
            String word = st.nextToken();
            char[] cArray = word.toCharArray();
            for (int i = 0; i < cArray.length; i++) {
                charSequence.add(cArray[i]);
            }
        }
        return charSequence;
    }

    public double computeJaccardCoefficient(String line1, String line2) {
        ArrayList<String> trigramseq1 = computeTrigramFeature(line1);
        ArrayList<String> trigramseq2 = computeTrigramFeature(line2);

        double intersecSize = 0;
        double unionSize = 0;

        for (int i = 0; i < trigramseq1.size(); i++) {
            if (trigramseq2.contains(trigramseq1.get(i))) {
                intersecSize++;
            }
        }
        unionSize = trigramseq1.size() + trigramseq2.size() - intersecSize;

        return intersecSize / unionSize;
    }

    public ArrayList<String> getTopDocsForTerm(String term) throws MalformedURLException, IOException, ParseException {

        ArrayList<String> docList = new ArrayList<>();

        try {

            URL url = new URL("http://clueweb.adaptcentre.ie/WebSearcher/search?query=" + term);
            // System.out.println(url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(55 * 1000);
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            //return stringBuilder.toString();
            docList = extractDocIds(stringBuilder.toString());

        } catch (Exception e) {
        }
        //System.out.println(stringBuilder.toString());
        return docList;
    }

    public ArrayList<String> extractDocIds(String jsonString) throws ParseException, IOException {
        ArrayList<String> docIdList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArr = (JSONArray) parser.parse(jsonString);
            // System.out.println(jsonArr.size());

            for (int i = 0; i < jsonArr.size(); i++) {

                JSONArray job = (JSONArray) jsonArr.get(i);

                String st1 = ((Object) job.get(0)).toString();
                String st2 = st1.replace("[", "");
                st2 = st2.replace("]", "");
                char d = '"';
                String st4 = st2.substring(st2.indexOf("id" + d), st2.length());
                String st7 = st4;
                try {
                    st4 = st4.substring(0, st4.indexOf(","));
                    String st3[] = st2.split(",");
                    String st5[] = st4.split(":");
                    st5[1] = st5[1].replace("" + d, "");
                    docIdList.add(st5[1]);
                    // System.out.println(st5[1]);
                    //  arr.add(st4);
                } catch (Exception e) {
                    st7 = st7.substring(st7.indexOf(",") + 1, st7.length());
                    String st8 = st7.substring(0, st7.indexOf(","));
                    String st5[] = st8.split(":");
                    //System.out.println(st8);
                    st5[1] = st5[1].replace("" + d, "");
                    docIdList.add(st5[1]);
                    // arr.add(st4);
                }

            }

        } catch (Exception e) {
            //System.out.println("");
        }
        return docIdList;
    }

    public HashMap<String, Integer> loadQuerySessionMap(String fileName) throws FileNotFoundException, IOException {

        HashMap<String, Integer> querySessMap = new HashMap<>();

        FileReader fr = new FileReader(new File(fileName));
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        int i = 0;
        while (line != null) {
            String st[] = line.split("\t");
            querySessMap.put(st[4], i++);
            line = br.readLine();
        }

        return querySessMap;
    }

    public ArrayList<String> getallTopDocForQuery(String query) throws IOException, MalformedURLException, ParseException {
        HashSet<String> docIdeSet = new HashSet<String>();
        StringTokenizer st = new StringTokenizer(query);
        while (st.hasMoreElements()) {
            String token = st.nextToken();
            ArrayList<String> docList = getTopDocsForTerm(token);
            docIdeSet.addAll(docList);
        }

        ArrayList<String> finalDocList = new ArrayList<>();
        finalDocList.addAll(docIdeSet);
        return finalDocList;

    }

    public void writeDocIds() throws FileNotFoundException, IOException, MalformedURLException, ParseException {
        FileReader fr = new FileReader(new File("/home/procheta/queryFile.txt"));
        BufferedReader br = new BufferedReader(fr);

        FileWriter fw = new FileWriter(new File("/home/procheta/scoreNew.txt"));
        BufferedWriter bw = new BufferedWriter(fw);

        String line = br.readLine();
        int count = 0;

        while (line != null) {
            System.out.println(count++);
            ArrayList<String> docList = getTopDocsForTerm(line);
            for (String s : docList) {

                bw.write(s + " ");
            }
            bw.newLine();
            line = br.readLine();
        }
        bw.close();
    }

    public HashMap<Integer, ArrayList<String>> readQueryTopDocList() throws FileNotFoundException, IOException {
        HashMap<Integer, ArrayList<String>> queryTopDocList = new HashMap<>();

        FileReader fr = new FileReader(new File("/home/procheta/scoreNew.txt"));
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();

        int i = 0;
        while (line != null) {

            ArrayList<String> docList = new ArrayList<>();
            try {
                String st[] = line.split(" ");
                for (int j = 0; j < st.length; j++) {
                    docList.add(st[j]);
                }
            } catch (Exception e) {
            }
            queryTopDocList.put(i++, docList);
            line = br.readLine();
        }
        return queryTopDocList;
    }

    public HashMap<Integer, HashMap<String, Double>> readwordBasedQueryFile(String fileName) throws FileNotFoundException, IOException {

        FileReader fr = new FileReader(new File(fileName));
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        int i = 0;
        HashMap<Integer, HashMap<String, Double>> queryScoreMap = new HashMap<>();

        while (line != null) {
            if (!line.equals("")) {
                HashMap<String, Double> scoreMap = perLineProcess(line);
                queryScoreMap.put(i++, scoreMap);
            } else {
                HashMap<String, Double> scoreMap = new HashMap<>();
                queryScoreMap.put(i++, scoreMap);
            }
            line = br.readLine();
        }
        return queryScoreMap;
    }

    public void writeSeesionIds() throws IOException {

        FileWriter fw = new FileWriter(new File("/home/procheta/sessLabel.txt"));
        BufferedWriter bw = new BufferedWriter(fw);

        FileReader fr = new FileReader(new File("/home/procheta/all-task-processed.txt"));
        BufferedReader br = new BufferedReader(fr);
        int i = 0;
        String line = br.readLine();

        while (line != null) {

            String st[] = line.split("\t");
            bw.write(st[0] + st[1] + "\t" + new Integer(i++).toString());
            bw.newLine();
            line = br.readLine();
        }
        bw.close();
    }

    public void writeQuerySequence() throws IOException {
        FileWriter fw = new FileWriter(new File("/home/procheta/sessSquence.txt"));
        BufferedWriter bw = new BufferedWriter(fw);

        FileReader fr = new FileReader(new File("/home/procheta/all-task-processed.txt"));
        BufferedReader br = new BufferedReader(fr);
        int i = 0;
        String line = br.readLine();

        while (line != null) {

            String st[] = line.split("\t");
            bw.write(st[3] + "\t" + new Integer(i++).toString());
            bw.newLine();
            line = br.readLine();
        }
        bw.close();

    }

    public static void main(String[] args) throws IOException, MalformedURLException, ParseException {
        Query q = new Query();
        q.writeQuerySequence();
        //   System.out.println(q.computeCharsequence("garden botanika com"));
        //   System.out.println(q.computeTrigramFeature("garden botanika com"));
        // CharSequence seq1 = new String("garden botanika com");
        // CharSequence seq2 = new String("disnei direct com");

        // System.out.println(q.computeLevenshteinDistance(seq1, seq2));
        //  System.out.println(q.computeJaccardCoefficient("garden botanika com", "disnei direct com"));
        // q.getTopDocsForTerm("dog");
        //q.writeDocIds();
    }

}
