package io.github.alkyaly.jscoremodsfabric;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class JsCoremodsFabric implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final Marker COREMOD = MarkerManager.getMarker("COREMOD");

    @Override
    public void onInitialize() {
        LOGGER.info("\uD83E\uDD80");
    }
}