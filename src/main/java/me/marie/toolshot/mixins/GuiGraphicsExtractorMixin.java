package me.marie.toolshot.mixins;

import me.marie.toolshot.TooltipRenderState;
import me.marie.toolshot.TooltipUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsExtractorMixin {

    @Inject(
            method = "setTooltipForNextFrameInternal",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;deferredTooltip:Ljava/lang/Runnable;", opcode = Opcodes.PUTFIELD)
    )
    private void onRenderTooltip(
            Font font,
            List<ClientTooltipComponent> lines,
            int xo,
            int yo,
            ClientTooltipPositioner positioner,
            Identifier style,
            boolean replaceExisting,
            CallbackInfo ci
    ) {
        var lastState = TooltipUtil.INSTANCE.getLastState();

        if (lastState == null || !lastState.equals(lines, style, font)) {
            TooltipUtil.INSTANCE.setLastState(new TooltipRenderState(lines, style, font));
        }
    }
}
