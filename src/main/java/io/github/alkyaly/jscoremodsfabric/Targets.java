package io.github.alkyaly.jscoremodsfabric;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.jetbrains.annotations.Nullable;

public interface Targets {

    MappingResolver RESOLVER = FabricLoader.getInstance().getMappingResolver();

    record Class(String name) implements Targets {
        @Override
        public String clazz() {
            return name;
        }
    }

    record Field(String clazz, String name) implements Targets {
        @Override
        public String clazz() {
            return clazz;
        }
    }

    record Method(String clazz, String name) implements Targets {
        @Override
        public String clazz() {
            return clazz;
        }
    }

    String clazz();

    static String map(Coremod.TargetType type, String owner, @Nullable String name, @Nullable String desc) {
        return switch (type) {
            case CLASS -> RESOLVER.mapClassName("intermediary", owner);
            case METHOD -> RESOLVER.mapMethodName("intermediary", owner, name, desc);
            case FIELD -> RESOLVER.mapFieldName("intermediary", owner, name, desc);
        };
    }
}
