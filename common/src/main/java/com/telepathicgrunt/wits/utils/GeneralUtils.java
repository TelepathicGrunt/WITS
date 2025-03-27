package com.telepathicgrunt.wits.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import net.minecraft.Util;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public final class GeneralUtils {
    private GeneralUtils() {}

    public static <T> T loadService(Class<T> service) {
        return ServiceLoader.load(service).findFirst().orElseThrow(() -> new IllegalStateException("No platform implementation found for " + service.getName()));
    }
}
