package com.telepathicgrunt.wits.neoforge;

import com.telepathicgrunt.wits.WITS;
import com.telepathicgrunt.wits.commands.WITSCommand;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;


@Mod(WITS.MODID)
public class WITSForge {

    public WITSForge() {
        IEventBus forgeBus = NeoForge.EVENT_BUS;
        forgeBus.addListener(this::registerCommand);
    }

    private void registerCommand(RegisterCommandsEvent event) {
        WITSCommand.createCommand(event.getDispatcher());
    }
}
