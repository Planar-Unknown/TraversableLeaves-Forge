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

@SuppressWarnings("SameParameterValue")
public class TLConfig {
    static final String defaultConfig = """
           #List of leaves (false = Blacklist)
           LeavesWhitelist = true
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
           EntityWhitelist = false
           Entities=[
            "minecraft:sheep",
            "minecraft:pig",
            "minecraft:cow",
            "minecraft:chicken",
            "minecraft:horse",
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
    static Map.Entry<Config, String> getConfigOrDefault(String name, String defaultConfig) {
        try {
            Files.createDirectories(Path.of("config/" + MODID));} catch (Exception ignored) {}
        return Map.entry(new TomlParser().parse(Path.of("config/" + MODID + "/"+ name +".toml").toAbsolutePath(),
                ((path, configFormat) -> {
                    FileWriter writer = new FileWriter(path.toFile().getAbsolutePath());
                    writer.write(defaultConfig);
                    writer.close();
                    return true;})), "config/" + MODID + "/"+ name +".toml");
    }
    private static final Map.Entry<Config, String> CONFIG = getConfigOrDefault("traversable_leaves",defaultConfig);
    private static final Config DEFAULT_CONFIG = new TomlParser().parse(defaultConfig);

    public static final Set<ResourceLocation> LEAVES = new HashSet<>();
    public static Set<ResourceLocation> ENTITIES = new HashSet<>();
    static {
        List<String> leafStrings = CONFIG.getKey().get("Traversable");
        leafStrings.forEach((leaf) -> LEAVES.add(new ResourceLocation(leaf)));

        List<String> entityStrings = CONFIG.getKey().get("Entities");
        entityStrings.forEach((entity) -> ENTITIES.add(new ResourceLocation(entity)));
    }

    public static final boolean IS_LEAVES_WHITELIST = getBooleanOrDefault("LeavesWhitelist", CONFIG, DEFAULT_CONFIG);
    public static final boolean IS_ENTITIES_WHITELIST = getBooleanOrDefault("EntityWhitelist", CONFIG, DEFAULT_CONFIG);


    //Todo: Make get List or Default method


    static boolean getBooleanOrDefault(String key, Map.Entry<Config, String> config, Config defaultConfig) {
        try {
            if ((config.getKey().get(key) == null)) {
                LOGGER.error("Key [{}] is missing from Config: {}", key, config.getValue());
                return defaultConfig.get(key);
            }
            return config.getKey().get(key);
        } catch (Exception e) {
            LOGGER.error("Value for [{}] is an invalid type in Config: {}", key, config.getValue());
            return defaultConfig.get(key);
        }
    }
}
