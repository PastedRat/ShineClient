/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2015 - 2026 CCBlueX
 *
 * LiquidBounce is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LiquidBounce is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LiquidBounce. If not, see <https://www.gnu.org/licenses/>.
 */

package net.ccbluex.liquidbounce.injection.mixins.minecraft.gui;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.ccbluex.liquidbounce.common.ClientLogoTexture;
import net.ccbluex.liquidbounce.event.EventManager;
import net.ccbluex.liquidbounce.event.events.ScreenRenderEvent;
import net.ccbluex.liquidbounce.features.misc.HideAppearance;
import net.ccbluex.liquidbounce.render.ClientRenderPipelines;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.IntSupplier;

/**
 * LiquidBounce Splash Screen
 */
@Mixin(LoadingOverlay.class)
public abstract class MixinLoadingOverlay {

    @Unique
    private static final int TEXT_COLOR = ARGB.color(255, 245, 251, 255);

    @Unique
    private static final int SUBTITLE_COLOR = ARGB.color(210, 190, 220, 255);

    @Unique
    private static final int LOGO_WIDTH = 180;

    @Inject(method = "registerTextures", at = @At("RETURN"))
    private static void initializeTexture(TextureManager textureManager, CallbackInfo ci) {
        textureManager.registerAndLoad(ClientLogoTexture.CLIENT_LOGO, new ClientLogoTexture());
    }

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void render(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        EventManager.INSTANCE.callEvent(new ScreenRenderEvent(context, delta));
    }

    @WrapWithCondition(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIIIIII)V"))
    private boolean drawMojangLogo(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier sprite, int x, int y, float u, float v, int width, int height, int regionWidth, int regionHeight, int textureWidth, int textureHeight, int color) {
        return !sprite.getPath().contains("mojangstudios");
    }

    @Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/ReloadInstance;getActualProgress()F"))
    private void drawClientLogo(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float a,
            CallbackInfo ci,
            @Local(name = "color") int color
    ) {
        // Don't draw the logo if the appearance is hidden
        if (HideAppearance.INSTANCE.isHidingNow()) {
            return;
        }

        int centerX = graphics.guiWidth() / 2;
        int centerY = graphics.guiHeight() / 2;
        var font = Minecraft.getInstance().font;

        int logoHeight = Math.max(1, (int) (LOGO_WIDTH * (ClientLogoTexture.HEIGHT / (float) ClientLogoTexture.WIDTH)));

        graphics.blit(
                ClientRenderPipelines.JCEF.SMOOTH_TEXTURE,
                ClientLogoTexture.CLIENT_LOGO,
                centerX - LOGO_WIDTH / 2,
                centerY - 110,
                0.0F,
                0.0F,
                LOGO_WIDTH,
                logoHeight,
                ClientLogoTexture.WIDTH,
                ClientLogoTexture.HEIGHT,
                ClientLogoTexture.WIDTH,
                ClientLogoTexture.HEIGHT,
                color
        );
        graphics.centeredText(font, Component.literal("SHINE CLIENT"), centerX, centerY - 8, TEXT_COLOR);
        graphics.centeredText(font, Component.literal("Loading the stars and preparing your client..."), centerX, centerY + 14, SUBTITLE_COLOR);
    }

    @ModifyExpressionValue(method = "extractRenderState", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/LoadingOverlay;BRAND_BACKGROUND:Ljava/util/function/IntSupplier;", opcode = Opcodes.GETSTATIC))
    private IntSupplier withClientColor(IntSupplier original) {
        return original;
    }

}
