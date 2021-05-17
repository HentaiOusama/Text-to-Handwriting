package com.hentai_productions;

public class Word {
    public final String word;
    public final int length;

    Word(String word) {
        this.word = word;
        if (word != null) {
            length = word.length();
        } else {
            length = 0;
        }
    }
}
