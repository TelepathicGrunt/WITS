package com.telepathicgrunt.wits.fabric;

import com.telepathicgrunt.wits.commands.WITSCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class WITSFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, dedicated) -> WITSCommand.createCommand(dispatcher));
    }
}
