package com.dreu.traversableleaves.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlParser;
import net.minecraft.resources.ResourceLocation;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.dreu.traversableleaves.TraversableLeaves.LOGGER;
import static com.dreu.traversableleaves.TraversableLeaves.MODID;

@SuppressWarnings({"SameParameterValue", "unchecked"})
public class TLConfig {
    public static boolean configNeedsRepair = false;
    static final String fileName = "config/" + MODID + "/general.toml";
    static final String defaultConfig = """
           #Movement Speed penalty while traversing leaves, 0 = no penalty (Range : 0 - 100)
           SpeedPenalty = 27 #Default: 27
           
           #Whether Armor value reduces movement penalty
           ArmorBonus = true #Defualt: true
           
           #List of leaves (false = Blacklist)
           LeavesWhitelist = true #Default: true
           Traversable=[
            "minecraft:jungle_leaves",
            "minecraft:oak_leaves",
            "minecraft:spruce_leaves",
            "minecraft:dark_oak_leaves",
            "minecraft:acacia_leaves",
            "minecraft:birch_leaves",
            "minecraft:azalea_leaves",
            "minecraft:flowering_azalea_leaves",
            "minecraft:mangrove_leaves"
           ]
           
           #List of Entities that can/cannot traverse leaves (false = Blacklist)
           EntityWhitelist = false #Default: false
           Entities=[
            "minecraft:sheep",
            "minecraft:pig",
            "minecraft:cow",
            "minecraft:chicken",
            "minecraft:donkey",
            "minecraft:llama",
            "minecraft:trader_llama",
            "minecraft:mule",
            "minecraft:frog",
            "minecraft:goat",
            "minecraft:mooshroom",
            "minecraft:turtle"
           ]
           """;
    private static final Config CONFIG = parseFileOrDefault();
    private static final Config DEFAULT_CONFIG = new TomlParser().parse(defaultConfig);

    public static final Set<ResourceLocation> LEAVES = new HashSet<>();
    public static final Set<ResourceLocation> ENTITIES = new HashSet<>();
    public static final float MOVEMENT_PENALTY = getOrDefault("SpeedPenalty", Integer.class) * 0.02f;
    public static final float ARMOR_SCALE_FACTOR = (2 - MOVEMENT_PENALTY) * 0.05f;
    public static final boolean ARMOR_HELPS = getOrDefault("ArmorBonus", Boolean.class);
    public static final boolean IS_LEAVES_WHITELIST = getOrDefault("LeavesWhitelist", Boolean.class);
    public static final boolean IS_ENTITIES_WHITELIST = getOrDefault("EntityWhitelist", Boolean.class);
    static {
        List<String> leafStrings = getOrDefault("Traversable", List.class);
        leafStrings.forEach((leaf) -> LEAVES.add(new ResourceLocation(leaf)));

        List<String> entityStrings = getOrDefault("Entities", List.class);
        entityStrings.forEach((entity) -> ENTITIES.add(new ResourceLocation(entity)));
    }

    static <T> T getOrDefault(String key, Class<T> clas) {
        try {
            if ((CONFIG.get(key) == null)) {
                LOGGER.error("Key [{}] is missing from Config: {}", key, fileName);
                configNeedsRepair = true;
                return clas.cast(DEFAULT_CONFIG.get(key));
            }
            return CONFIG.get(key);
        } catch (Exception e) {
            LOGGER.error("Value for [{}] is an invalid type in Config: {}", key, fileName);
            configNeedsRepair = true;
            return clas.cast(DEFAULT_CONFIG.get(key));
        }
    }

    static Config parseFileOrDefault() {
        try {
            Files.createDirectories(Path.of("config/" + MODID));} catch (Exception ignored) {}
        return new TomlParser().parse(Path.of(fileName).toAbsolutePath(),
                ((path, configFormat) -> {
                    FileWriter writer = new FileWriter(path.toFile().getAbsolutePath());
                    writer.write(defaultConfig);
                    writer.close();
                    return true;}));
    }

    public static void repairConfig() {/*TODO*/}
}
