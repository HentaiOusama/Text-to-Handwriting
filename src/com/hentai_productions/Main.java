package com.hentai_productions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    private static final int UNDERLINE = -2;

    private static final HashMap<Integer, CharacterImage> allASCIIImages = new HashMap<>();
    private static final StringBuffer inputText = new StringBuffer();
    private static final String parameterFileName = "./baseParameters.txt";
    private static final int scaleFactor = 10;
    private static int outputFileCount = 0;

    // Readable Data
    private static int heightInMM = 208, widthInMM = 147, leftAndRightMarginInMM = 6;
    private static boolean shouldOffsetCharacterHeight = true;
    private static float baseCharacterHeightInMM = 4.5f;
    private static int topMarginInMM = 11, bottomMarginInMM = 10;
    private static float baseLineSpacingInMM = 0.2f, baseSpaceWidthInMM = 1.55f;
    private static boolean shouldAddSignature = false;
    private static float slantModeEnterProbability = 0f, slantModeExitProbability = 0f;

    // Derived Data
    private static int leftStartPos = leftAndRightMarginInMM * scaleFactor, rightEndPos = (widthInMM - leftAndRightMarginInMM - 1) * scaleFactor;
    private static int bottomEndPos = (heightInMM - bottomMarginInMM - 1) * scaleFactor;

    public static void main(String[] args) throws IOException {
        buildParametersAndInputText();

        BufferedImage bufferedImage = generateHandWrittenPage();

        exportImage(bufferedImage);
    }

    private static void buildParametersAndInputText() throws IOException {
        File file = new File(parameterFileName);
        Scanner scanner;
        if (file.exists()) {
            try {
                scanner = new Scanner(new FileInputStream(file));

                heightInMM = Integer.parseInt(scanner.nextLine().split("#->")[0].trim());
                widthInMM = Integer.parseInt(scanner.nextLine().split("#->")[0].trim());
                leftAndRightMarginInMM = Integer.parseInt(scanner.nextLine().split("#->")[0].trim());
                shouldOffsetCharacterHeight = Boolean.parseBoolean(scanner.nextLine().split("#->")[0].trim());
                baseCharacterHeightInMM = Float.parseFloat(scanner.nextLine().split("#->")[0].trim());
                topMarginInMM = Integer.parseInt(scanner.nextLine().split("#->")[0].trim());
                bottomMarginInMM = Integer.parseInt(scanner.nextLine().split("#->")[0].trim());
                baseLineSpacingInMM = Float.parseFloat(scanner.nextLine().split("#->")[0].trim());
                baseSpaceWidthInMM = Float.parseFloat(scanner.nextLine().split("#->")[0].trim());
                shouldAddSignature = Boolean.parseBoolean(scanner.nextLine().split("#->")[0].trim());
                slantModeEnterProbability = Float.parseFloat(scanner.nextLine().split("#->")[0].trim());
                slantModeExitProbability = Float.parseFloat(scanner.nextLine().split("#->")[0].trim());

                leftStartPos = leftAndRightMarginInMM * scaleFactor;
                rightEndPos = (widthInMM - leftAndRightMarginInMM - 1) * scaleFactor;
                bottomEndPos = (heightInMM - bottomMarginInMM - 1) * scaleFactor;
                scanner.close();
            } catch (Exception e) {
                System.out.println("Error While reading data from the Base Parameter File");
                e.printStackTrace();
            }
        } else {
            System.out.println("Base Parameter File Not Found. Using Default Values...");
        }

        file = new File("inputDocument.txt");
        if (file.exists()) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            int data;
            while ((data = bufferedReader.read()) != -1) {
                inputText.append((char) data);
            }
            bufferedReader.close();
        }
        System.out.println("Input Data :-\n" + inputText);
        System.out.println("--------------------------------------------------------------------");
    }

    private static BufferedImage generateHandWrittenPage() throws IOException {
        BufferedImage bufferedImage = new BufferedImage(widthInMM * scaleFactor, heightInMM * scaleFactor, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics graphics = bufferedImage.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, widthInMM * scaleFactor, heightInMM * scaleFactor); // Building the page background

        drawHandWrittenPage(graphics);

        return bufferedImage;
    }

    // Not yet Complete
    private static void drawHandWrittenPage(Graphics graphics) throws IOException {
        if (shouldAddSignature) {
            addSignature(graphics);
        }

        Cursor cursor = new Cursor(leftStartPos, topMarginInMM * scaleFactor, leftStartPos, baseCharacterHeightInMM, baseLineSpacingInMM, scaleFactor,
                bottomEndPos, rightEndPos, slantModeEnterProbability, slantModeExitProbability);

        while (inputText.length() > 0) {

            SpecialSymbol specialSymbol = isOrGetSpecialSymbol(0);

            if (specialSymbol.isSpecialSymbol) {
                int data = specialSymbol.getSymbolCode();
                for (int i = 0; i < specialSymbol.getSymbolLength(); i++) {
                    inputText.deleteCharAt(0);
                }
                switch (data) {
                    case 13 -> {
                        // Carriage Return
                        // Do Nothing
                    }

                    case '\n' -> {
                        if (!cursor.enter()) {
                            return;
                        }
                    }

                    case ' ' -> cursor.move((int) ((baseSpaceWidthInMM * scaleFactor) + ((Math.random() * 8) - 3)));

                    case UNDERLINE -> {
                        // Not yet complete
                    }
                }
            } else {
                // Not yet complete. Read a word and do the stuff needed.
                Word word = getWord();
                if (word.length == 0) {
                    return;
                } else {
                    for (int i = 0; i < word.length; i++) {
                        inputText.deleteCharAt(0);
                    }
                }

                // Below is temporary code. This has to be improved. (Not yet complete)
                WordImageArray wordImageArray = getWordImageArray(word);
                for (int i = 0; i < wordImageArray.allImages.size(); i++) {
                    if (!printCharacterImage(graphics, cursor, wordImageArray.allImages.get(i))) {
                        return;
                    }
                }
            }
        }
    }

    private static void addSignature(Graphics graphics) throws IOException {
        BufferedImage tempImg = ImageIO.read(new File("./characterSet/0/Sign.png"));
        graphics.drawImage(tempImg, 0, 0, tempImg.getWidth() * 8 * scaleFactor / tempImg.getHeight(),
                8 * scaleFactor, null);
    }

    private static SpecialSymbol isOrGetSpecialSymbol(int pos) {
        SpecialSymbol specialSymbol = null;

        int data = inputText.charAt(pos);

        if (data == '\n') {
            specialSymbol = new SpecialSymbol(true, '\n', 1);
        } else if (data == 13) {
            specialSymbol = new SpecialSymbol(true, 13, 1);
        } else if (data == ' ') {
            specialSymbol = new SpecialSymbol(true, ' ', 1);
        } else if (data == '^' && inputText.length() >= (pos + 2)) {
            if (inputText.charAt(1) == '_' && inputText.charAt(2) == data) {
                specialSymbol = new SpecialSymbol(true, UNDERLINE, 3);
            }
        } else {
            specialSymbol = new SpecialSymbol(false);
        }

        return specialSymbol;
    }

    private static Word getWord() {

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < inputText.length(); i++) {
            if (!isOrGetSpecialSymbol(i).isSpecialSymbol) {
                result.append(inputText.charAt(i));
            } else {
                break;
            }
        }

        return new Word(result.toString());
    }

    private static WordImageArray getWordImageArray(Word word) throws IOException {
        ArrayList<CharacterImage> allImages = new ArrayList<>();

        for (int i = 0; i < word.length; i++) {
            CharacterImage baseImg = getCharacterImage(word.word.charAt(i));
            assert baseImg != null;
            int height = baseImg.height;
            if (shouldOffsetCharacterHeight) {
                height += (int) ((Math.random() * 8) - 4);
            }
            int width = baseImg.width * height / baseImg.height;
            allImages.add(new CharacterImage(baseImg.image, width, height));
        }

        return new WordImageArray(allImages);
    }

    private static CharacterImage getCharacterImage(int data) throws IOException {
        CharacterImage characterImage;
        if (!allASCIIImages.containsKey(data)) {
            File file = new File("./characterSet/0/" + data + ".png");
            if (!file.exists()) {
                System.out.println(file + " Not Found. Skipping the character...");
                return null;
            }
            BufferedImage bufferedImage = ImageIO.read(file);
            int height = ((int) (baseCharacterHeightInMM * scaleFactor));
            int width = bufferedImage.getWidth() * height / bufferedImage.getHeight();
            characterImage = new CharacterImage(bufferedImage, width, height);
            allASCIIImages.put(data, characterImage);
        } else {
            characterImage = allASCIIImages.get(data);
        }

        return characterImage;
    }

    private static boolean printCharacterImage(Graphics graphics, Cursor cursor, CharacterImage characterImage) {
        if (characterImage == null) {
            return true; // Yep. This is not a mistake. Return value is true, which means the character was skipped.
        }

        int yDifference = (int) ((baseCharacterHeightInMM * scaleFactor) - characterImage.height);

        graphics.drawImage(characterImage.image, cursor.getX(), cursor.getY() + yDifference + ((int) ((Math.random() * 6) - 2.5f)),
                characterImage.width, characterImage.height, null);

        return cursor.move(characterImage.width + ((int) (Math.random() * 4)));
    }

    private static void exportImage(BufferedImage bufferedImage) throws IOException {
        boolean success = false;
        File outputFile = new File("./outputFiles/" + ++outputFileCount + ".png");
        if (!outputFile.getParentFile().exists()) {
            if (!outputFile.getParentFile().mkdirs()) {
                System.out.println("Image " + outputFileCount + " creation : Failure");
                return;
            }
        }
        if (!outputFile.exists()) {
            if (outputFile.createNewFile()) {
                ImageIO.write(bufferedImage, "png", outputFile);
                success = true;
            } else {
                System.out.println("Error while exporting image no. " + outputFileCount);
            }
        } else {
            ImageIO.write(bufferedImage, "png", outputFile);
            success = true;
        }
        System.out.println("Page " + outputFileCount + " Export : " + ((success) ? "Success" : "Failure"));
    }
}