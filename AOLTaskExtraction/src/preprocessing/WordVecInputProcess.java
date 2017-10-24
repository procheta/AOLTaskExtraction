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
import java.util.ArrayList;
import java.util.Properties;

class AolObject {

    String AnonId;
    String query;
    TimeUnit queryTime;
}

class TimeUnit {

    double year;
    double month;
    double day;
    double hour;
    double min;
    double sec;

    public TimeUnit(String line) {
        String st[] = line.split(" ");
        String ydm[] = st[0].split("-");
        year = Double.parseDouble(ydm[0]);
        month = Double.parseDouble(ydm[1]);
        day = Double.parseDouble(ydm[2]);
        String hms[] = st[1].split(":");
        hour = Double.parseDouble(hms[0]);
        min = Double.parseDouble(hms[1]);
        sec = Double.parseDouble(hms[2]);
    }

    public int isMorThanTwentySixMinuteDif(TimeUnit t) {

        if ((this.year != t.year) || (this.month != t.month) || (this.day != t.day) || (this.hour != t.hour)) {
            return 1;
        }
        double minDif = this.min - t.min;
        double scDif = (this.sec - t.sec) / 60;

        double timeDiff = minDif + scDif;

        if (timeDiff >= 26) {
            return 1;
        } else {
            return 0;
        }
    }

}

/**
 *
 * @author procheta
 */
public class WordVecInputProcess {

    String aolQueryLogPath;
    BufferedWriter bw;

    public WordVecInputProcess(String propFileName) throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        prop.load(new FileReader(new File(propFileName)));
        aolQueryLogPath = prop.getProperty("aolLogPath");
        FileWriter fw = new FileWriter(new File(prop.getProperty("writeFile")));
        bw = new BufferedWriter(fw);
    }

    public AolObject extractAOLObject(String line) {

        String st[] = line.split("\t");
        AolObject aob = new AolObject();
        aob.AnonId = st[0];
        aob.query = st[1];
        aob.queryTime = new TimeUnit(st[2]);

        return aob;
    }

    public void getsameSessionQuery(AolObject aob1, AolObject aob2, ArrayList<String> sessionQuerySet) {

        if (aob2.queryTime.isMorThanTwentySixMinuteDif(aob1.queryTime) == 0) {
            sessionQuerySet.add(aob2.query);
        }
    }

    public void writeSessionQueries(ArrayList<String> sessQuerySet) throws IOException {

        for (String query : sessQuerySet) {
            bw.write(query + " ");
        }
        bw.newLine();
    }

    public void readAOLLog() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(new File(aolQueryLogPath));
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        line = br.readLine();
        int flag = 0;
        AolObject prevAolobj = null;
        ArrayList<String> sessionQuerySet = new ArrayList<>();

        while (line != null) {
            AolObject aob = extractAOLObject(line);
            if(flag == 0)
            {
                prevAolobj = aob;
                flag = 1;
            }
            int initialSize = sessionQuerySet.size();
            getsameSessionQuery(prevAolobj, aob, sessionQuerySet);
            if (initialSize == sessionQuerySet.size()) {
                writeSessionQueries(sessionQuerySet);
                sessionQuerySet = new ArrayList<>();
                sessionQuerySet.add(aob.query);
            }
            prevAolobj = aob;
            line = br.readLine();
        }
        writeSessionQueries(sessionQuerySet);
    }

    public static void main(String[] args) throws IOException {

        WordVecInputProcess wvp = new WordVecInputProcess("/home/procheta/NetBeansProjects/AOLTaskExtraction/src/preprocessing/init.properties");
        wvp.readAOLLog();
        wvp.bw.close();
    }
}
