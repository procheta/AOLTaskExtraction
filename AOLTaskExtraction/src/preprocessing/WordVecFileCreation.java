/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 *
 * @author Procheta
 */
public class WordVecFileCreation {

    File originalFile;

    public WordVecFileCreation(String FileName) {

        originalFile = new File(FileName);
    }

    // function to extract a particular column from the ground truth file
    public void columnExtractionInputFile(String writeFileName) throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(originalFile);
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();

        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(writeFileName)));
        while (line != null) {
            String st[] = line.split("\t");
            //System.out.println(st[0]);
            String str = st[4];
            String writeStr = str;
            bw.write(writeStr);
            bw.newLine();
            line = br.readLine();
        }
        bw.close();
    }
    // create only query file

    public void prepareWordvecInputFile(String writeFileName) throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(originalFile);
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();

        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(writeFileName)));
        while (line != null) {
            String st[] = line.split("\t");
            String str = st[0];
            String writeStr = str;
            if (str.contains(".")) {
                String stArray[] = str.split("\\.");
                writeStr = stArray[0];
                for (int j = 1; j < stArray.length; j++) {
                    writeStr = writeStr + " " + stArray[j];
                }
            }
            bw.write(writeStr);
            bw.newLine();
            line = br.readLine();
        }
        bw.close();
    }
    //combining the output of cluto and true class labels

    public void createFinalFile(String fileName2, String writeFile) throws FileNotFoundException, IOException {

        FileReader fr = new FileReader(new File(fileName2));
        BufferedReader br = new BufferedReader(fr);

        BufferedReader br1 = new BufferedReader(new FileReader(originalFile));

        String line1 = br1.readLine();
        String line2 = br.readLine();
        System.out.println(line2);
        HashSet<String> stt = new HashSet<String>();
        FileWriter fw = new FileWriter(new File(writeFile));
        BufferedWriter bw = new BufferedWriter(fw);
        while (line1 != null) {
            //  String st[] = line1.split("\t");

            //  if (line2.equals("-2") || line2.equals("-1")) {
            //     Random rn = new Random();
            //    bw.write(rn.nextInt() % 6 + " " + st[1]);
            //  } else {
            // System.out.println(line1 + " " + st.length);
            //  bw.write(line2 + " " + st[0] + st[1] + st[2]);
            // stt.add(st[0] + st[1] + st[2]);
            bw.write(line2 + " " + line1);
            bw.newLine();
            line1 = br1.readLine();
            line2 = br.readLine();
        }
        bw.close();
        System.out.println(stt.size());
    }

    public void createFinalFileForSession(String fileName2, String fileName1, String writeFile) throws FileNotFoundException, IOException {

        FileReader fr = new FileReader(new File(fileName2));
        BufferedReader br = new BufferedReader(fr);

        BufferedReader br1 = new BufferedReader(new FileReader(fileName1));

        String line1 = br1.readLine();
        String line2 = br.readLine();
        System.out.println(line2);
        HashSet<String> stt = new HashSet<String>();
        FileWriter fw = new FileWriter(new File(writeFile));
        BufferedWriter bw = new BufferedWriter(fw);
        while (line1 != null) {
            //  String st[] = line1.split("\t");

            //  if (line2.equals("-2") || line2.equals("-1")) {
            //     Random rn = new Random();
            //    bw.write(rn.nextInt() % 6 + " " + st[1]);
            //  } else {
            // System.out.println(line1 + " " + st.length);
            //  bw.write(line2 + " " + st[0] + st[1] + st[2]);
            // stt.add(st[0] + st[1] + st[2]);
            bw.write(line2 + " " + line1);
            bw.newLine();
            line1 = br1.readLine();
            line2 = br.readLine();
        }
        bw.close();
        System.out.println(stt.size());
    }

    public void createSessionInputFile() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(originalFile);
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        String stArray[] = line.split("\t");
        String prevSessId = stArray[0] + stArray[1];

        FileWriter fw = new FileWriter(new File("C:/Users/Procheta//Documents/ResearchData/AOLTaskData/sessData/" + prevSessId + ".txt"));
        BufferedWriter bw = new BufferedWriter(fw);
        int sessCount = 0;
        int count = 0;
        while (line != null) {
            String st[] = line.split("\t");
            String sessId = st[0] + st[1];
            System.out.println(sessId);
            if (!prevSessId.equals(sessId)) {
                bw.close();
                fw = new FileWriter(new File("C:/Users/Procheta//Documents/ResearchData/AOLTaskData/sessData/" + sessId + ".txt"));
                bw = new BufferedWriter(fw);
                sessCount++;
                //System.out.println(count);
                count = 0;

                // System.out.println(prevSessId);
            }
            count++;
            bw.write(st[4]);
            bw.newLine();
            prevSessId = sessId;
            line = br.readLine();
        }
        bw.close();
        System.out.println("Session Count: " + sessCount);
    }

    public void createSessionWithOutput() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(originalFile);
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        String stArray[] = line.split("\t");
        String prevSessId = stArray[0] + stArray[1];

        FileWriter fw = new FileWriter(new File("/home/procheta/sessOutput/" + prevSessId + ".txt"));
        BufferedWriter bw = new BufferedWriter(fw);
        int sessCount = 0;
        while (line != null) {
            String st[] = line.split("\t");
            String sessId = st[0] + st[1];
            if (!prevSessId.equals(sessId)) {
                bw.close();
                fw = new FileWriter(new File("/home/procheta/sessOutput/" + sessId + ".txt"));
                bw = new BufferedWriter(fw);
                sessCount++;
            }
            bw.write(st[0] + st[1] + st[2]);
            bw.newLine();
            prevSessId = sessId;
            line = br.readLine();
        }
        bw.close();
        System.out.println("Session Count: " + sessCount);

    }

    public void createSessionData() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(originalFile);
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        String stArray[] = line.split("\t");
        String prevSessId = stArray[0] + stArray[1];

        FileWriter fw = new FileWriter(new File("/home/procheta/sessDataProcessed/" + prevSessId + ".txt"));
        BufferedWriter bw = new BufferedWriter(fw);
        int sessCount = 0;
        while (line != null) {
            String st[] = line.split("\t");
            String sessId = st[0] + st[1];
            if (!prevSessId.equals(sessId)) {
                bw.close();
                fw = new FileWriter(new File("/home/procheta/sessDataProcessed/" + sessId + ".txt"));
                bw = new BufferedWriter(fw);
                sessCount++;
            }
            bw.write(st[4]);
            bw.newLine();
            prevSessId = sessId;
            line = br.readLine();
        }
        bw.close();
        System.out.println("Session Count: " + sessCount);

    }

    public void createLabelForTimeSPlit() throws IOException {

        File[] files = new File("C:/Users/Procheta//Documents/ResearchData/AOLTaskData/sessionTrueLabel/").listFiles();
        for (File file : files) {
            FileWriter fw = new FileWriter(new File("C:/Users/Procheta//Documents/ResearchData/AOLTaskData/sessionLabel/" + file.getName()));
            BufferedWriter bw = new BufferedWriter(fw);
            FileReader fr = new FileReader(new File("C:/Users/Procheta//Documents/ResearchData/AOLTaskData/sessionTrueLabel/" + file.getName()));
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while (line != null) {
                bw.write("0");
                bw.newLine();
                line = br.readLine();
            }
            bw.close();
        }

    }

    public void allQueryProcess() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(originalFile);
        BufferedReader br = new BufferedReader(fr);

        FileWriter fw = new FileWriter(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/all-task-processed.txt"));
        BufferedWriter bw = new BufferedWriter(fw);

        String line = br.readLine();
        while (line != null) {
            String st[] = line.split("\t");

            String query = st[4];
            if (query.contains(".")) {
                String stArray[] = query.split("\\.");
                query = "";
                for (int j = 0; j < stArray.length; j++) {
                    query = query + " " + stArray[j];
                }
            }

            StringBuffer tokenizedContentBuff = new StringBuffer();
            Analyzer analyzer = new EnglishAnalyzer();
            TokenStream stream = analyzer.tokenStream("words", new StringReader(query));
            CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
            stream.reset();

            while (stream.incrementToken()) {
                String term = termAtt.toString();
                term = term.toLowerCase();
                tokenizedContentBuff.append(term).append(" ");
            }

            stream.end();
            stream.close();
            bw.write(st[0] + "\t" + st[1] + "\t" + st[2] + "\t" + st[3] + "\t" + tokenizedContentBuff.toString());
            //  bw.write(tokenizedContentBuff.toString());
            bw.newLine();
            line = br.readLine();
        }
        bw.close();
    }

    public void createTrueLabel() throws FileNotFoundException, IOException {

        FileReader fr = new FileReader(originalFile);
        BufferedReader br = new BufferedReader(fr);

        FileWriter fw = new FileWriter(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/all-task-trueLabel.txt"));
        BufferedWriter bw = new BufferedWriter(fw);

        String line = br.readLine();

        while (line != null) {
            String st[] = line.split("\t");
            String sessId = st[0] + st[1] + st[2];

            bw.write(sessId);
            bw.newLine();
            line = br.readLine();
        }
        bw.close();
    }

    public void rewriteVector() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/queryFile1.txt"));
        BufferedReader br = new BufferedReader(fr);

        FileWriter fw = new FileWriter(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/queryFile2.txt"));
        BufferedWriter bw = new BufferedWriter(fw);
        String line = br.readLine();

        while (line != null) {
            String st[] = line.split(",");
            int count = 0;
            ArrayList<Integer> ar = new ArrayList<Integer>();
            double sum = 0;
            for (String s : st) {
                if (!s.equals("0.0")) {
                    sum = sum + Double.parseDouble(s) * Double.parseDouble(s);
                }
            }
            System.out.println("initial: " + sum);
            sum = Math.sqrt(.99);
            System.out.println(sum);
            for (String s : st) {
                if (!s.equals("0.0")) {
                    System.out.println(s);
                    Double t = Double.parseDouble(s) / sum;
                    bw.write(t.toString() + " ");
                } else {
                    bw.write("0 ");
                }
            }
            bw.newLine();
            System.out.println("new line");
            line = br.readLine();
        }
        bw.close();

    }

    public void getQueryIds() throws FileNotFoundException, IOException {

        FileReader fr = new FileReader(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/queryFile.txt"));
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        HashSet<String> queryList = new HashSet<>();

        while (line != null) {
            queryList.add(line);
            line = br.readLine();
        }
        HashMap<String, String> queryIdMap = new HashMap<String, String>();
        HashSet<String> queryIds = new HashSet<String>(queryIdMap.values());
        // queryIds = (HashSet<String>) queryIdMap.keySet();
        Iterator it = queryIdMap.keySet().iterator();
        while (it.hasNext()) {
            queryIds.add((String) it.next());

        }

        for (String s : queryIdMap.values()) {
            queryIds.add(s);
        }

        fr = new FileReader(new File("C:/Users/Procheta/Desktop/new.txt"));
        br = new BufferedReader(fr);

        line = br.readLine();
        while (line != null) {
            String st[] = line.split("\\s+");
            String g = line.substring(line.indexOf(st[1]));
            queryIds.add(g);
            line = br.readLine();
        }
        queryList.removeAll(queryIdMap.keySet());
        fr = new FileReader(new File("C:/Users/Procheta/Desktop/new.txt"));
        br = new BufferedReader(fr);

        FileWriter fw = new FileWriter(originalFile);
        BufferedWriter bw = new BufferedWriter(fw);

        line = br.readLine();
        while (line != null) {

            if (line.startsWith("QUERYNO")) {
                String f = line.replaceFirst(" ", "#");
                String st1[] = f.split("#");
                if (queryIds.contains(st1[0])) {
                    bw.write(st1[1]);
                    bw.newLine();;
                }
            }
            line = br.readLine();
        }
        bw.close();
        System.out.println(queryIdMap);
    }

    public void cleanQueryFile() throws FileNotFoundException, IOException {

        FileReader fr = new FileReader(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/queryFile.txt"));
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();

        FileWriter fw = new FileWriter(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/cleanedQueryFile.txt"));
        BufferedWriter bw = new BufferedWriter(fw);

        while (line != null) {
            line = line.replace("www", "");
            line = line.replace("com", "");
            bw.write(line);
            bw.newLine();
            line = br.readLine();
        }

        bw.close();
    }

    public void prepareInputFile() throws FileNotFoundException, IOException {

        FileReader fr = new FileReader(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/inputQuery.txt"));
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();

        FileReader fr1 = new FileReader(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/inputLabel.txt"));
        BufferedReader br1 = new BufferedReader(fr1);

        String line1 = br1.readLine();

        HashMap<String, LinkedList> qidMap = new HashMap<String, LinkedList>();

        while (line != null) {
            if (qidMap.containsKey(line1)) {
                LinkedList l1 = qidMap.get(line1);
                l1.add(line);
            } else {
                LinkedList l1 = new LinkedList();
                l1.add(line);
                qidMap.put(line1, l1);
            }
            line = br.readLine();
            line1 = br1.readLine();
        }

        FileWriter fw = new FileWriter(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/labelFile.txt"));
        BufferedWriter bw = new BufferedWriter(fw);

        FileWriter fw1 = new FileWriter(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/all-task-processed.txt"));
        BufferedWriter bw1 = new BufferedWriter(fw1);

        Iterator it = qidMap.keySet().iterator();

        while (it.hasNext()) {
            String st = (String) it.next();
            LinkedList<String> l = qidMap.get(st);
            for (String s : l) {
                bw.write(s);
                bw.newLine();
                bw1.write(st);
                bw1.newLine();

            }

        }
        bw.close();
        bw1.close();
    }

    public void countCrossSession() throws FileNotFoundException, IOException {

        FileReader fr = new FileReader(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/all-task-processed.txt"));
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        FileReader fr1 = new FileReader(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/labelFile.txt"));
        BufferedReader br1 = new BufferedReader(fr1);
        String line1 = br1.readLine();
        ArrayList<String> sessionIds = new ArrayList<>();
        HashMap<String, String> sessTaskMap = new HashMap<>();
        ArrayList<String> beforeTaskIds = new ArrayList<>();
        HashSet<String> sess = new HashSet<>();
        HashSet<String> sessCount = new HashSet<>();
        int count1 = 0;
        while (line != null) {
            String st[] = line.split("\t");
            sessionIds.add(st[0] + st[1]);
            // beforeTaskIds.add(st[0] + st[1] + st[2]);

            sessTaskMap.put(st[0] + st[1] + "#" + line1, line1);
            sess.add(st[0] + st[1]);
            line = br.readLine();
            line1 = br1.readLine();
            count1++;
        }

        System.out.println(sess.size());
        Iterator it = sessTaskMap.keySet().iterator();
        int count = 0;
        while (it.hasNext()) {
            String st = (String) it.next();
            Iterator it1 = sessTaskMap.keySet().iterator();
            while (it1.hasNext()) {
                String st1 = (String) it1.next();
                if (!st.equals(st1) && sessTaskMap.get(st1).equals(sessTaskMap.get(st))) {
                    count++;
                    String stt[] = st.split("#");

                    sessCount.add(stt[0]);
                    stt = st1.split("#");
                    sessCount.add(stt[0]);
                }
            }
        }

        System.out.println(sessCount.size() + "jjjj");
        System.out.println((double) sessCount.size() / (sess.size()) * 100);
        ArrayList<String> taskIds = new ArrayList<>();

        while (line != null) {
            taskIds.add(line);
            line = br.readLine();
        }
        int num_cross_session_tasks = 0;
        count = 0;
        for (int i = 0; i < taskIds.size(); i++) {
            String taskId1 = taskIds.get(i);
            String sessionId1 = sessionIds.get(i);
            for (int j = i + 1; j < taskIds.size(); j++) {
                String taskId2 = taskIds.get(j);
                String sessionId2 = sessionIds.get(j);
                if (taskId1.equals(taskId2) && !sessionId1.equals(sessionId2)) {
                    num_cross_session_tasks++;
                }
                if (!sessionId1.equals(sessionId2)) {
                    count++;
                }
            }
        }
        System.out.println("count " + count);
        System.out.println("Number of cross session tasks: " + num_cross_session_tasks);
    }

    public void createModifiedQueryExpansion() throws IOException {
        FileWriter fw = new FileWriter(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/modifiedQuery3.txt"));
        BufferedWriter bw = new BufferedWriter(fw);

        FileReader fr = new FileReader(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/queryFile.txt"));
        BufferedReader br = new BufferedReader(fr);

        FileReader fr1 = new FileReader(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/modifiedQuery.txt"));
        BufferedReader br1 = new BufferedReader(fr1);

        String line1 = br.readLine();
        String line2 = br1.readLine();
        int count = 0;
        while (line1 != null) {
            try {
                String st[] = line1.split(" ");
                //System.out.println(st[st.length - 1]);
                int index = line2.indexOf(st[st.length - 1]);
                String stt = line2.substring(index, line2.length());
                String stArr[] = stt.split(" ");
                for (int i = 0; i < 3; i++) {
                    line1 = line1 + " " + stArr[i];
                }
                bw.write(line1);
                bw.newLine();
            } catch (Exception e) {
                System.out.println(line1);
                System.out.println(line2);
                count++;
            }
            line1 = br.readLine();
            line2 = br1.readLine();
        }
        bw.close();
        System.out.println(count);
    }

    public void createExpandQueryFile(String filename) throws FileNotFoundException, IOException {

        FileReader fr = new FileReader(new File(filename));
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();

        FileWriter fw = new FileWriter(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/modifiedQuery3.txt"));
        BufferedWriter bw = new BufferedWriter(fw);
        while (line != null) {
            String st[] = line.split(" ");
            String st1[] = null;
            int j = 0;
            try {
                for (j = 0; j < st.length; j++) {

                    st1 = st[j].split(":");
                    System.out.println(st1[1]);
                    String st11[] = st1[1].split("\\^");
                    bw.write(st11[0] + " ");
                    //System.out.println(st11.length);^M
                }
            } catch (Exception e) {
                bw.write("not expanded");
                System.out.println(j);
            }
            bw.newLine();
            line = br.readLine();
        }
        bw.close();
    }

    public void createFileForCrossValidation() throws Exception {

        FileReader fr = new FileReader(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/modifiedQuery.txt"));
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        int count1 = 0;
        int count2 = 0;
        FileWriter fw = new FileWriter(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/modifiedpart0.txt"));
        BufferedWriter bw = new BufferedWriter(fw);

        while (line != null) {
            bw.write(line);
            bw.newLine();
            if (count1 == 284) {
                count1 = -1;
                bw.close();
                count2++;
                fw = new FileWriter(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/modifiedpart" + new Integer(count2).toString()) + ".txt");
                bw = new BufferedWriter(fw);
            }
            count1++;

            line = br.readLine();
        }
        bw.close();
    }

    public static void main(String[] args) throws Exception {

        //WordVecFileCreation wvf = new WordVecFileCreation("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/testFile.txt");
        WordVecFileCreation wvf = new WordVecFileCreation("/home/procheta/all-task-processed.txt");
        wvf.createSessionData();
        String s = "";
        String originalfile = "/home/procheta/sessOutput/";
        String output = "/home/procheta/AOLTaskExperimentData/queryEmbeddingData/clusterId/sessClusterQueryEmbed/";
        String writeFile = "/home/procheta/AOLTaskExperimentData/queryEmbeddingData/sessFinalQueryEmbed/";
        File dir = new File("/home/procheta/sessDataProcessed/");
        File[] directoryListing = dir.listFiles();
        for (File f : directoryListing) {
            wvf.createFinalFileForSession(output + f.getName(), originalfile + f.getName(), writeFile + f.getName());
        }

    }

}
