package me.siv.toolshot

import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig
import me.siv.toolshot.config.Config
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier
import org.lwjgl.glfw.GLFW
import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val MODID = "toolshot"

object Toolshot : ClientModInitializer, Logger by LoggerFactory.getLogger(MODID) {
    val mc: Minecraft = Minecraft.getInstance()

    val configurator = Configurator("toolshot")
    var config: ResourcefulConfig? = null

    private val categoryResource = Identifier.fromNamespaceAndPath(MODID, "main")
    val CATEGORY: KeyMapping.Category = KeyMapping.Category.register(categoryResource)

    val COPY: KeyMapping = KeyMappingHelper.registerKeyMapping(
        KeyMapping(
            "key.toolshot.copy",
            GLFW.GLFW_KEY_UNKNOWN,
            CATEGORY,
        )
    )

    override fun onInitializeClient() {
        config = Config.register(configurator)
        ScreenEvents.BEFORE_INIT.register { _, screen, _, _ ->
            ScreenKeyboardEvents.allowKeyPress(screen)
                .register { _, event  ->
                    if (COPY.matches(event)) {
                        val state = TooltipUtil.lastState ?: return@register true
                        TooltipUtil.copyTooltipToClipboard(state)
                        return@register false
                    }
                    return@register true
                }
        }
    }
}