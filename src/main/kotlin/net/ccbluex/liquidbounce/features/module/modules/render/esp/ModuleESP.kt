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
package net.ccbluex.liquidbounce.features.module.modules.render.esp

import net.ccbluex.liquidbounce.config.ConfigSystem
import net.ccbluex.liquidbounce.features.misc.FriendManager
import net.ccbluex.liquidbounce.features.module.ClientModule
import net.ccbluex.liquidbounce.features.module.ModuleCategories
import net.ccbluex.liquidbounce.features.module.modules.render.esp.modes.Esp2DMode
import net.ccbluex.liquidbounce.features.module.modules.render.esp.modes.EspBoxMode
import net.ccbluex.liquidbounce.features.module.modules.render.esp.modes.EspGlowMode
import net.ccbluex.liquidbounce.features.module.modules.render.esp.modes.EspLegacy2DMode
import net.ccbluex.liquidbounce.render.GenericDistanceHSBColorMode
import net.ccbluex.liquidbounce.render.GenericEntityHealthColorMode
import net.ccbluex.liquidbounce.render.GenericRainbowColorMode
import net.ccbluex.liquidbounce.render.GenericStaticColorMode
import net.ccbluex.liquidbounce.render.engine.type.Color4b
import net.ccbluex.liquidbounce.utils.combat.EntityTaggingManager
import net.ccbluex.liquidbounce.utils.combat.shouldBeShown
import net.ccbluex.liquidbounce.utils.entity.RenderedEntities
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

/**
 * ESP module
 *
 * Allows you to see targets through walls.
 */
object ModuleESP : ClientModule("ESP", ModuleCategories.RENDER) {

    override val baseKey: String
        get() = "${ConfigSystem.KEY_PREFIX}.module.esp"

    val modes = choices("Mode", EspGlowMode, arrayOf(
        EspBoxMode,
        Esp2DMode,
        EspLegacy2DMode,
//        EspOutlineMode,
        EspGlowMode
    ))

    private val colorModes = choices("ColorMode", 0) {
        arrayOf(
            GenericDistanceHSBColorMode.entity(it),
            GenericEntityHealthColorMode(it),
            GenericStaticColorMode(it, Color4b.WHITE.with(a = 100)),
            GenericRainbowColorMode(it)
        )
    }
    private val invisibleColor by color("Invisible", Color4b.ORANGE)
    private val friendColor by color("Friends", Color4b.GREEN)

    internal val maximumDistance by float("MaximumDistance", 128F, 1F..512F)

    private enum class GlowPalette(override val tag: String) : net.ccbluex.liquidbounce.config.types.list.Tagged {
        ELECTRIC_CYAN("ElectricCyan"),
        HOT_PINK("HotPink"),
        CYAN_PINK_WAVE("CyanPinkWave")
    }

    private val glowPalette = enumChoice("GlowPalette", GlowPalette.CYAN_PINK_WAVE)
    private val glowBaseAlpha by int("GlowBaseAlpha", 140, 0..255)
    private val glowPulseStrength by float("GlowPulseStrength", 0.35f, 0f..1f)

    override fun onEnabled() {
        RenderedEntities.subscribe(this)
    }

    override fun onDisabled() {
        RenderedEntities.unsubscribe(this)
    }

    fun getColor(entity: LivingEntity): Color4b {
        if (modes.activeMode == EspGlowMode) {
            val phase = ((System.currentTimeMillis() % 2000L) / 2000.0 * (Math.PI * 2.0)).toFloat()
            val pulse = (kotlin.math.sin(phase) * 0.5f + 0.5f)
            val alpha = (glowBaseAlpha + (255 - glowBaseAlpha) * (pulse * glowPulseStrength)).toInt().coerceIn(0, 255)

            return when (glowPalette.get()) {
                GlowPalette.ELECTRIC_CYAN -> Color4b(0, 255, 255, alpha)
                GlowPalette.HOT_PINK -> Color4b(255, 30, 180, alpha)
                GlowPalette.CYAN_PINK_WAVE -> {
                    val t = pulse
                    Color4b(
                        r = (255f * t).toInt().coerceIn(0, 255),
                        g = (255f - 120f * t).toInt().coerceIn(0, 255),
                        b = 255,
                        a = alpha
                    )
                }
            }
        }

        if (entity.hurtTime > 0) {
            return Color4b.RED
        }

        if (entity.isInvisible) {
            return invisibleColor
        }

        if (entity is Player) {
            if (FriendManager.isFriend(entity) && friendColor.a > 0) {
                return friendColor
            }

            EntityTaggingManager.getTag(entity).color?.let { return it }
        }

        return colorModes.activeMode.getColor(entity)
    }

    /**
     * Check if the entity requires true sight to be shown with the current ESP mode
     */
    fun requiresTrueSight(entity: LivingEntity) =
        modes.activeMode.requiresTrueSight && entity.shouldBeShown()

}
