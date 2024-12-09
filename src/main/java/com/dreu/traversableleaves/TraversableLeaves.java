package com.dreu.traversableleaves;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import static com.dreu.traversableleaves.config.TLConfig.configNeedsRepair;
import static com.dreu.traversableleaves.config.TLConfig.repairConfig;

@Mod(TraversableLeaves.MODID)
public class TraversableLeaves {
    public static final String MODID = "traversable_leaves";
    public static final Logger LOGGER = LogUtils.getLogger();
    public TraversableLeaves(){if(configNeedsRepair)repairConfig();}
}
