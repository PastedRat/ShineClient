package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.ClientModule
import net.ccbluex.liquidbounce.features.module.ModuleCategories
import net.ccbluex.liquidbounce.render.engine.type.Color4b

object ModuleArmChams : ClientModule("ArmChams", ModuleCategories.RENDER) {

    private val gradient by boolean("Gradient", true)
    private val firstColor by color("Color1", Color4b(0, 180, 255, 180))
    private val secondColor by color("Color2", Color4b(173, 92, 255, 180))
    private val gradientSpeed by float("GradientSpeed", 1.6f, 0.1f..6f)
    private val alpha by int("Alpha", 180, 1..255)
    private val fullBright by boolean("FullBright", true)
    private val affectHeldItems by boolean("AffectHeldItems", true)

    fun shouldApply() = running

    fun isFullBright() = fullBright

    fun shouldAffectHeldItems() = affectHeldItems

    fun getActiveColor(): Color4b {
        val c = if (gradient) {
            val t = (System.currentTimeMillis() / 1000.0) * gradientSpeed
            val factor = ((kotlin.math.sin(t) * 0.5) + 0.5).toFloat()
            val inv = 1f - factor
            Color4b(
                (firstColor.r * inv + secondColor.r * factor).toInt().coerceIn(0, 255),
                (firstColor.g * inv + secondColor.g * factor).toInt().coerceIn(0, 255),
                (firstColor.b * inv + secondColor.b * factor).toInt().coerceIn(0, 255),
                alpha
            )
        } else {
            firstColor.with(a = alpha)
        }

        return c
    }
}
