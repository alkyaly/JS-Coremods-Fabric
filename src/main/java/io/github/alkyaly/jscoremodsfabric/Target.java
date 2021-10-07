package io.github.alkyaly.jscoremodsfabric;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public interface Target {

    MappingResolver RESOLVER = FabricLoader.getInstance().getMappingResolver();

    record Class(String name) implements Target {
        @Override
        public String clazz() {
            return name;
        }
    }

    record Field(String clazz, String name) implements Target {
        @Override
        public String clazz() {
            return clazz;
        }
    }

    record Method(String clazz, String name) implements Target {
        @Override
        public String clazz() {
            return clazz;
        }
    }

    String clazz();

    static String map(Coremod.TargetType type, String owner, String name, String desc) {
        return switch (type) {
            case CLASS -> RESOLVER.mapClassName("intermediary", owner);
            case METHOD -> RESOLVER.mapMethodName("intermediary", owner, name, desc);
            case FIELD -> RESOLVER.mapFieldName("intermediary", owner, name, desc);
        };
    }
}
