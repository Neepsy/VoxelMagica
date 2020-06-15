package com.neepsy.voxelmagica.entity.Rendering;
// Made with Blockbench 3.5.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.neepsy.voxelmagica.entity.InfuseProjectileEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InfuseProjectileModel extends EntityModel<InfuseProjectileEntity> {
	private final ModelRenderer main;

	public InfuseProjectileModel() {
		textureWidth = 16;
		textureHeight = 16;

		main = new ModelRenderer(this);
		main.setRotationPoint(0.0F, 24.0F, 0.0F);
		
	}

	@Override
	public void setRotationAngles(InfuseProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		main.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	public void rotate(float x, float y, float z) {
		this.main.rotateAngleY = y * ((float)Math.PI / 180F);
		this.main.rotateAngleX = z * ((float)Math.PI / 180F);
	}


}