package me.marie.toolshot.mixins;

import me.marie.toolshot.TooltipUtil;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {


    @Inject(method = "render", at = @At("HEAD"))
    void prepare(CallbackInfo ci) {
        TooltipUtil.INSTANCE.setLastState(null);
    }

}
