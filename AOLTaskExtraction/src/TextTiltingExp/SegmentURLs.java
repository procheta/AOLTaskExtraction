/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TextTiltingExp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author Procheta
 */
public class SegmentURLs {

    String mode;

    public SegmentURLs(String mode) {
        this.mode = mode;

    }

    public void createSegments(String fileName) throws FileNotFoundException, IOException {

        ArrayList<String> queryList = new ArrayList<String>();
        FileReader fr = null;

        if (mode.equals("time_split")) {
            fr = new FileReader(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/sessData/" + fileName));
        } else {
            fr = new FileReader(new File(fileName));
        }
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();

        while (line != null) {
            queryList.add(line);
            line = br.readLine();
        }

        ArrayList<ArrayList<String>> clusterList = new ArrayList<>();

        ArrayList<String> cluster = new ArrayList<>();
        String prevQuery = queryList.get(0);
        String words[] = prevQuery.split(" ");
        HashSet<String> wordList = new HashSet<String>();
        for (String i : words) {
            wordList.add(i);
        }
        int checkFlag = 0;
        for (String query : queryList) {
            // System.out.println(query);
            int flag = 0;
            String nextWords[] = query.split(" ");
            for (String s : nextWords) {
                if (wordList.contains(s)) {
                    flag = 1;
                }
            }

            if (flag == 1) {
                cluster.add(query);

            } else {
                //  if(cluster.size() == 0)
                //    cluster.add(prevQuery);
                checkFlag = 1;
                clusterList.add(cluster);
                cluster = new ArrayList<>();
                cluster.add(query);
            }

            wordList = new HashSet<String>();
            for (String i : nextWords) {
                wordList.add(i);
            }
            prevQuery = query;
            // System.out.println(query);
        }
        //  if(checkFlag == 0)
        clusterList.add(cluster);
        System.out.println(clusterList.size());
       // System.out.println(clusterList);
        writeClusterFile(clusterList, fileName);

    }

    public void writeClusterFile(ArrayList<ArrayList<String>> clusterList, String filename) throws IOException {

        FileWriter fw = null;
        if(mode.equals("time_split"))
        fw = new FileWriter(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/sessionOutputLabel/" + filename));
       else
           fw = new FileWriter(new File(filename));
        
        BufferedWriter bw = new BufferedWriter(fw);
        Integer i = 0;

        for (ArrayList<String> cluster : clusterList) {
            for (String q : cluster) {
                bw.write(i.toString());
                bw.newLine();
            }
            i++;

        }
        bw.close();
    }

    public static void main(String[] args) throws IOException {
        SegmentURLs sgl = new SegmentURLs("wholeData");
        if (sgl.mode.equals("time_split")) {
            File[] files = new File("C:/Users/Procheta//Documents/ResearchData/AOLTaskData/sessData/").listFiles();
            for (File file : files) {
                System.out.println(file.getName());
                sgl.createSegments(file.getName());
            }
        } else {
                sgl.createSegments("C:/Users/Procheta//Documents/ResearchData/AOLTaskData/queryFile.txt");
        }
    }

}
