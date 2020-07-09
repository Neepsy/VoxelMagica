package com.neepsy.voxelmagica.proxy;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.neepsy.voxelmagica.VoxelMagica;
import com.neepsy.voxelmagica.blocks.ModBlocks;
import com.neepsy.voxelmagica.blocks.TestBlockScreen;
import com.neepsy.voxelmagica.effects.ModEffects;
import com.neepsy.voxelmagica.entity.*;
import com.neepsy.voxelmagica.entity.Rendering.*;
import com.neepsy.voxelmagica.util.Constants;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;


import javax.annotation.Nullable;
import java.util.*;

@Mod.EventBusSubscriber(modid= VoxelMagica.MODID, value = Dist.CLIENT, bus=Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy implements IProxy {

    public void init(){
        ScreenManager.registerFactory(ModBlocks.TESTBLOCKCONTAINER, TestBlockScreen::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.INFUSEPROJECTILE, InfuseProjectileRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.JOLTPROJECTILE, JoltProjectileRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.SHOCKBOLTPROJECTILE, ShockboltProjectileRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.AEROBLASTPROJECTILE, AeroblastProjectileRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.SCORCHPROJECTILE, ScorchProjectileRenderer::new);
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public void loadModels(){
        //Make sure forge knows about our custom models
        ModelResourceLocation m1 = new ModelResourceLocation("voxelmagica:rapier", "inventory");
        ModelResourceLocation m2 = new ModelResourceLocation("voxelmagica:whitestaff", "inventory");
        ModelLoader.addSpecialModel(m1);
        ModelLoader.addSpecialModel(m2);
    }

    public void onModelBake(ModelBakeEvent event){

        ArrayList<ModelResourceLocation> models = new ArrayList();
        models.add(new ModelResourceLocation("voxelmagica:whitestaff", "inventory"));
        models.add(new ModelResourceLocation("voxelmagica:rapier", "inventory"));

        Map<ResourceLocation, IBakedModel> map = event.getModelRegistry();
        Map<Item, Integer> needsModel = Constants.getInstance().needsCustomModel;


        for(Item i : needsModel.keySet()){

            ResourceLocation item = i.getRegistryName();
            ResourceLocation item_inv = new ModelResourceLocation(item, "inventory");
            //ResourceLocation item_hand = new ModelResourceLocation(item + "_hand", "inventory");
            ResourceLocation item_hand = models.get(needsModel.get(i));


            IBakedModel itemModel_default = map.get(item_inv);
            IBakedModel itemModel_hand = map.get(item_hand);
            IBakedModel itemModel_3D = new IBakedModel() {
                @Override
                public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
                    return itemModel_default.getQuads(state, side, rand);
                }

                @Override
                public boolean isAmbientOcclusion() {
                    return itemModel_default.isAmbientOcclusion();
                }

                @Override
                public boolean isGui3d() {
                    return itemModel_default.isGui3d();
                }

                @Override
                public boolean func_230044_c_() {
                    return itemModel_default.func_230044_c_();
                }

                @Override
                public boolean isBuiltInRenderer() {
                    return itemModel_default.isBuiltInRenderer();
                }

                @Override
                public TextureAtlasSprite getParticleTexture() {
                    return itemModel_default.getParticleTexture();
                }

                @Override
                public ItemOverrideList getOverrides() {
                    return itemModel_default.getOverrides();
                }

                @Override
                public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
                    IBakedModel modelToUse = itemModel_default;
                    if(cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND ||
                            cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND ||
                            cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND ||
                            cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND){
                        modelToUse = itemModel_hand;
                    }
                    return ForgeHooksClient.handlePerspective(modelToUse, cameraTransformType, mat);
                }
            };
            map.put(item_inv, itemModel_3D);

        }


    }
}
