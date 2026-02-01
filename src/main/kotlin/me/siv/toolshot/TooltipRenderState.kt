package me.siv.toolshot

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
//? if < 1.21.11 {
/*import net.minecraft.resources.ResourceLocation*/
//? } else {
import net.minecraft.resources.Identifier
//? }


data class TooltipRenderState(
    var lines: List<ClientTooltipComponent>,
    var background: /*? if < 1.21.11 {*//*ResourceLocation?*//*? } else {*/Identifier?/*? }*/,
    var font: Font,
) {
    fun equals(
        lines: List<ClientTooltipComponent>,
        background: /*? if < 1.21.11 {*//*ResourceLocation?*//*? } else {*/Identifier?/*? }*/,
        font: Font
    ): Boolean {
        return this.lines == lines && this.background == background && this.font == font
    }
}
