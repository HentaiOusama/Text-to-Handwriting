package com.hentai_productions;

import java.util.ArrayList;

public class WordImageArray {
    ArrayList<CharacterImage> allImages;
    int totalWidth = 0;

    WordImageArray(ArrayList<CharacterImage> allImages) {
        this.allImages = allImages;
        for (CharacterImage img : allImages) {
            totalWidth += img.width;
        }
    }
}
