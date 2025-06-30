package com.dreu.traversableleaves;

import com.dreu.traversableleaves.config.TLConfig;
import com.dreu.traversableleaves.network.PacketHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

@Mod(TraversableLeaves.MODID)
public class TraversableLeaves {
    public static boolean configHasBeenPopulated = false;
    public static final String MODID = "traversable_leaves";
    public static final Logger LOGGER = LogUtils.getLogger();
    public TraversableLeaves() {
        TLConfig.parse();
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        DeferredRegister<Block> ROAR = DeferredRegister.create(ForgeRegistries.BLOCKS, "zz");
        ROAR.register("zz", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
        ROAR.register(eventBus);
        PacketHandler.register();
        MinecraftForge.EVENT_BUS.register(eventBus);
    }
}
