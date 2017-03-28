/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

/**
 *
 * @author Procheta
 */
public class UrlDumpFile {

    HashSet<String> cluWebdocIdList;
    String writeFileName;
    Analyzer analyzer;
    String clueWebSearchQueryUrl;
    String clueWebDocIdUrl;
    String queryFile;

    public UrlDumpFile() {

        writeFileName = "C:/Users/Procheta/Documents/ResearchData/AOLTaskData/urlDumpFile.txt";
        queryFile = "C:/Users/Procheta/Documents/ResearchData/AOLTaskData/all-tasks.txt";
        clueWebSearchQueryUrl = "http://clueweb.adaptcentre.ie/WebSearcher/search?query=";
        clueWebDocIdUrl = "http://clueweb.adaptcentre.ie/WebSearcher/view?docid=";
        analyzer = new EnglishAnalyzer();
        cluWebdocIdList = new HashSet<>();
    }

    // given the query File it will return all the cluweb doc ids related to those queries 
    public void getCluwebDocIds(String fileName) throws FileNotFoundException, IOException, ParseException {
        FileReader fr = new FileReader(new File(fileName));
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        ArrayList arr = new ArrayList<String>();
        HashSet<String> docIdList = new HashSet<>();
        int lineCount = 0;
        while (line != null) {
            String query = line;
            String text = clueWebSearchQueryUrl;

            String st[] = query.split(" ");
            for (int i = 0; i < st.length; i++) {
                if (i != 0) {
                    text = text + "%20" + st[i];
                } else {
                    text = text + st[i];
                }
            }
            String all = "";
            int urlNumCheck = 0;
            int pageNum = 1;
            try {
                while (pageNum <= 2) {
                    all = "";
                    URL url = new URL(text + "&page=" + pageNum);

                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    all = "";
                    while ((line = in.readLine()) != null) {
                        all += line;
                    }
                    JSONObject jobJSONObject = new JSONObject();

                    JSONParser jsonParser = new JSONParser();
                    JSONArray jsonArray = (JSONArray) jsonParser.parse(all);
                    int count = 0;
                    for (int j = 0; j < jsonArray.size() - 1; j++) {
                        String st1 = ((Object) jsonArray.get(j)).toString();
                        String st2 = st1.replace("[", "");
                        st2 = st2.replace("]", "");
                        char d = '"';
                        String st4 = st2.substring(st2.indexOf("id" + d), st2.length());
                        st4 = st4.substring(0, st4.indexOf(","));
                        String st3[] = st2.split(",");
                        docIdList.add(st4);
                        arr.add(st4);
                        count++;
                        if (count >= 20) {
                            urlNumCheck = 1;
                            break;
                        }

                    }
                    //System.out.println(lineCount);
                    // break;
                    if (urlNumCheck == 1) {
                        break;
                    }
                    pageNum++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            line = br.readLine();
            lineCount++;
        }
        System.out.println(arr.size());
        System.out.println(docIdList.size());
        System.out.println(lineCount);

        this.cluWebdocIdList = docIdList;
    }

    public void writeDocSnippets() throws IOException, SAXException, TikaException {

        FileWriter fw = new FileWriter(new File(writeFileName));
        BufferedWriter bwFile = new BufferedWriter(fw);

        HttpGet request = null;
        HttpResponse response = null;
        String htmlContent = "";
        BufferedWriter bw = null;
        for (String docId : cluWebdocIdList) {
            String st = docId;
            char d = '"';
            String d1 = "" + d;
            try {
                st = st.replaceAll(d1, "");
                String idArray[] = st.split(":");
                String urlPath = clueWebDocIdUrl + idArray[1];

                HttpClient client = new DefaultHttpClient();
                request = new HttpGet(urlPath);
                request.addHeader("User-Agent", "Apache HTTPClient");
                response = client.execute(request);
                HttpEntity entity = response.getEntity();
                htmlContent = EntityUtils.toString(entity);
                bw = new BufferedWriter(new FileWriter(new File("new.html")));
                bw.write(htmlContent);
                bw.close();
                BodyContentHandler handler = new BodyContentHandler();
                ParseContext pcontext = new ParseContext();
                Metadata metadata = new Metadata();
                AutoDetectParser htmlparser = new AutoDetectParser();
                FileInputStream inputstream = new FileInputStream(new File("new.html"));
                htmlparser.parse(inputstream, handler, metadata, pcontext);
                //System.out.println("Contents of the document:" + handler.toString());

                StringBuffer tokenizedContentBuff = new StringBuffer();
                TokenStream stream = analyzer.tokenStream("words", new StringReader(handler.toString()));
                CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
                stream.reset();

                while (stream.incrementToken()) {
                    String term = termAtt.toString();
                    term = term.toLowerCase();
                    tokenizedContentBuff.append(term).append(" ");
                }

                stream.end();
                stream.close();

                String content = tokenizedContentBuff.toString();
                if (content.contains(".")) {
                    String dotSplit[] = content.split("\\.");
                    content = "";
                    for (int i = 0; i < dotSplit.length; i++) {
                        content = content + " " + dotSplit[i];
                    }
                }
                bwFile.write(content + " .");
                bwFile.newLine();
            } catch (Exception e) {
                System.out.println(st);
                System.out.println(e);
            }
            //System.out.println(tokenizedContentBuff.toString());
            //  break;
        }
        bwFile.close();
    }

    public void processQueryFile(String fileName) throws IOException {

        FileWriter fw = new FileWriter(new File("C:/Users/Procheta//Documents/ResearchData/AOLTaskData/sessDataProcessed/" + fileName));
        BufferedWriter bw = new BufferedWriter(fw);
        //bw.newLine();

        FileReader fr = new FileReader(new File("C:/Users/Procheta//Documents/ResearchData/AOLTaskData/sessData/" + fileName));
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();

        while (line != null) {
            StringBuffer tokenizedContentBuff = new StringBuffer();
            TokenStream stream = analyzer.tokenStream("words", new StringReader(line));
            CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
            stream.reset();

            while (stream.incrementToken()) {
                String term = termAtt.toString();
                term = term.toLowerCase();
                tokenizedContentBuff.append(term).append(" ");
            }

            stream.end();
            stream.close();

            //bw.write(tokenizedContentBuff.toString() + ".");
            int flag = 0;
            String content = tokenizedContentBuff.toString();
            if (content.contains(".")) {
                String g[] = content.split("\\.");
                content = "";
                for (int j = 0; j < g.length; j++) {
                    content = content + " " + g[j];
                }
            }
            bw.write(content + " .");
            bw.newLine();
            line = br.readLine();
        }
        bw.close();

    }

    public void getTrueLabelsFile() throws IOException {

        FileWriter fw = new FileWriter(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/labelFile.txt"));
        BufferedWriter bw = new BufferedWriter(fw);
        bw.newLine();

        FileReader fr = new FileReader(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/testFileQ.txt"));
        BufferedReader br = new BufferedReader(fr);

        FileReader fr1 = new FileReader(new File(queryFile));
        BufferedReader br1 = new BufferedReader(fr1);

        String line1 = br.readLine();
        String line2 = br1.readLine();

        while (line1 != null) {
            String st[] = line2.split("\t");
            String taskId = st[0] + st[1] + st[2];
            StringBuffer tokenizedContentBuff = new StringBuffer();
            TokenStream stream = analyzer.tokenStream("words", new StringReader(st[4]));
            CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
            stream.reset();

            while (stream.incrementToken()) {
                String term = termAtt.toString();
                term = term.toLowerCase();
                tokenizedContentBuff.append(term).append(" ");
            }

            stream.end();
            stream.close();

            //bw.write(tokenizedContentBuff.toString() + ".");
            int flag = 0;
            String g[] = tokenizedContentBuff.toString().split("\\.");
            String g1 = "";
            for (int j = 0; j < g.length; j++) {
                g1 = g1 + g[j];
                flag = 1;
            }
            if (flag == 0) {
                line2 = tokenizedContentBuff.toString();
            } else {
                line2 = g1;
            }
            if (line1.equals(line2)) {
                bw.write(taskId);
                bw.newLine();
                line1 = br.readLine();
                line2 = br1.readLine();

            } else {

                line2 = br1.readLine();

            }
        }
        bw.close();

    }

    public void createEvalFile(String filename) throws IOException {
        FileWriter fw = new FileWriter(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/sessEval/" + filename));
        BufferedWriter bw = new BufferedWriter(fw);

        FileReader fr = new FileReader(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/sessionOutputLabel/" + filename));
        BufferedReader br = new BufferedReader(fr);
       String line2="";
       String line1="";
        try {
            FileReader fr1 = new FileReader(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/sessionTrueLabel/" + filename));
            BufferedReader br1 = new BufferedReader(fr1);
            HashSet<String> idSet = new HashSet<>();
            line1 = br.readLine();
             line2 = br1.readLine();
            // System.out.println(line2);
            while (line1 != null) {
               String st[] = line2.split("\t");
                bw.write(line1 + " " + st[1]);
                bw.newLine();
                idSet.add(line2);
                line1 = br.readLine();
                line2 = br1.readLine();
               // break;
            }
            bw.close();
            System.out.println(idSet.size());
        } catch (Exception e) {
            System.out.println("hhhhhhhh");
            System.out.println(filename);
            System.out.println(line1);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, FileNotFoundException, ParseException, SAXException, TikaException {
        UrlDumpFile urdp = new UrlDumpFile();
        // urdp.getCluwebDocIds("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/testFile.txt");
        // urdp.writeDocSnippets();
        // urdp.processQueryFile();
        // urdp.getTrueLabelsFile();
        // urdp.createEvalFile();

        File[] files = new File("C:/Users/Procheta//Documents/ResearchData/AOLTaskData/sessionTrueLabel/").listFiles();
        for (File file : files) {
            // urdp.processQueryFile(file.getName());
            System.out.println(file.getName());
            urdp.createEvalFile(file.getName());
           // break;
        }
    }

}
