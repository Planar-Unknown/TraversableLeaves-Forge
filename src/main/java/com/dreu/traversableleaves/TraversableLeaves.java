package com.dreu.traversableleaves;

import com.mojang.logging.LogUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import static com.dreu.traversableleaves.config.TLConfig.configNeedsRepair;
import static com.dreu.traversableleaves.config.TLConfig.repairConfig;

@Mod(TraversableLeaves.MODID)
public class TraversableLeaves {
    public static final String MODID = "traversable_leaves";
    public static final Logger LOGGER = LogUtils.getLogger();
    public TraversableLeaves(){
        if(configNeedsRepair)repairConfig();
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        DeferredRegister<Block> RARA = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
        RARA.register("dev_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));
        RARA.register(eventBus);
        MinecraftForge.EVENT_BUS.register(eventBus);
    }
}
