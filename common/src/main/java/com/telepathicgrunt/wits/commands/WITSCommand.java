package com.telepathicgrunt.wits.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WITSCommand {
    public static void createCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        String commandString = "wits";
        String dimensionArg = "dimension";
        String locationArg = "location";

        LiteralCommandNode<CommandSourceStack> source = dispatcher.register(Commands.literal(commandString)
            .requires((permission) -> permission.hasPermission(0))
            .executes(cs -> {
                WorldCoordinates coordinates;
                if (cs.getSource().getEntity() instanceof Player) {
                    BlockPos currentPosition = cs.getSource().getEntity().blockPosition();
                    coordinates = new WorldCoordinates(
                            new WorldCoordinate(false, currentPosition.getX()),
                            new WorldCoordinate(false, currentPosition.getY()),
                            new WorldCoordinate(false, currentPosition.getZ()));
                }
                else {
                    coordinates = new WorldCoordinates(
                            new WorldCoordinate(false, 0),
                            new WorldCoordinate(false, 0),
                            new WorldCoordinate(false, 0));
                }

                listStructuresAtSpot(cs.getSource().getLevel(), coordinates, true, cs);
                return 1;
            })
            .requires((permission) -> permission.hasPermission(2))
            .then(Commands.argument(dimensionArg, DimensionArgument.dimension())
            .then(Commands.argument(locationArg, Vec3Argument.vec3())
            .executes(cs -> {
                listStructuresAtSpot(DimensionArgument.getDimension(cs, dimensionArg), Vec3Argument.getCoordinates(cs, locationArg), false, cs);
                return 1;
            })
        )));

        dispatcher.register(Commands.literal(commandString).redirect(source));
    }

    private static void listStructuresAtSpot(ServerLevel level, Coordinates coordinates, boolean callerPosition, CommandContext<CommandSourceStack> cs) {
        BlockPos centerPos = coordinates.getBlockPos(cs.getSource());
        List<StructureStart<?>> structureStarts = new ArrayList<>();

        for (StructureFeature<?> structureFeature : Registry.STRUCTURE_FEATURE) {
            StructureStart<?> startsForFeature = level.structureFeatureManager().getStructureAt(centerPos, true, structureFeature);
            structureStarts.add(startsForFeature);
        }

        Set<? extends StructureFeature<?>> structures = structureStarts.stream()
                .filter(ss -> ss.getBoundingBox().isInside(centerPos))
                .map(StructureStart::getFeature).collect(Collectors.toSet());

        if (structures.isEmpty()) {
            Component component = new TextComponent(callerPosition ?
                    "There's no structures at your location." :
                    "There's no structures at the location.");
            cs.getSource().sendSuccess(component, !(cs.getSource().getEntity() instanceof Player));
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (callerPosition) {
            stringBuilder.append("Structure(s) at your location:");
        }
        else {
            stringBuilder.append("Structure(s) at ").append(centerPos).append(":");
        }

        for (StructureFeature<?> structure : structures) {
            ResourceLocation key = Registry.STRUCTURE_FEATURE.getKey(structure);
            stringBuilder.append("§r\n - §6").append(key);
        }

        Component component = new TextComponent(stringBuilder.toString());
        cs.getSource().sendSuccess(component, !(cs.getSource().getEntity() instanceof Player));
    }
}
