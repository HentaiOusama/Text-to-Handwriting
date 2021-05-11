package com.hentai_productions;

// Not yet complete. Still need to define what to do in slant mode and upon exiting slant mode...
public class Cursor {
    private int x, y;
    private final int leftStartPos, scaleFactor, bottomEndPos, rightEndPos;
    private final float characterHeightInMM, lineSpacingInMM;

    private final float slantModeEnterProbability, slantModeExitProbability;
    private boolean isSlantModeActive = false;

    Cursor(int x, int y, int leftStartPos, float characterHeightInMM, float lineSpacingInMM, int scaleFactor, int bottomEndPos, int rightEndPos,
           float slantModeEnterProbability, float slantModeExitProbability) {
        this.x = x;
        this.y = y;
        this.leftStartPos = leftStartPos;
        this.characterHeightInMM = characterHeightInMM;
        this.lineSpacingInMM = lineSpacingInMM;
        this.scaleFactor = scaleFactor;
        this.bottomEndPos = bottomEndPos;
        this.rightEndPos = rightEndPos;
        this.slantModeEnterProbability = slantModeEnterProbability;
        this.slantModeExitProbability = slantModeExitProbability;
    }

    boolean enter() {

        if (isSlantModeActive) {
            float randomPick = (float) (Math.random() * 100);
            if (randomPick <= slantModeExitProbability - 0.01f) {
                isSlantModeActive = false;
            }
        }

        x = leftStartPos;
        y += ((characterHeightInMM + lineSpacingInMM) * scaleFactor) + (Math.random() * 4) - 1;

        return y < bottomEndPos;
    }

    boolean move(int distance) {

        if (!isSlantModeActive) {
            float randomPick = (float) (Math.random() * 100);
            if (randomPick <= slantModeEnterProbability - 0.01f) {
                isSlantModeActive = true;
            }
        }

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
