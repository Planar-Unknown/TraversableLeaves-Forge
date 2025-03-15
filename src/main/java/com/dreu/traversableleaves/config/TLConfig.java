package com.dreu.traversableleaves.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlParser;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.dreu.traversableleaves.TraversableLeaves.LOGGER;
import static com.dreu.traversableleaves.TraversableLeaves.MODID;

@SuppressWarnings({"SameParameterValue", "unchecked"})
public class TLConfig {
    public static boolean configNeedsRepair = false;
    static final String fileName = "config/" + MODID + "/general.toml";
    static final String defaultConfig =
            "#To reset this config to default, delete this file and rerun the game.\n" +
            "#Movement Speed penalty while traversing leaves, 0 = no penalty (Range : 0 - 100)\n" +
            "SpeedPenalty = 27 #Default: 27\n" +
            "\n" +
            "#Whether Armor value reduces movement penalty\n" +
            "ArmorBonus = true #Defualt: true\n" +
            "\n" +
            "#Whether leaves behave like ladders\n" +
            "CanClimb = true #Default: true\n" +
            "\n" +
            "#List of leaves (false = Blacklist)\n" +
            "LeavesWhitelist = false #Default: true\n" +
            "Traversable=[\n]\n" +
            "\n" +
            "#List of Entities that can/cannot traverse leaves (false = Blacklist)\n" +
            "EntityWhitelist = false #Default: false\n" +
            "Entities=[\n" +
            " \"minecraft:sheep\",\n" +
            " \"minecraft:pig\",\n" +
            " \"minecraft:cow\",\n" +
            " \"minecraft:chicken\",\n" +
            " \"minecraft:donkey\",\n" +
            " \"minecraft:llama\",\n" +
            " \"minecraft:trader_llama\",\n" +
            " \"minecraft:mule\",\n" +
            " \"minecraft:frog\",\n" +
            " \"minecraft:goat\",\n" +
            " \"minecraft:mooshroom\",\n" +
            " \"minecraft:turtle\"\n" +
            "]";
    private static final Config CONFIG = parseFileOrDefault();
    private static final Config DEFAULT_CONFIG = new TomlParser().parse(defaultConfig);

    public static final Set<ResourceLocation> LEAVES = new HashSet<>();
    public static final Set<ResourceLocation> ENTITIES = new HashSet<>();
    private static final int CACHED_SPEED_PENALTY = getOrDefault("SpeedPenalty", Integer.class);
    public static final float MOVEMENT_PENALTY = CACHED_SPEED_PENALTY * 0.02f;
    public static final float ARMOR_SCALE_FACTOR = (2 - MOVEMENT_PENALTY) * 0.05f;
    public static final boolean CAN_CLIMB = getOrDefault("CanClimb", Boolean.class);
    public static final boolean ARMOR_HELPS = getOrDefault("ArmorBonus", Boolean.class);
    public static final boolean IS_LEAVES_WHITELIST = getOrDefault("LeavesWhitelist", Boolean.class);
    public static final boolean IS_ENTITIES_WHITELIST = getOrDefault("EntityWhitelist", Boolean.class);
    static {
        List<String> leafStrings = getOrDefault("Traversable", List.class);
        leafStrings.forEach((leaf) -> LEAVES.add(new ResourceLocation(leaf)));

        List<String> entityStrings = getOrDefault("Entities", List.class);
        entityStrings.forEach((entity) -> ENTITIES.add(new ResourceLocation(entity)));
    }

    static <T> T getOrDefault(String key, Class<T> clazz) {
        try {
            if ((CONFIG.get(key) == null)) {
                LOGGER.error("Key [{}] is missing from Config: [{}] | Marking config file for repair...", key, fileName);
                configNeedsRepair = true;
                return clazz.cast(DEFAULT_CONFIG.get(key));
            }
            return clazz.cast(CONFIG.get(key));
        } catch (Exception e) {
            LOGGER.error("Value: [{}] for [{}] is an invalid type in Config: {} | Expected: [{}] but got: [{}] | Marking config file for repair...", CONFIG.get(key), key, fileName, clazz.getTypeName(), CONFIG.get(key).getClass().getTypeName());
            configNeedsRepair = true;
            return clazz.cast(DEFAULT_CONFIG.get(key));
        }
    }

    static Config parseFileOrDefault() {
        try {
            Files.createDirectories(new File("config/" + MODID).toPath());
        } catch (Exception e) {
            LOGGER.warn("Failed to create Config Directories for " + new File("config/" + MODID).toPath());
        }
        return new TomlParser().parse(new File(fileName),
                ((path, configFormat) -> {
                    FileWriter writer = new FileWriter(path.toFile().getAbsolutePath());
                    writer.write(defaultConfig);
                    writer.close();
                    return true;}));
    }

    public static void repairConfig() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        Path faulty_path = Paths.get(fileName.replace(".toml", "_faulty_" + timestamp + ".toml"));
        LOGGER.info("An issue was found with config: {} | You can find a copy of faulty config at: {} | Repairing...", fileName, fileName.replace(".toml", "_faulty.toml"));
        Path sourcePath = Paths.get(fileName);
        try {
            Files.copy(sourcePath, faulty_path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.warn("Exception during faulty config caching: {}", e.getMessage());
        }
        try (FileWriter writer = new FileWriter(new File(fileName).getAbsolutePath())) {
            StringBuilder contents = new StringBuilder("#Movement Speed penalty while traversing leaves, 0 = no penalty (Range : 0 - 100)\n");
            contents.append("SpeedPenalty = ")
                    .append(CACHED_SPEED_PENALTY)
                    .append(" #Default: 27\n")
                    .append("\n")
                    .append("#Whether Armor value reduces movement penalty\n")
                    .append("ArmorBonus = ")
                    .append(ARMOR_HELPS)
                    .append(" #Defualt: true\n")
                    .append("\n")
                    .append("#List of leaves (false = Blacklist)\n")
                    .append("LeavesWhitelist = ")
                    .append(IS_LEAVES_WHITELIST)
                    .append(" #Default: true\n")
                    .append("Traversable=[\n");
            for (ResourceLocation rL : LEAVES) {
                contents.append("   \"").append(rL.toString()).append("\",\n");
            }
            contents.append("]\n\n" +
                            "#List of Entities that can/cannot traverse leaves (false = Blacklist)\n" +
                            "EntityWhitelist =")
                    .append(IS_ENTITIES_WHITELIST)
                    .append(" #Default: false\n")
                    .append("Entities=[\n");
            for (ResourceLocation rL : ENTITIES) {
                contents.append("   \"").append(rL.toString()).append("\",\n");
            }
            contents.append("]\n");
            writer.write(contents.toString());
        } catch (IOException e) {
            LOGGER.warn("Exception during config repair: {}", e.getMessage());
        }
    }
}
