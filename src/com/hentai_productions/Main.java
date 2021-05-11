package com.hentai_productions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
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
    private static int heightInMM = 208, widthInMM = 147, leftAndRightMarginInMM = 7, topMarginInMM = 12, bottomMarginInMM = 10;
    private static float characterHeightInMM = 2, lineSpacingInMM = 0.2f, baseSpaceWidthInMM = 0.2f;
    private static float slantModeEnterProbability = 0f, slantModeExitProbability = 0f;
    private static boolean shouldAddSignature = false;

    // Derived Data
    private static int leftStartPos = leftAndRightMarginInMM * scaleFactor, rightEndPos = (widthInMM - leftAndRightMarginInMM - 1) * scaleFactor;
    private static int bottomEndPos = (heightInMM - bottomMarginInMM - 1) * scaleFactor;

    private static void buildParametersAndInputText() throws IOException {
        File file = new File(parameterFileName);
        Scanner scanner;
        if (file.exists()) {
            try {
                scanner = new Scanner(new FileInputStream(file));
                heightInMM = Integer.parseInt(scanner.nextLine().split("#->")[0].trim());
                widthInMM = Integer.parseInt(scanner.nextLine().split("#->")[0].trim());
                leftAndRightMarginInMM = Integer.parseInt(scanner.nextLine().split("#->")[0].trim());
                characterHeightInMM = Float.parseFloat(scanner.nextLine().split("#->")[0].trim());
                topMarginInMM = Integer.parseInt(scanner.nextLine().split("#->")[0].trim());
                bottomMarginInMM = Integer.parseInt(scanner.nextLine().split("#->")[0].trim());
                lineSpacingInMM = Float.parseFloat(scanner.nextLine().split("#->")[0].trim());
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
        if(file.exists()) {
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

    private static void addSignature(Graphics graphics) throws IOException {
        BufferedImage tempImg = ImageIO.read(new File("./characterSet/0/Sign.png"));
        graphics.drawImage(tempImg, 0, 0, tempImg.getWidth() * 8 * scaleFactor / tempImg.getHeight(),
                8 * scaleFactor, null);
    }

    private static void exportImage(BufferedImage bufferedImage) throws IOException {
        boolean success = false;
        File outputFile = new File("./outputFiles/" + ++outputFileCount + ".png");
        if(!outputFile.getParentFile().exists()) {
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

    private static boolean printCharacter(Graphics graphics, Cursor cursor, int data) throws IOException {
        CharacterImage characterImage = getCharacterImage(data);
        if (characterImage == null) {
            return true;
        }

        graphics.drawImage(characterImage.image, cursor.getX(), cursor.getY(), characterImage.width, characterImage.height, null);

        return cursor.move(characterImage.width);
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
            characterImage = new CharacterImage(bufferedImage, (int) (bufferedImage.getWidth() *
                    characterHeightInMM * scaleFactor / bufferedImage.getHeight()), (int) (characterHeightInMM * scaleFactor));
            allASCIIImages.put(data, characterImage);
        } else {
            characterImage = allASCIIImages.get(data);
        }

        return characterImage;
    }

    private static int getExpectedWordLength(String string) throws IOException {
        int length = 0;
        CharacterImage characterImage;
        for (int i = 0; i < string.length(); i++) {
            characterImage = getCharacterImage(string.charAt(i));
            length += (characterImage != null) ? characterImage.width : 0;
        }
        return length;
    }

    public static void main(String[] args) throws IOException {
        buildParametersAndInputText();

        BufferedImage bufferedImage = generateHandWrittenPage();

        exportImage(bufferedImage);
    }



    private static void drawHandWrittenPage(Graphics graphics) throws IOException {
        if (shouldAddSignature) {
            addSignature(graphics);
        }

        Cursor cursor = new Cursor(leftStartPos, topMarginInMM * scaleFactor, leftStartPos, characterHeightInMM , lineSpacingInMM, scaleFactor,
                bottomEndPos, rightEndPos, slantModeEnterProbability, slantModeExitProbability);

        while (inputText.length() > 0) {

            int data = inputText.charAt(0);

            // Check for flags
            if (data == '^' && inputText.length() >= 3) {
                if (inputText.charAt(1) == '_' && inputText.charAt(2) == data) {
                    data = UNDERLINE;
                    inputText.deleteCharAt(0);
                    inputText.deleteCharAt(0);
                }
            }

            inputText.deleteCharAt(0);

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

                default -> {
                    if(!printCharacter(graphics, cursor, data)) {
                        return;
                    }
                }
            }
        }
    }
}