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
    public static final String CATEGORY_SPELLS = "spells";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.IntValue TESTBLOCK_CAPACITY;
    public static ForgeConfigSpec.IntValue TESTBLOCK_GENERATION;
    public static ForgeConfigSpec.IntValue TESTBLOCK_BURNTIME;
    public static ForgeConfigSpec.IntValue TESTBLOCK_XFER;

    public static ForgeConfigSpec.IntValue GRIEFSHARD_RESTORE;
    public static ForgeConfigSpec.IntValue GCD;

    public static ForgeConfigSpec.DoubleValue JOLT_DMG;
    public static ForgeConfigSpec.DoubleValue SHOCKBOLT_DMG;


    static {
        COMMON_BUILDER.comment("General").push(CATEGORY_GENERAL);
        GRIEFSHARD_RESTORE = COMMON_BUILDER.comment("Mana restored by a Grief Shard")
                .defineInRange("griefShardRestore",500,0,10000);
        GCD = COMMON_BUILDER.comment("Cooldown in ticks shared by all spells")
                .defineInRange("globalCoolDown",25,1,200);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("General").push(CATEGORY_SPELLS);
        JOLT_DMG = COMMON_BUILDER.comment("Damage dealt by Jolt (50% armor pen)")
                .defineInRange("joltDamage",9f,1f,100f);
        SHOCKBOLT_DMG = COMMON_BUILDER.comment("Damage dealt by Shockbolt and Aeroblast (50% armor pen)")
                .defineInRange("shockboltDamage",12f,1f,100f);
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
