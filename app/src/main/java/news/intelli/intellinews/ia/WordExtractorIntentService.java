package news.intelli.intellinews.ia;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.ArrayMap;

import java.lang.reflect.Array;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import news.intelli.intellinews.mvp.model.Word;

/**
 * Created by llefoulon on 03/12/2016.
 */

public class WordExtractorIntentService extends IntentService {
    public static final String CONTENT = "content";
    public static final String WORDS_ANALYSOR = "words-analysor";
    public static final String WORDS = "words";
    public static final String MIN_ITERATION_KEY = "min-iteration";
    private static final int MIN_ITERATION = 3;
    private HashMap<String,Integer> wordAndIteration = new HashMap<>();
    private HashSet<String> wordMoreThanIteration = new HashSet<>();

    private final static Comparator<Word> WORD_COMPARATOR = new Comparator<Word>() {
        @Override
        public int compare(Word w1, Word w2) {
            return Integer.compare(w1.getOccurence(),w2.getOccurence());
        }
    };

    /*private final static Comparator<String> STRING_COMPARATOR = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            return s1.compareTo(s2);
        }
    };*/

    public WordExtractorIntentService(){
        super(WordExtractorIntentService.class.getName());
    }

    private void insert(String s,int maxIteration) {
        if(s.length() < 3)
            return;

        String[] splitWord = s.split("'");
        int size = splitWord.length;
        if(size > 1) {
            for (int i = 0; i < size; ++i) {
                insert(splitWord[i],maxIteration);
            }
            return;
        }

        //remove every accent
        s = s.toLowerCase();
        Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[^\\p{ASCII}]", "");
        //

        //TODO use binary tree implementation or hashmap
        //TODO binary tree (B-Tree) will be more efficient than ArrayMap -> http://grepcode.com/file/repo1.maven.org/maven2/org.robolectric/android-all/5.0.0_r2-robolectric-0/android/util/ArrayMap.java#ArrayMap.indexOf%28java.lang.Object%2Cint%29
        //TODO but less effective than HashMap<K,V>
        Integer repetition = wordAndIteration.get(s);
        if(repetition == null) {
            wordAndIteration.put(s,1);
        } else {
            ++repetition;
            wordAndIteration.put(s,repetition);
            if(repetition > maxIteration) {
                if(!wordMoreThanIteration.contains(s)){
                    wordMoreThanIteration.add(s);
                }
            }
        }
    }

    private ArrayList<Word> getWordAppearingMostOften() {
        Iterator<String> itString = wordMoreThanIteration.iterator();
        ArrayList<Word> words = new ArrayList<>(wordMoreThanIteration.size());
        String word;
        while(itString.hasNext()) {
            word = itString.next();
            words.add(new Word(word,wordAndIteration.get(word)));
        }

        return words;
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String content = intent.getStringExtra(CONTENT);
        int iteration = intent.getIntExtra(MIN_ITERATION_KEY,MIN_ITERATION);
        if(TextUtils.isEmpty(content))
            return;

        String[] contentWords = content.split(" ");
        for(int i = 0, size = contentWords.length;i < size;++i) {
            insert(contentWords[i],iteration);
        }

        ArrayList<Word> words = getWordAppearingMostOften();

        Collections.sort(words, WORD_COMPARATOR);

        //word that are repeat more than 3 times
        Intent wordIntent = new Intent(WORDS_ANALYSOR);
        wordIntent.putParcelableArrayListExtra(WORDS,words);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(wordIntent);
        //

    }

}
