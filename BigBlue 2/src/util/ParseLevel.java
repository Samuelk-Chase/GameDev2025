package util;

import java.io.*;
import java.util.*;

public class  ParseLevel {
    public class LevelData {
        public String name;
        public int width;
        public int height;
        public char[][] gameplayLayer;
        public char[][] ruleLayer;

        public LevelData(String name, int width, int height, char[][] gameplayLayer, char[][] ruleLayer) {
            this.name = name;
            this.width = width;
            this.height = height;
            this.gameplayLayer = gameplayLayer;
            this.ruleLayer = ruleLayer;
        }
    }

    public List<LevelData> parseLevels(String filePath) {
        List<LevelData> levels = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Skip empty lines before a level

                // Read level name
                String name = line.trim();

                // Read dimensions
                line = reader.readLine();
                if (line == null) break;
                String[] dimensions = line.split(" x ");
                int width = Integer.parseInt(dimensions[0]);
                int height = Integer.parseInt(dimensions[1]);

                // Read gameplay layer
                char[][] gameplayLayer = new char[height][width];
                for (int y = 0; y < height; y++) {
                    line = reader.readLine();
                    if (line == null) throw new Exception("Incomplete gameplay layer");
                    String row = line.length() > width ? line.substring(0, width) : line;
                    row = String.format("%-" + width + "s", row); // Pad with spaces if short
                    gameplayLayer[y] = row.toCharArray();
                }

                // Read rule layer
                char[][] ruleLayer = new char[height][width];
                for (int y = 0; y < height; y++) {
                    line = reader.readLine();
                    if (line == null) throw new Exception("Incomplete rule layer");
                    String row = line.length() > width ? line.substring(0, width) : line;
                    row = String.format("%-" + width + "s", row); // Pad with spaces if short
                    ruleLayer[y] = row.toCharArray();
                }

                levels.add(new LevelData(name, width, height, gameplayLayer, ruleLayer));
            }
        } catch (Exception e) {
            System.err.println("Error parsing level file: " + e.getMessage());
        }
        return levels;
    }
}