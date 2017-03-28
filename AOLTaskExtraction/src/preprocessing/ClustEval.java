package preprocessing;

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

class ClustEval {

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

    public static void main(String[] args) throws Exception {

        //if (args.length < 1) {
        //	System.err.println("usage: ClustEval <file each line containing clustering o/p <tab> ref o/p>");
        //	return;
        //}
        if (mode.equals("time_split")) {
            File[] files = new File("C:/Users/Procheta//Documents/ResearchData/AOLTaskData/sessionOutputLabel/").listFiles();
            double jacAvg = 0;
            double favg = 0;
            double avgran = 0;
            int count = 0;
            ArrayList<Integer> frequencyList = new ArrayList<Integer>();
            ArrayList<Double> ranList = new ArrayList<Double>();
            ArrayList<Double> ranFreqList = new ArrayList<Double>();

            ArrayList<Double> fsList = new ArrayList<Double>();
            ArrayList<Double> fsFreqList = new ArrayList<Double>();

            ArrayList<Double> jaccardList = new ArrayList<Double>();
            for (File file : files) {
                List<String> classLabels = new ArrayList<>(), refLabels = new ArrayList<>();
                readLines("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/sessEval/" + file.getName(), classLabels, refLabels);

                int numInstances = classLabels.size();
                assert (numInstances == refLabels.size());
                HashMap<String, LinkedList<Integer>> predicTedList = new HashMap<>();
                HashMap<String, LinkedList<Integer>> trueList = new HashMap<>();
                int tp = 0, fp = 0, fn = 0, tn = 0;
                int docid = 0;
                for (int i = 0; i < numInstances; i++) {
                    if (predicTedList.containsKey(classLabels.get(i))) {
                        LinkedList<Integer> l = predicTedList.get(classLabels.get(i));
                        l.add(docid);
                        predicTedList.put(classLabels.get(i), l);
                    } else {
                        LinkedList<Integer> l = new LinkedList<>();
                        l.add(docid);
                        predicTedList.put(classLabels.get(i), l);
                    }
                    if (trueList.containsKey(refLabels.get(i))) {
                        LinkedList<Integer> l = trueList.get(refLabels.get(i));
                        l.add(docid);
                        trueList.put(refLabels.get(i), l);
                    } else {
                        LinkedList<Integer> l = new LinkedList<>();
                        l.add(docid);
                        trueList.put(refLabels.get(i), l);
                    }
                    docid++;
                }
                for (String cl : predicTedList.keySet()) {
                    LinkedList<Integer> l = predicTedList.get(cl);
                    double max = 0;
                    for (String clt : trueList.keySet()) {

                        LinkedList<Integer> lt = trueList.get(clt);
                        double count1 = 0;
                        for (Integer t : lt) {
                            if (l.contains(t)) {
                                count1++;
                            }
                        }
                        double prec = count1 / l.size();
                        double recall = count1 / lt.size();
                        double fsc = 2 * (prec * recall) / (prec + recall);
                        if (max < fsc) {
                            max = fsc;
                        }
                    }
                    fsList.add(max);
                    fsFreqList.add(new Double(l.size()));

                }

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
                //  

                float acc = (tp + tn) / (float) (tp + fp + tn + fn);
                float prec = (tp) / (float) (tp + fp);
                float recall = (tp) / (float) (tp + fn);
                float fscore = 2 * prec * recall / (float) (prec + recall);
                float jac = tp / (float) (fp + fn + tp);

                if (!Double.isNaN(jac)) {
                    jacAvg += jac;
                    frequencyList.add(numInstances);
                    jaccardList.add((double) jac);
                }

                if (!Double.isNaN(acc)) {

                    ranList.add(new Double(acc));
                    ranFreqList.add(new Double(numInstances));

                    //
                }
                if (!Double.isNaN(jac)) {
                    frequencyList.add(numInstances);
                    jaccardList.add((double) jac);
                    jacAvg += jac;
                } else {

                    // System.out.println(numInstances);
                    if (numInstances == 2) {
                        count++;
                    }
                    //     System.out.println("Confusion matrix:");
                    //   System.out.println("[" + tp + "\t" + fp + "]");
                    //  System.out.println("[" + fn + "\t" + tn + "]");

                }
                if (!Double.isNaN(fscore)) {
                    favg += fscore;
                }

                // System.out.println(fp);
            }

            jacAvg = jacAvg / count;
            jacAvg = 0;
            double freqSum = 0;
            for (int i = 0; i < jaccardList.size(); i++) {
                freqSum += frequencyList.get(i);
            }
            //  System.out.println(freqSum);
            for (int i = 0; i < jaccardList.size(); i++) {
                jacAvg += jaccardList.get(i) * (frequencyList.get(i));
            }
            double ranSum = 0;
            for (int i = 0; i < ranFreqList.size(); i++) {
                ranSum += ranFreqList.get(i);
            }
            avgran = 0;
            for (int i = 0; i < ranList.size(); i++) {
                avgran += ranFreqList.get(i) * (ranList.get(i));
            }

            double fsSum = 0;
            for (int i = 0; i < fsFreqList.size(); i++) {
                fsSum += fsFreqList.get(i);
            }
            favg = 0;
            for (int i = 0; i < fsList.size(); i++) {
                favg += fsFreqList.get(i) * (fsList.get(i));
            }
            avgran /= ranSum;
            jacAvg /= freqSum;
            favg = favg / fsSum;
            System.out.println(count);
            System.out.println("Avg Jac: " + jacAvg);
            System.out.println("Avf f1: " + favg);
            System.out.println("Avf RI: " + avgran);
        } else {

            List<String> classLabels = new ArrayList<>(), refLabels = new ArrayList<>();
            readLines("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/finalFile.txt", classLabels, refLabels);

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
            //  

            float acc = (tp + tn) / (float) (tp + fp + tn + fn);
            float prec = (tp) / (float) (tp + fp);
            float recall = (tp) / (float) (tp + fn);
            float fscore = 2 * prec * recall / (float) (prec + recall);
            float jac = tp / (float) (fp + fn + tp);

            System.out.println(String.format("[%-8d\t%7d]", tp, fp));
            System.out.println(String.format("[%-8d\t%3d]", fn, tn));

            System.out.println("jaccard: " + jac);
            System.out.println("Fscore: " + fscore);
            System.out.println("RI: " + acc);

        }

        // System.out.println(fp);
    }

}
