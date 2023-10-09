package com.telepathicgrunt.wits.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
            .requires((permission) -> permission.hasPermission(0))
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
            .requires((permission) -> permission.hasPermission(2))
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
            cs.getSource().sendSuccess(component, !cs.getSource().isPlayer());
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (callerPosition) {
            stringBuilder.append("Structure(s) at your location:");
        }
        else {
            stringBuilder.append("Structure(s) at ").append(centerPos).append(":");
        }

        for (Structure structure : structures) {
            ResourceLocation key = level.registryAccess().ownedRegistryOrThrow(Registry.STRUCTURE_REGISTRY).getKey(structure);
            stringBuilder.append("§r\n - §6").append(key);
        }

        Component component = Component.literal(stringBuilder.toString());
        cs.getSource().sendSuccess(component, !cs.getSource().isPlayer());
    }
}
