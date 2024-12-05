package com.dreu.traversableleaves;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(TraversableLeaves.MODID)
public class TraversableLeaves {
    public static final String MODID = "traversable_leaves";
    public static final Logger LOGGER = LogUtils.getLogger();
    public TraversableLeaves() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
    }
}
