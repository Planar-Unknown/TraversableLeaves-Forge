package com.dreu.traversableleaves.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlParser;
import net.minecraft.resources.ResourceLocation;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.dreu.traversableleaves.TraversableLeaves.MODID;

public class TLConfig {
    static final String defaultConfig = """
           #List of leaves that should be traversable
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
    public static final Set<ResourceLocation> LEAVES = new HashSet<>();
    static {
        List<String> leafStrings = CONFIG.getKey().get("Traversable");
        leafStrings.forEach((leaf) -> {
            LEAVES.add(new ResourceLocation(leaf));
        });
    }
}
