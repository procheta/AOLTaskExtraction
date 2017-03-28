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
import java.util.HashSet;
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
            String st[] = line1.split("\t");

            //  if (line2.equals("-2") || line2.equals("-1")) {
            //     Random rn = new Random();
            //    bw.write(rn.nextInt() % 6 + " " + st[1]);
            //  } else {
            // System.out.println(line1 + " " + st.length);
            bw.write(line2 + " " + st[0] + st[1] + st[2]);
            stt.add(st[0] + st[1] + st[2]);
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

        FileWriter fw = new FileWriter(new File("C:/Users/Procheta//Documents/ResearchData/AOLTaskData/sessionTrueLabel/" + prevSessId + ".txt"));
        BufferedWriter bw = new BufferedWriter(fw);
        int sessCount = 0;
        while (line != null) {
            String st[] = line.split("\t");
            String sessId = st[0] + st[1];
            if (!prevSessId.equals(sessId)) {
                bw.close();
                fw = new FileWriter(new File("C:/Users/Procheta//Documents/ResearchData/AOLTaskData/sessionTrueLabel/" + sessId + ".txt"));
                bw = new BufferedWriter(fw);
                sessCount++;
            }
            bw.write(st[4] + "\t" + st[0] + st[1] + st[2]);
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

    public static void main(String[] args) throws IOException {

        //WordVecFileCreation wvf = new WordVecFileCreation("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/testFile.txt");
        WordVecFileCreation wvf = new WordVecFileCreation("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/googleNewsVectorFile.txt");
        // wvf.columnExtractionInputFile("C:/Users/Procheta//Documents/ResearchData/AOLTaskData/queryFile.txt");
// wvf.prepareWordvecInputFile("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/wordVecInput.txt");
        wvf.createFinalFile("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/output.txt", "C:/Users/Procheta/Documents/ResearchData/AOLTaskData/finalFile.txt");
        //  wvf.rewriteVector();
        //  wvf.createSessionInputFile();
        // wvf.createSessionWithOutput();
        // wvf.createTrueLabel();
        //  wvf.allQueryProcess();
        //
        // wvf.createLabelForTimeSPlit();
    }

}
