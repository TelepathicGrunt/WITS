package com.telepathicgrunt.wits.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.List;

public class WITSCommand {
    public static void createCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        String commandString = "wits";
        String opCommandString = "witsop";
        String dimensionArg = "dimension";
        String locationArg = "location";

        LiteralCommandNode<CommandSourceStack> source = dispatcher.register(Commands.literal(commandString)
                .requires(Commands.hasPermission(Commands.LEVEL_ALL))
            .executes(cs -> {
                WorldCoordinates coordinates;
                if (cs.getSource().isPlayer()) {
                    BlockPos currentPosition = cs.getSource().getPlayer().blockPosition();
                    coordinates = WorldCoordinates.absolute(currentPosition.getX(), currentPosition.getY(), currentPosition.getZ());
                }
                else {
                    coordinates = WorldCoordinates.absolute(0, 0, 0);
                }

                listStructuresAtSpot(cs.getSource().getLevel(), coordinates, true, cs);
                return 1;
            })
        );

        dispatcher.register(Commands.literal(commandString).redirect(source));

        LiteralCommandNode<CommandSourceStack> source2 = dispatcher.register(Commands.literal(opCommandString)
            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(Commands.argument(dimensionArg, DimensionArgument.dimension())
            .then(Commands.argument(locationArg, Vec3Argument.vec3())
            .executes(cs -> {
                listStructuresAtSpot(DimensionArgument.getDimension(cs, dimensionArg), Vec3Argument.getCoordinates(cs, locationArg), false, cs);
                return 1;
            })
        )));

        dispatcher.register(Commands.literal(opCommandString).redirect(source2));
    }

    private static void listStructuresAtSpot(ServerLevel level, Coordinates coordinates, boolean callerPosition, CommandContext<CommandSourceStack> cs) {
        BlockPos centerPos = coordinates.getBlockPos(cs.getSource());

        List<StructureStart> structureStarts = level.structureManager().startsForStructure(new ChunkPos(centerPos), s -> true);
        List<Structure> structures = structureStarts.stream()
                .filter(ss -> ss.getBoundingBox().isInside(centerPos))
                .map(StructureStart::getStructure).toList();

        if (structures.isEmpty()) {
            Component component = Component.literal(callerPosition ?
                    "There's no structures at your location." :
                    "There's no structures at the location.");
            cs.getSource().sendSuccess(() -> component, !cs.getSource().isPlayer());
            return;
        }

        MutableComponent component;
        if (callerPosition) {
            component = MutableComponent.create(PlainTextContents.create("Structure(s) at your location:"));
        }
        else {
            component = MutableComponent.create(PlainTextContents.create("Structure(s) at " + centerPos.toShortString() + ":"));
        }

        for (Structure structure : structures) {
            Identifier key = level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getKey(structure);
            component.append(Component.literal("\n -").withStyle(ChatFormatting.RESET))
                    .append(Component.literal(key.toString()).withStyle(ChatFormatting.GOLD));
        }

        cs.getSource().sendSuccess(() -> component, !cs.getSource().isPlayer());
    }
}
