package com.neepsy.voxelmagica.util;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class Config {
    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_ENERGY = "energy";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.IntValue TESTBLOCK_CAPACITY;
    public static ForgeConfigSpec.IntValue TESTBLOCK_GENERATION;
    public static ForgeConfigSpec.IntValue TESTBLOCK_BURNTIME;
    public static ForgeConfigSpec.IntValue TESTBLOCK_XFER;

    static {
        COMMON_BUILDER.comment("General").push(CATEGORY_GENERAL);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Energy Settings").push(CATEGORY_ENERGY);

        TESTBLOCK_CAPACITY = COMMON_BUILDER.comment("Energy capacity of test generator")
                .defineInRange("testGenCapacity", 50000, 0, Integer.MAX_VALUE);
        TESTBLOCK_GENERATION = COMMON_BUILDER.comment("FE produced per tick")
                .defineInRange("testGenProduce", 32, 0, Integer.MAX_VALUE);
        TESTBLOCK_BURNTIME= COMMON_BUILDER.comment("Burn time (in ticks) per item")
                .defineInRange("testGenBurnTime", 200, 0, Integer.MAX_VALUE);
        TESTBLOCK_XFER = COMMON_BUILDER.comment("Max FE output")
                .defineInRange("testGenOutput", 64, 0, Integer.MAX_VALUE);


        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path){
        final CommentedFileConfig config = CommentedFileConfig.builder(path)
                .sync().autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        config.load();;
        spec.setConfig(config);
    }
}
