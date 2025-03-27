package com.telepathicgrunt.wits.fabric.entrypoints;

import com.telepathicgrunt.wits.WITS;
import com.telepathicgrunt.wits.commands.WITSCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class Main implements ModInitializer {

    @Override
    public void onInitialize() {
        WITS.init();
        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, dedicated) -> WITSCommand.createCommand(dispatcher));
    }
}
