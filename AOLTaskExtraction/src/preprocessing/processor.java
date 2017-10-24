/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocessing;

/**
 *
 * @author procheta
 */
/*

 * To change this license header, choose License Headers in Project Properties.

 * To change this template file, choose Tools | Templates

 * and open the template in the editor.

 */
import java.io.*;

import java.util.*;

import org.apache.lucene.analysis.Analyzer;

import org.apache.lucene.analysis.TokenStream;

import org.apache.lucene.analysis.Tokenizer;

import org.apache.lucene.analysis.core.LowerCaseFilter;

import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;

import org.apache.lucene.analysis.en.PorterStemFilter;

import org.apache.lucene.analysis.standard.StandardFilter;

import org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import org.apache.lucene.analysis.util.CharArraySet;

import org.apache.lucene.analysis.util.FilteringTokenFilter;

import org.apache.lucene.util.Version;

/**
 *
 *
 *
 * @author Debasis
 *
 */


// Removes tokens with any digit
class ValidWordFilter extends FilteringTokenFilter {

    CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);

    public ValidWordFilter(TokenStream in) {

        super(in);

    }

    @Override

    protected boolean accept() throws IOException {

        String token = termAttr.toString();

        int len = token.length();

        for (int i = 0; i < len; i++) {

            char ch = token.charAt(i);

            if (Character.isDigit(ch)) {
                return false;
            }

            if (ch == '.') {
                return false;
            }

        }

        return true;

    }

}

class URLFilter extends FilteringTokenFilter {

    TypeAttribute typeAttr = addAttribute(TypeAttribute.class);

    public URLFilter(TokenStream in) {

        super(in);

    }

    @Override

    protected boolean accept() throws IOException {

        boolean isURL = typeAttr.type().equals(UAX29URLEmailTokenizer.TOKEN_TYPES[UAX29URLEmailTokenizer.URL]);

        return !isURL;

    }

}

public class processor {

    String ifile;

    public processor(String ifile) {

        this.ifile = ifile;

    }

    protected List<String> buildStopwordList(String stopFile) {

        List<String> stopwords = new ArrayList<>();

        String line;

        try (FileReader fr = new FileReader(stopFile);
                BufferedReader br = new BufferedReader(fr)) {

            while ((line = br.readLine()) != null) {

                stopwords.add(line.trim());

            }

            br.close();

        } catch (Exception ex) {

            ex.printStackTrace();

        }

        return stopwords;

    }

    Analyzer constructAnalyzer() {

        Analyzer eanalyzer = new EnglishAnalyzer(); // default analyzer

        return eanalyzer;

    }

    String analyzeText(String txt, Analyzer analyzer) throws Exception {

        StringBuffer tokenizedContentBuff = new StringBuffer();

        TokenStream stream = analyzer.tokenStream("dummy", new StringReader(txt));

        CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);

        stream.reset();

        int numwords = 0;

        while (stream.incrementToken()) {

            String term = termAtt.toString();

            tokenizedContentBuff.append(term).append(" ");

            numwords++;

        }

        stream.end();

        stream.close();

        return numwords == 0 ? null : tokenizedContentBuff.toString();

    }

    void proces() throws Exception {

        FileReader fr = new FileReader(ifile);

        BufferedReader br = new BufferedReader(fr);

        FileWriter fw = new FileWriter(ifile + ".analyzed");

        BufferedWriter bw = new BufferedWriter(fw);

        Analyzer analyzer = constructAnalyzer();

        String line;

        while ((line = br.readLine()) != null) {

            String ppline = analyzeText(line, analyzer);

            if (ppline != null) {

                bw.write(ppline);

                bw.newLine();

            }

        }

        bw.close();

        fw.close();

        br.close();

        fr.close();

    }

    public static void main(String[] args) {

        try {

            if (args.length < 1) {

                System.err.println("usage: java MblogPreprocessor <input file>");

                return;

            }

            processor pp = new processor(args[0]);

            pp.proces();

        } catch (Exception ex) {

            ex.printStackTrace();

        }

    }

}
