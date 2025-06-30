package com.dreu.traversableleaves.events;

import com.dreu.traversableleaves.TraversableLeaves;
import com.dreu.traversableleaves.config.TLConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = "traversable_leaves", bus = Mod.EventBusSubscriber.Bus.FORGE, value = {Dist.CLIENT})
public class ClientForgeEvents {
  @SubscribeEvent
  public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
    if (Minecraft.getInstance().isLocalServer() && !TraversableLeaves.configHasBeenPopulated) {
      TLConfig.parse();
      TLConfig.populate();
      TraversableLeaves.configHasBeenPopulated = true;
    }
  }
}