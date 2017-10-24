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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

/**
 *
 * @author Procheta
 */
public class WeightedClustering {

    HashMap<Integer, ArrayList<Double>> simScoreList;
    int numQueries;
    HashSet<Integer> visited;
    ArrayList<ArrayList<Integer>> graph;
    ArrayList<ArrayList<Integer>> clusterlist;
    double itaValue;
    String scoreFile;
    String clusterFile;
    double alphaValue;
    String queryloadMode;
    String queryFile;

    public WeightedClustering(String propFilename, double itavalue, double alphavalue) throws IOException {
        Properties prop = new Properties();
        prop.load(new FileReader(propFilename));
        scoreFile = prop.getProperty("scoreFile");
        queryloadMode = prop.getProperty("queryLoadMode");
        if (queryloadMode.equals("single")) {
            numQueries = Integer.parseInt(prop.getProperty("noq"));
        }
        {
            queryFile = prop.getProperty("query");
        }
        //else
        //     numQueries=getqueryNum();
        visited = new HashSet<>();
        this.itaValue = itavalue;
        clusterFile = prop.getProperty("clusterFile");
        this.alphaValue = alphavalue;
        if (queryloadMode.equals("single")) {
            readMatrix();
        }
    }

    public int getqueryNum() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(new File(queryFile));
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        int count = 0;
        while (line != null) {
            count++;
            line = br.readLine();
        }

        return count;
    }

    public void findNeighbours(int j, ArrayList<Integer> cl) {

        ArrayList<Integer> clist = graph.get(j);
        int oneHopNeighbourSIze = clist.size();
        for (int i = 0; i < oneHopNeighbourSIze; i++) {
            int nodeNum = clist.get(i);
            if (!visited.contains(nodeNum)) {
                cl.add(nodeNum);
                visited.add(nodeNum);
                findNeighbours(nodeNum, cl);
            }
        }

    }

    public void readMatrix() throws FileNotFoundException, IOException {

        FileReader fr = new FileReader(new File(scoreFile+new Double(alphaValue).toString()+".txt"));
        //FileReader fr = new FileReader(new File(scoreFile)); 
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        simScoreList = new HashMap<>();
        int i = 0;

        while (line != null) {
            String st[] = line.split(" ");
            ArrayList<Double> dlist = new ArrayList<>();
            for (int j = 0; j < st.length; j++) {
                dlist.add(Double.parseDouble(st[j]));
            }
            simScoreList.put(i++, dlist);
            line = br.readLine();
        }

    }

    public void constructGarph() throws IOException {

        if (queryloadMode.equals("batch")) {
            numQueries = getqueryNum();
            readMatrix();
        }
        graph = new ArrayList<>();
        visited = new HashSet<>();
        int clusterNuber = 0;
        System.out.println("ita" + numQueries);
        System.out.println(simScoreList.size());
        System.out.println(queryFile);
        for (int i = 0; i < numQueries; i++) {
            ArrayList<Double> dlist = simScoreList.get(i);
            ArrayList<Integer> clist = new ArrayList<>();
          // System.out.println(dlist); 
          for (int j = 0; j < numQueries; j++) {
                if (dlist.get(j) >= itaValue) {
                    clist.add(j);
                }
           
            }
           // System.out.println(clist);
            if (clist.size() == 0) {
                clist.add(i);
            }
            graph.add(clist);
        }

    }

    public void cluster() throws IOException {

        constructGarph();
       // System.out.println(graph);
        clusterlist = new ArrayList<>();
        for (int i = 0; i < numQueries; i++) {
            if (!visited.contains(i)) {
                ArrayList<Integer> cll = new ArrayList<>();
                visited.add(i);
                cll.add(i);
                findNeighbours(i, cll);
                clusterlist.add(cll);
            }
        }
       /* System.out.println(clusterlist);

        FileReader fr = new FileReader(new File("/home/procheta/queryFileNew.txt"));
        BufferedReader br = new BufferedReader(fr);
         ArrayList<String> q = new ArrayList<>();
        String line = br.readLine();
         while(line != null){
         q.add(line);
          
          line = br.readLine();
        }
        for(int i = 0; i < clusterlist.size(); i++){
    	ArrayList<Integer> a = clusterlist.get(i);
        for(int j = 0; j < a.size(); j++){
		System.out.print(q.get(a.get(j))+",");
	} 
		System.out.println("@@@@@@");
        }


        System.out.println(clusterlist.size());*/
    }

    public void writeClusterIds() throws IOException {

        // FileWriter fw = new FileWriter(new File(clusterFile + new Double(itaValue).toString() + "_alpha_" + new Double(alphaValue).toString() + ".txt"));
        FileWriter fw = new FileWriter(new File(clusterFile));

        BufferedWriter bw = new BufferedWriter(fw);

        HashMap<Integer, Integer> clusterIdmap = new HashMap<>();

        for (int i = 0; i < clusterlist.size(); i++) {
            ArrayList<Integer> clist = clusterlist.get(i);
            for (int i1 : clist) {
                clusterIdmap.put(i1, i);
            }

        }
        System.out.println(clusterIdmap.size());

        for (int i = 0; i < numQueries; i++) {
            bw.write(clusterIdmap.get(i).toString());
            bw.newLine();
        }
        bw.close();
    }

    public static void main(String[] args) throws IOException {
        // System.out.println(args[2]);
        WeightedClustering wc = new WeightedClustering(args[0],Double.parseDouble( args[1]),Double.parseDouble(args[2])); 
        if (wc.queryloadMode.equals("single")) {
            wc.cluster();
            wc.writeClusterIds();
        } else {
            String s = wc.scoreFile;
            String s1 = wc.clusterFile;
            String s2 = wc.queryFile;
            System.out.println(wc.queryFile);
            File dir = new File(wc.queryFile);
            File[] directoryListing = dir.listFiles();
            for (File f : directoryListing) {
                wc.queryFile = s2 + f.getName();
                wc.scoreFile = s + f.getName();
                wc.cluster();
                wc.clusterFile = s1 + f.getName();
                wc.writeClusterIds();
            }
        }
    }

}
