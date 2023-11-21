package com.telepathicgrunt.wits.forge;

import com.telepathicgrunt.wits.WITS;
import com.telepathicgrunt.wits.commands.WITSCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;


@Mod(WITS.MODID)
public class WITSForge {

    public WITSForge() {
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::registerCommand);
    }

    private void registerCommand(RegisterCommandsEvent event) {
        WITSCommand.createCommand(event.getDispatcher());
    }
}
