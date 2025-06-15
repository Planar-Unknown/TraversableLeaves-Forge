package com.dreu.traversableleaves.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlParser;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.dreu.traversableleaves.TraversableLeaves.LOGGER;
import static com.dreu.traversableleaves.TraversableLeaves.MODID;

@SuppressWarnings({"SameParameterValue", "unchecked", "DataFlowIssue"})
public class TLConfig {
  //Todo: properly rigor test this config
  public static boolean configNeedsRepair = false;
  static final String fileName = "config/" + MODID + "/general.toml";
  static final String DEFAULT_CONFIG_STRING = """
      # To reset this config to default, delete this file and rerun the game.
      # Movement Speed penalty while traversing leaves, 0 = no penalty (Range : 0 - 100) | Default: 27
      SpeedPenalty = 27
      
      # Whether Armor value reduces movement penalty | Default: true
      ArmorBonus = true
      
      # List of traversable blocks. | Default: ["#minecraft:leaves"]
      Traversable=[
       "#minecraft:leaves"
      ]
      
      # Whether the Entities list is a whitelist (true), or a blacklist (false) | Default: false
      EntityWhitelist = false
      
      # List of Entities that can/cannot traverse blocks | Default: []
      Entities=[
      ]
      """;

  public static void repairConfig() {
    LOGGER.info("An issue was found with config: {} | You can find a copy of faulty config at: {} | Repairing...", fileName, fileName.replace(".toml", "_faulty.toml"));
    Path sourcePath = Paths.get(fileName);
    Path destinationPath = Paths.get(fileName.replace(".toml", "_faulty.toml"));
    try {
      Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      LOGGER.warn("Exception during faulty config caching: {}", e.getMessage());
    }
    try (FileWriter writer = new FileWriter(new File(fileName).getAbsolutePath())) {
      StringBuilder contents = new StringBuilder()
          .append("# To reset this config to default, delete this file and rerun the game.")
          .append("# Movement Speed penalty while traversing leaves, 0 = no penalty (Range : 0 - 100) | Default: 27\n")
          .append("SpeedPenalty = ")
          .append(CACHED_SPEED_PENALTY)
          .append("\n")
          .append("# Whether Armor value reduces movement penalty | Default: true\n")
          .append("ArmorBonus = ")
          .append(ARMOR_HELPS)
          .append("\n")
          .append("# List of traversable blocks | Default: [\"#minecraft:leaves\"]\n")
          .append("Traversable=[\n");
      for (String key : BLOCKS_CACHE)
        contents.append("   \"").append(key).append("\",\n");
      contents.append("""
              ]
              
              #List of Entities that can/cannot traverse blocks (false = Blacklist)
              EntityWhitelist =\s""")
          .append(IS_ENTITIES_WHITELIST)
          .append(" #Default: false\n")
          .append("Entities=[\n");
      for (String key : ENTITIES_CACHE)
        contents.append("   \"").append(key).append("\",\n");
      contents.append("]\n");
      writer.write(contents.toString());
    } catch (IOException e) {
      LOGGER.warn("Exception during config repair: {}", e.getMessage());
    }
  }

  private static final Config DEFAULT_CONFIG = new TomlParser().parse(DEFAULT_CONFIG_STRING);
  private static Config CONFIG;

  public static final Set<ResourceLocation> TL_BLOCKS = new HashSet<>();
  public static final Set<ResourceLocation> TL_ENTITIES = new HashSet<>();

  private static final Set<String> BLOCKS_CACHE = new HashSet<>();
  private static final Set<String> ENTITIES_CACHE = new HashSet<>();

  public static boolean IS_ENTITIES_WHITELIST;
  private static int CACHED_SPEED_PENALTY;
  public static float MOVEMENT_PENALTY;
  public static float ARMOR_SCALE_FACTOR;
  public static boolean ARMOR_HELPS;

  public static void parse() {
    CONFIG = parseConfigOrDefault();
  }

