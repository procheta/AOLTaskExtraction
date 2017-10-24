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
import java.util.Iterator;

/**
 *
 * @author Procheta
 */
public class QueryVecFormulator {
    
    ArrayList<HashMap<String,Double>> qList;
    String writeFile;
    String scoreFile;
    
    public QueryVecFormulator(String writeFileName, String scoreFileName){
        qList = new ArrayList<>();
        writeFile = writeFileName;
        scoreFile = scoreFileName;
    }            

    public void readScoreFile() throws FileNotFoundException, IOException {

        FileReader fr = new FileReader(new File(scoreFile));
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        ArrayList<HashMap<String, Double>> query = new ArrayList<>();

        while (line != null) {
            if (!line.startsWith("###")) {
                String cluewebIdList = line.substring(line.indexOf("@@@") + 3, line.length());
                HashMap<String, Double> scoreMap = processString(cluewebIdList);
                query.add(scoreMap);
            } else {
                if(query.size() != 0){
                  HashMap<String, Double> q = computeQueryVec(query);
                  qList.add(q);
                }
                query = new ArrayList<>();
            }
            line = br.readLine();
        }
        System.out.println(qList.size());
    }

    public HashMap<String, Double> processString(String line) {

        String st[] = line.split(",");
        HashMap<String, Double> scoreMap = new HashMap<>();
        System.out.println(st[0]);
        for (int i = 0; i < st.length; i++) {

            String token = st[i];
            String tokens[] = token.split(":");
            scoreMap.put(tokens[0], Double.parseDouble(tokens[1]));
        }
        return scoreMap;
    }

    public HashMap<String, Double> computeQueryVec(ArrayList<HashMap<String, Double>> queryMap) {

        HashMap<String, Double> queryScore = new HashMap<>();

        for (int i = 0; i < queryMap.size(); i++) {
            HashMap<String, Double> scoreMap = queryMap.get(i);

            Iterator it = scoreMap.keySet().iterator();
            while(it.hasNext()){
                String token = (String) it.next();
                if(!queryScore.containsKey(token)){
                    queryScore.put(token, scoreMap.get(token));
                }else{
                    queryScore.put(token, queryScore.get(token) + scoreMap.get(token));
                }
            }
        }
        return queryScore;
    }
    
    public void writeQueryVec() throws IOException{
        FileWriter fw = new FileWriter(new File(writeFile));
        BufferedWriter bw = new BufferedWriter(fw);
        
       for (int i = 0; i < qList.size(); i++) {
            HashMap<String, Double> scoreMap = qList.get(i);
            Iterator it = scoreMap.keySet().iterator();
            while(it.hasNext()){
                String token = (String) it.next();
                double score = scoreMap.get(token);
                bw.write(token + ":"+ new Double(score).toString()+" ");
            }
            bw.newLine();
       }
      bw.close();
    }
    
    public static void main(String[] args) throws IOException{
        QueryVecFormulator qv = new QueryVecFormulator(args[0],args[1]);
        qv.readScoreFile();
        qv.writeQueryVec();
    }
}
