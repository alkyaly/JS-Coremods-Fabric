package io.github.alkyaly.jscoremodsfabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.MappingResolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class JsCoremodsFabric implements ModInitializer {

    public static final String MOD_ID = "jscoremodsfabric";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Marker COREMOD = MarkerManager.getMarker("COREMOD");

    @Override
    public void onInitialize() {
    }
}