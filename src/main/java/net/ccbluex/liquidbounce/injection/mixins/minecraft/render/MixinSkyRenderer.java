/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 */
package net.ccbluex.liquidbounce.injection.mixins.minecraft.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleCustomAmbience;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import org.jspecify.annotations.NullMarked;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@NullMarked
@Mixin(SkyRenderer.class)
public abstract class MixinSkyRenderer {

    @WrapOperation(
        method = "extractRenderState",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/level/SkyRenderState;skyColor:I", opcode = Opcodes.PUTFIELD)
    )
    private void applyCustomSkyColor(SkyRenderState instance, int value, Operation<Void> original) {
        var ambientWorld = ModuleCustomAmbience.AmbientWorld.INSTANCE;
        if (ambientWorld.getRunning() && ambientWorld.resolveSkyTint().a() > 0) {
            value = ambientWorld.resolveSkyTint().argb();
        } else {
            var customSkyColor = ModuleCustomAmbience.SkyColor.INSTANCE;
            if (customSkyColor.getRunning()) {
                value = customSkyColor.getColor().argb();
            }
        }
        original.call(instance, value);
    }

    @WrapOperation(
        method = "extractRenderState",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/level/SkyRenderState;cloudColor:I", opcode = Opcodes.PUTFIELD),
        require = 0
    )
    private void applyCustomCloudColor(SkyRenderState instance, int value, Operation<Void> original) {
        var ambientWorld = ModuleCustomAmbience.AmbientWorld.INSTANCE;
        if (ambientWorld.getRunning() && ambientWorld.resolveCloudTint().a() > 0) {
            value = ambientWorld.resolveCloudTint().argb();
        }
        original.call(instance, value);
    }
}
