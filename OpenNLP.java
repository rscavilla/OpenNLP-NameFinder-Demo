package us.fourfrontdev.OpenNLP;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

public class OpenNLP {

	public OpenNLP() {}

	public static void main(String[] args) {
//		detectLanguage();

		processText();
		
	}

	private static void processText() {
		/* https://opennlp.apache.org/docs/1.9.4/manual/opennlp.html
		 * 1. Detect Sentences
		 * 2. Tokenize Text
		 * 3. Find Names
		 * To find names in raw text the text must be segmented into tokens and sentences.
		 */
		
		String text = "Pierre Vinken, 61 years old, will join the board as a nonexecutive director Nov. 29. Mr. Vinken is chairman of Elsevier N.V., the Dutch publishing group. Robert Steven Scavilla is the CEO of FourFront, LLC. Rudolph Agnew, 55 years old and former chairman of Consolidated Gold Fields PLC, was named a director of this British industrial conglomerate.";

		// 1. detect sentences
		String sentenceText[] = sentenceDetector(text);
		
		// 2. Tokenize text
		List<String[]>ts = TokenizedText(sentenceText);
		
		// 3. Find Names
		findNames(ts);
		
	}

	private static void findNames(List<String[]> ts) {
		InputStream is = null;
		is = OpenNLP.class.getResourceAsStream("/en-ner-person.bin");
		
		try {
			TokenNameFinderModel model = new TokenNameFinderModel(is);
			NameFinderME nameFinder = new NameFinderME(model);
			
			ts.forEach(docs -> {
				Span nameSpans[] = nameFinder.find(docs);
				for(Span s: nameSpans) {
					String entity=s.getType() + ": " + getCoveredText(s, docs);
					System.out.println(entity);
				}
			});
			
			nameFinder.clearAdaptiveData();

			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static String getCoveredText(Span s, String[] docs) {

		/*
		 * Span s has a getCoverredText method, but I can't get it to work??
		 */
		String entity = "";
		for(int i=s.getStart() ; i<s.getEnd() ; i++) {
			entity+=(docs[i] + " ");
		}

		return entity;
	}

	private static List<String[]> TokenizedText(String[] sentenceText) {
		InputStream tokenM = null;
		tokenM = OpenNLP.class.getResourceAsStream("/en-token.bin");
		
		TokenizerModel model=null;
		
		try {
			model = new TokenizerModel(tokenM);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Tokenizer tokenizer = new TokenizerME(model);
		
		List<String[]> ts = new ArrayList<String[]>();
		for(String sentence : sentenceText) {
			ts.add(tokenizer.tokenize(sentence));
		}

		return ts;
	}

	private static String[] sentenceDetector(String text) {
		InputStream is = null;
		is = OpenNLP.class.getResourceAsStream("/en-sent.bin");
		
		String sentences[] = null;
		try {
			SentenceModel model = new SentenceModel(is);
			SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
			sentences = sentenceDetector.sentDetect(text);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return sentences;
	}
}
