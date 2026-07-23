package me.marie.toolshot.mixins;


import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.marie.toolshot.GuiRendererInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.state.WindowRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(GuiRenderer.class)
public abstract class GuiRendererMixin implements GuiRendererInterface {
    @Final
    @Shadow
    private List<GuiRenderer.Draw> draws;
    @Shadow
    private int firstDrawIndexAfterBlur;
    @Final
    @Shadow
    private ProjectionMatrixBuffer guiProjectionMatrixBuffer;
    @Final
    @Shadow
    private GuiRenderState renderState;

    @Shadow
    private void prepare() {
    }

    @Shadow
    private void clearUnusedOversizedItemRenderers() {
    }

    @Final
    @Shadow
    private StagedVertexBuffer vertexBuffer;

    @Shadow
    private void executeDrawRange(final Supplier<String> label, final RenderTarget mainRenderTarget, final GpuBufferSlice dynamicTransforms, final int startIndex, final int endIndex) {
    }

    @Shadow
    @Final
    private Projection guiProjection;

    @Shadow
    public abstract void render();

    @Unique
    void toolShot$draw(GpuBufferSlice gpuBufferSlice, RenderTarget renderTarget) {
        if (!this.draws.isEmpty()) {
            Minecraft minecraft = Minecraft.getInstance();
            WindowRenderState windowState = minecraft.gameRenderer.gameRenderState().windowRenderState;
            this.guiProjection.setupOrtho(
                    1000.0F, 11000.0F,
                    (float)windowState.width / (float)windowState.guiScale,
                    (float)windowState.height / (float)windowState.guiScale,
                    true
            );
            RenderSystem.setProjectionMatrix(this.guiProjectionMatrixBuffer.getBuffer(this.guiProjection), ProjectionType.ORTHOGRAPHIC);
            GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform((new Matrix4f()).setTranslation(0.0F, 0.0F, -11000.0F));

            if (this.firstDrawIndexAfterBlur > 0) {
                this.executeDrawRange(() -> "GUI before blur", renderTarget, dynamicTransforms, 0, Math.min(this.firstDrawIndexAfterBlur, this.draws.size()));
            }

            if (this.draws.size() > this.firstDrawIndexAfterBlur) {
                RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(renderTarget.getDepthTexture(), 0.0F);
                minecraft.gameRenderer.processBlurEffect();
                this.executeDrawRange(() -> "GUI after blur", renderTarget, dynamicTransforms, this.firstDrawIndexAfterBlur, this.draws.size());

            }
        }
    }

    @Override
    public void toolShot$render(GpuBufferSlice gpuBufferSlice, RenderTarget renderTarget) {
        this.prepare();
        this.vertexBuffer.upload();

        this.toolShot$draw(gpuBufferSlice, renderTarget);

        this.vertexBuffer.endDraw();
        this.vertexBuffer.endFrame();

        this.draws.clear();
        this.renderState.reset();
        this.firstDrawIndexAfterBlur = Integer.MAX_VALUE;
        this.clearUnusedOversizedItemRenderers();
    }
}
