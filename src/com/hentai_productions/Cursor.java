package com.hentai_productions;

public class Cursor {
    private int x, y;
    private final int leftStartPos, scaleFactor, bottomEndPos, rightEndPos;
    private final float characterHeightInMM, lineSpacingInMM;

    Cursor(int x, int y, int leftStartPos, float characterHeightInMM, float lineSpacingInMM, int scaleFactor, int bottomEndPos, int rightEndPos) {
        this.x = x;
        this.y = y;
        this.leftStartPos = leftStartPos;
        this.characterHeightInMM = characterHeightInMM;
        this.lineSpacingInMM = lineSpacingInMM;
        this.scaleFactor = scaleFactor;
        this.bottomEndPos = bottomEndPos;
        this.rightEndPos = rightEndPos;
    }

    boolean enter() {
        x = leftStartPos;
        y += (characterHeightInMM + lineSpacingInMM) * scaleFactor;

        return y < bottomEndPos;
    }

    boolean move(int distance) {
        x += distance;
        if(x >= rightEndPos) {
            return enter();
        } else {
            return true;
        }
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }
}