  public static void populate() {
    ENTITIES_CACHE.clear();
    BLOCKS_CACHE.clear();
    TL_BLOCKS.clear();
    TL_ENTITIES.clear();
    CACHED_SPEED_PENALTY = getOrDefault("SpeedPenalty", Integer.class);
    MOVEMENT_PENALTY = CACHED_SPEED_PENALTY * 0.02f;
    ARMOR_SCALE_FACTOR = (2 - MOVEMENT_PENALTY) * 0.05f;
    ARMOR_HELPS = getOrDefault("ArmorBonus", Boolean.class);
    IS_ENTITIES_WHITELIST = getOrDefault("EntityWhitelist", Boolean.class);

    Set<ResourceLocation> toRemove = new HashSet<>();
    ((List<String>) getOrDefault("Traversable", List.class)).forEach((configKey) -> {
      if (configKey.startsWith("-")) {
        if (configKey.charAt(1) == '#') {
          if (isValidBlockTag(configKey.substring(2))) {
            BLOCKS_CACHE.add(configKey);
            for (Block block : ForgeRegistries.BLOCKS.tags().getTag(BlockTags.create(new ResourceLocation(configKey.substring(2)))))
              toRemove.add(ForgeRegistries.BLOCKS.getKey(block));
          }
        } else if (isValidBlock(configKey.substring(1))) {
          BLOCKS_CACHE.add(configKey);
          toRemove.add(new ResourceLocation(configKey.substring(1)));
        }
      } else if (configKey.startsWith("#")) {
        if (isValidBlockTag(configKey.substring(1))) {
          BLOCKS_CACHE.add(configKey);
          for (Block block : ForgeRegistries.BLOCKS.tags().getTag(BlockTags.create(new ResourceLocation(configKey.substring(1)))))
            TL_BLOCKS.add(ForgeRegistries.BLOCKS.getKey(block));
        }
      } else if (isValidBlock(configKey)) {
        BLOCKS_CACHE.add(configKey);
        TL_BLOCKS.add(new ResourceLocation(configKey));
      }
    });
    TL_BLOCKS.removeAll(toRemove);

    toRemove.clear();
    ((List<String>) getOrDefault("Entities", List.class)).forEach((configKey) -> {
      if (configKey.startsWith("-")) {
        if (configKey.charAt(1) == '#') {
          if (isValidEntityTag(configKey.substring(2))) {
            ENTITIES_CACHE.add(configKey);
            for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES.tags().getTag(TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(configKey.substring(2)))))
              toRemove.add(ForgeRegistries.ENTITY_TYPES.getKey(entityType));
          }
        } else if (isValidEntity(configKey.substring(1))) {
          ENTITIES_CACHE.add(configKey);
          toRemove.add(new ResourceLocation(configKey.substring(1)));
        }
      }
      if (configKey.startsWith("#"))
        if (isValidEntityTag(configKey.substring(1))) {
          ENTITIES_CACHE.add(configKey);
          for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES.tags().getTag(TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(configKey.substring(1)))))
            TL_ENTITIES.add(ForgeRegistries.ENTITY_TYPES.getKey(entityType));
        } else if (isValidEntity(configKey)) {
          ENTITIES_CACHE.add(configKey);
          TL_ENTITIES.add(new ResourceLocation(configKey));
        }
    });
    TL_ENTITIES.removeAll(toRemove);
  }

  private static boolean isValidEntityTag(String tagId) {
    if (!ResourceLocation.isValidResourceLocation(tagId)) {
      LOGGER.warn("Not a valid Entity Tag ResourceLocation: <{}> declared in Config: [{}] | Skipping Tag...", tagId, fileName);
      return false;
    }
    if (!ForgeRegistries.ENTITY_TYPES.tags().isKnownTagName(TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(tagId)))) {
      LOGGER.warn("Not an existing Entity Tag: <{}> declared in Config: [{}] | Skipping Tag...", tagId, fileName);
      return false;
    }
    return true;
  }

  private static boolean isValidEntity(String entityId) {
    if (!ResourceLocation.isValidResourceLocation(entityId)) {
      LOGGER.warn("Not a valid Entity ResourceLocation: <{}> declared in Config: [{}] | Skipping Block...", entityId, fileName);
      return false;
    }
    if (!ModList.get().isLoaded(entityId.split(":")[0])) {
      LOGGER.warn("Config: [{}] declared Entity: <{}> but Mod: '{{}}' is not loaded | Skipping Block...", fileName, entityId, entityId.split(":")[0]);
      return false;
    }
    if (!ForgeRegistries.ENTITY_TYPES.containsKey(new ResourceLocation(entityId))) {
      LOGGER.warn("Config: [{}] declared Entity: <{}> which does not exist, check for typos! | Skipping Block...", fileName, entityId);
      return false;
    }
    return true;
  }

  private static boolean isValidBlockTag(String tagId) {
    if (!ResourceLocation.isValidResourceLocation(tagId)) {
      LOGGER.warn("Not a valid Block Tag ResourceLocation: <{}> declared in Config: [{}] | Skipping Tag...", tagId, fileName);
      return false;
    }
    if (!ForgeRegistries.BLOCKS.tags().isKnownTagName(BlockTags.create(new ResourceLocation(tagId)))) {
      LOGGER.warn("Not an existing Block Tag: <{}> declared in Config: [{}] | Skipping Tag...", tagId, fileName);
      return false;
    }
    return true;
  }

  private static boolean isValidBlock(String blockId) {
    if (!ResourceLocation.isValidResourceLocation(blockId)) {
      LOGGER.warn("Not a valid Block ResourceLocation: <{}> declared in Config: [{}] | Skipping Block...", blockId, fileName);
      return false;
    }
    if (!ModList.get().isLoaded(blockId.split(":")[0])) {
      LOGGER.warn("Config: [{}] declared Block: <{}> but Mod: '{{}}' is not loaded | Skipping Block...", fileName, blockId, blockId.split(":")[0]);
      return false;
    }
    if (!ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(blockId))) {
      LOGGER.warn("Config: [{}] declared Block: <{}> which does not exist, check for typos! | Skipping Block...", fileName, blockId);
      return false;
    }
    return true;
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

  static Config parseConfigOrDefault() {
    try {
      Files.createDirectories(Path.of("config/" + MODID));
    } catch (Exception ignored) {
    }
    return new TomlParser().parse(Path.of(fileName).toAbsolutePath(),
        ((path, configFormat) -> {
          FileWriter writer = new FileWriter(path.toFile().getAbsolutePath());
          writer.write(DEFAULT_CONFIG_STRING);
          writer.close();
          return true;
        }));
  }
}
