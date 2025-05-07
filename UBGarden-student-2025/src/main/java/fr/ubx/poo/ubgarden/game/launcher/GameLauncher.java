package fr.ubx.poo.ubgarden.game.launcher;

import fr.ubx.poo.ubgarden.game.*;

import java.io.File;
import java.util.Properties;

public class GameLauncher {

    private GameLauncher() {
    }

    public static GameLauncher getInstance() {
        return LoadSingleton.INSTANCE;
    }

    private int integerProperty(Properties properties, String name, int defaultValue) {
        return Integer.parseInt(properties.getProperty(name, Integer.toString(defaultValue)));
    }

    private boolean booleanProperty(Properties properties, String name, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(name, Boolean.toString(defaultValue)));
    }

    private Configuration getConfiguration(Properties properties) {

        int waspMoveFrequency = integerProperty(properties, "waspMoveFrequency", 2);
        int hornetMoveFrequency = integerProperty(properties, "hornetMoveFrequency", 1);

        int gardenerEnergy = integerProperty(properties, "gardenerEnergy", 100);
        int energyBoost = integerProperty(properties, "energyBoost", 50);
        long energyRecoverDuration = integerProperty(properties, "energyRecoverDuration", 1_000);
        long diseaseDuration = integerProperty(properties, "diseaseDuration", 5_000);

        return new Configuration(gardenerEnergy, energyBoost, energyRecoverDuration, diseaseDuration, waspMoveFrequency, hornetMoveFrequency);
    }


    private String decompressLine(String line) {
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < line.length()) {
            char current = line.charAt(i);

            if (current == 'x') {
                result.append('\n');
                i++;
            }
            else if (Character.isLetter(current) || current == '+' || current == '-' || current == '<' || current == '>' || current == 'P') {
                if (i + 2 < line.length() && line.charAt(i + 1) == 'x' && Character.isDigit(line.charAt(i + 2))) {
                    int j = i + 2;
                    StringBuilder numStr = new StringBuilder();
                    while (j < line.length() && Character.isDigit(line.charAt(j))) {
                        numStr.append(line.charAt(j));
                        j++;
                    }
                    int repeat = Integer.parseInt(numStr.toString());
                    result.append(String.valueOf(current).repeat(repeat));
                    i = j;
                }
                else if (i + 1 < line.length() && Character.isDigit(line.charAt(i + 1))) {
                    int j = i + 1;
                    StringBuilder numStr = new StringBuilder();
                    while (j < line.length() && Character.isDigit(line.charAt(j))) {
                        numStr.append(line.charAt(j));
                        j++;
                    }
                    int repeat = Integer.parseInt(numStr.toString());
                    result.append(String.valueOf(current).repeat(repeat));
                    i = j;
                }
                // Tek karakter
                else {
                    result.append(current);
                    i++;
                }
            }
            else if (Character.isDigit(current)) {
                i++;
            }
            else {
                throw new RuntimeException("Invalid character in compressed string: " + current);
            }
        }

        return result.toString();
    }









    public Game load(File file) {
        try {
            Properties props = new Properties();
            props.load(new java.io.FileReader(file));

            boolean compression = Boolean.parseBoolean(props.getProperty("compression", "false"));
            int nbLevels = Integer.parseInt(props.getProperty("levels", "1"));

            Configuration configuration = getConfiguration(props);
            World world = new World(nbLevels);
            Position gardenerPosition = null;
            MapLevel[] mapLevels = new MapLevel[nbLevels];


            for (int level = 0; level < nbLevels; level++) {
                String rawLevelStr = props.getProperty("level" + (level + 1));
                String levelStr = decompressLine(rawLevelStr);
                System.out.println("Decompressed level string (level " + (level + 1) + "):\n" + levelStr);

                String[] lines = levelStr.split("\n");
                int width = lines[0].length();
                int height = lines.length;
                MapLevel mapLevel = new MapLevel(width, height);

                for (int y = 0; y < height; y++) {
                    String row = lines[y];
                    for (int x = 0; x < width; x++) {
                        char c = row.charAt(x);
                        mapLevel.set(x, y, MapEntity.fromCode(c));
                    }
                }

                if (level == 0) {
                    gardenerPosition = mapLevel.getGardenerPosition();
                    if (gardenerPosition == null)
                        throw new RuntimeException("Gardener not found in level 1");
                }

                mapLevels[level] = mapLevel;
            }


            Game game = new Game(world, configuration, gardenerPosition);

            for (int level = 0; level < nbLevels; level++) {
                Level levelMap = new Level(game, level + 1, mapLevels[level]);
                world.put(level + 1, levelMap);
            }

            return game;

        } catch (Exception e) {
            throw new RuntimeException("Error loading game: " + e.getMessage(), e);
        }
    }




    public Game load() {
        Properties emptyConfig = new Properties();
        MapLevel mapLevel = new MapLevelDefaultStart();
        Position gardenerPosition = mapLevel.getGardenerPosition();
        if (gardenerPosition == null)
            throw new RuntimeException("Gardener not found");
        Configuration configuration = getConfiguration(emptyConfig);
        World world = new World(1);
        Game game = new Game(world, configuration, gardenerPosition);
        Map level = new Level(game, 1, mapLevel);
        world.put(1, level);
        return game;
    }

    private static class LoadSingleton {
        static final GameLauncher INSTANCE = new GameLauncher();
    }

}
