package com.telepathicgrunt.wits.services;

import com.telepathicgrunt.wits.utils.GeneralUtils;

public interface PlatformService {
    PlatformService INSTANCE = GeneralUtils.loadService(PlatformService.class);
}
