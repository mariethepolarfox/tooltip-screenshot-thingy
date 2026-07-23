package me.marie.toolshot

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.resources.Identifier


data class TooltipRenderState(
    var lines: List<ClientTooltipComponent>,
    var background: Identifier?,
    var font: Font,
) {
    fun equals(
        lines: List<ClientTooltipComponent>,
        background:Identifier?,
        font: Font
    ): Boolean {
        return this.lines == lines && this.background == background && this.font == font
    }
}
