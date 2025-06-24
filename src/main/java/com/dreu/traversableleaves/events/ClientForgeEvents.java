package com.dreu.traversableleaves.events;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.dreu.traversableleaves.TraversableLeaves.MODID;
import static com.dreu.traversableleaves.TraversableLeaves.configHasBeenPopulated;
import static com.dreu.traversableleaves.config.TLConfig.parse;
import static com.dreu.traversableleaves.config.TLConfig.populate;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MODID, bus = FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {

  @SubscribeEvent
  public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
    if (Minecraft.getInstance().isLocalServer() && !configHasBeenPopulated) {
      parse();
      populate();
      configHasBeenPopulated = true;
    }
  }
}
