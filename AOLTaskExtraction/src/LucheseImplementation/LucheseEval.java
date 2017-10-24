package LucheseImplementation;

import preprocessing.*;
import java.io.*;
import java.util.*;

class RefLabelFreq implements Comparable<RefLabelFreq> {

    String refLabel;
    int freq;

    public RefLabelFreq(String refLabel) {
        this.refLabel = refLabel;
        freq = 0;

    }

    @Override
    public int compareTo(RefLabelFreq o) {
        return -1 * Integer.compare(freq, o.freq); // descending
    }
}

class RefLabelFreqs {

    String predictedLabel;
    HashMap<String, RefLabelFreq> refLabels;
    List<RefLabelFreq> sortedList;

    public RefLabelFreqs(String predictedLabel) {
        refLabels = new HashMap<>();
        this.predictedLabel = predictedLabel;
    }

    void add(String refClassLabel) {
        RefLabelFreq rlf = refLabels.get(refClassLabel);
        if (rlf == null) {
            rlf = new RefLabelFreq(refClassLabel);
        }
        rlf.freq++;
        refLabels.put(refClassLabel, rlf);
    }

    RefLabelFreq getMax() {
        if (sortedList != null) {
            return sortedList.get(0);
        }

        sortedList = new ArrayList<>();
        for (RefLabelFreq rf : refLabels.values()) {
            sortedList.add(rf);
        }
        Collections.sort(sortedList);
        return sortedList.get(0);
    }
}

class LucheseEval {

    static final boolean DEBUG = false;
    static final String mode = "wholeData";

    static void readLines(String fileName, List<String> classLabels, List<String> refLabels) throws Exception {
        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);

        String line;
        int count = 0;
        while ((line = br.readLine()) != null) {

            String[] tokens = line.split("\\s+");
            classLabels.add(tokens[0]);
            refLabels.add(tokens[1]);
        }

        br.close();
        fr.close();
    }

    static void modifyClassLabels(List<String> classLabels, List<String> refLabels) throws Exception {
        HashMap<String, RefLabelFreqs> freqMap = new HashMap<>();

        for (String classLabel : classLabels) {
            for (String refLabel : refLabels) {
                RefLabelFreqs lfreqs = freqMap.get(classLabel);
                if (lfreqs == null) {
                    lfreqs = new RefLabelFreqs(classLabel);
                    freqMap.put(classLabel, lfreqs);
                }
                lfreqs.add(refLabel);
            }
        }

        int numClassLabels = classLabels.size();
        for (int i = 0; i < numClassLabels; i++) {
            String classLabel = classLabels.get(i);

            RefLabelFreqs rlfs = freqMap.get(classLabel);
            if (rlfs == null) {
                continue;
            }
            RefLabelFreq maxFreq = rlfs.getMax();

            refLabels.set(i, maxFreq.refLabel);
        }
    }

    static void createFinalFile(String inputFile, String EvalFile, String writeFile) throws FileNotFoundException, IOException {

        FileReader fr = new FileReader(inputFile);
        BufferedReader br = new BufferedReader(fr);

        BufferedReader br1 = new BufferedReader(new FileReader(EvalFile));

        String line1 = br1.readLine();
        String line2 = br.readLine();
        FileWriter fw = new FileWriter(new File(writeFile));
        BufferedWriter bw = new BufferedWriter(fw);
        while (line1 != null) {
            bw.write(line2 + " " + line1);
            bw.newLine();
            line1 = br1.readLine();
            line2 = br.readLine();
        }
        bw.close();
    }

    public static void main(String[] args) throws Exception {

        createFinalFile(args[0], args[1], args[2]);

        List<String> classLabels = new ArrayList<>(), refLabels = new ArrayList<>();
        readLines(args[2], classLabels, refLabels);

        int numInstances = classLabels.size();
        assert (numInstances == refLabels.size());
        int tp = 0, fp = 0, fn = 0, tn = 0;
        int docid = 0;

        for (int i = 0; i < numInstances - 1; i++) {
            String l_i = classLabels.get(i);
            String r_i = refLabels.get(i);

            for (int j = i + 1; j < numInstances; j++) {
                String l_j = classLabels.get(j);
                String r_j = refLabels.get(j);

                boolean labelsAgree = l_i.equals(l_j);
                boolean refClassesAgree = r_i.equals(r_j);

                if (DEBUG) {
                    System.out.println("Decision for class-pair: (" + l_i + "," + l_j + "), refpair: (" + r_i + "," + r_j + ")");
                    System.out.println(labelsAgree + ", " + refClassesAgree);
                }

                if (labelsAgree && refClassesAgree) {
                    tp++;
                }
                if (labelsAgree && !refClassesAgree) {
                    fp++;
                }
                if (!labelsAgree && refClassesAgree) {
                    fn++;
                }
                if (!labelsAgree && !refClassesAgree) {
                    tn++;
                }

            }
        }

        float acc = (tp + tn) / (float) (tp + fp + tn + fn);
        float prec = (tp) / (float) (tp + fp);
        float recall = (tp) / (float) (tp + fn);
        float fscore = 2 * prec * recall / (float) (prec + recall);
        float jac = tp / (float) (fp + fn + tp);

        System.out.println(String.format("[%-8d\t%7d]", tp, fp));
        System.out.println(String.format("[%-8d\t%3d]", fn, tn));

        FileWriter fw = new FileWriter(new File(args[3]));
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(String.format("[%-8d\t%7d]", tp, fp));
        bw.newLine();
        bw.write(String.format("[%-8d\t%3d]", fn, tn));
        bw.newLine();
        bw.write("jaccard: " + jac);
        bw.newLine();
        bw.write("Fscore: " + fscore);
        bw.newLine();
        bw.write("RI: " + acc);
        bw.newLine();
        bw.write("Recall: " + recall);
        bw.newLine();
        bw.write("Precision : " + prec);
        bw.close();
        System.out.println("jaccard: " + jac);
        System.out.println("Fscore: " + fscore);
        System.out.println("RI: " + acc);
        System.out.println("Recall: " + recall);
        System.out.println("Precision : " + prec);

    }
}
