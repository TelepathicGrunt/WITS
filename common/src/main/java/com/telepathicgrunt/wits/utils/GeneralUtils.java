package com.telepathicgrunt.wits.utils;

import java.util.ServiceLoader;

public final class GeneralUtils {
    private GeneralUtils() {}

    public static <T> T loadService(Class<T> service) {
        return ServiceLoader.load(service).findFirst().orElseThrow(() -> new IllegalStateException("No platform implementation found for " + service.getName()));
    }
}
