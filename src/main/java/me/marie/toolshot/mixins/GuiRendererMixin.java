package me.marie.toolshot.mixins;


import me.marie.toolshot.GuiRendererInterface;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.state.WindowRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.MappableRingBuffer;
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
public class GuiRendererMixin implements GuiRendererInterface {
    @Final
    @Shadow
    private List<GuiRenderer.Draw> draws;
    @Shadow
    private int firstDrawIndexAfterBlur;
    @Final
    @Shadow
    private List<GuiRenderer.MeshToDraw> meshesToDraw;
    @Final
    @Shadow
    GuiRenderState renderState;
    @Final
    @Shadow
    private Map<VertexFormat, MappableRingBuffer> vertexBuffers;

    @Final
    @Shadow
    private Projection guiProjection;
    @Final
    @Shadow
    private ProjectionMatrixBuffer guiProjectionMatrixBuffer;

    @Shadow
    private void prepare() {
    }

    @Shadow
    private void clearUnusedOversizedItemRenderers() {
    }

    @Shadow
    private void executeDrawRange(Supplier<String> supplier, RenderTarget arg, GpuBufferSlice gpuBufferSlice, GpuBufferSlice gpuBufferSlice2, GpuBuffer gpuBuffer, VertexFormat.IndexType arg2, int j, int k) {
    }

    @Unique
    void toolShot$draw(GpuBufferSlice gpuBufferSlice, RenderTarget renderTarget) {
        if (!this.draws.isEmpty()) {
            WindowRenderState windowState = Minecraft.getInstance().gameRenderer.getGameRenderState().windowRenderState;
            this.guiProjection.setupOrtho(1000.0F, 11000.0F, (float)windowState.width / (float)windowState.guiScale, (float)windowState.height / (float)windowState.guiScale, true);
            RenderSystem.setProjectionMatrix(this.guiProjectionMatrixBuffer.getBuffer(this.guiProjection), ProjectionType.ORTHOGRAPHIC);
            int i = 0;

            for (GuiRenderer.Draw guirenderer$draw : this.draws) {
                if (guirenderer$draw.indexCount > i) {
                    i = guirenderer$draw.indexCount;
                }
            }

            RenderSystem.AutoStorageIndexBuffer rendersystem$autostorageindexbuffer = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
            GpuBuffer gpubuffer = rendersystem$autostorageindexbuffer.getBuffer(i);
            VertexFormat.IndexType vertexformat$indextype = rendersystem$autostorageindexbuffer.type();
            GpuBufferSlice gpubufferslice = RenderSystem.getDynamicUniforms().writeTransform(
                    (new Matrix4f()).setTranslation(0.0F, 0.0F, -11000.0F),
                    new Vector4f(1.0F, 1.0F, 1.0F, 1.0F),
                    new Vector3f(),
                    new Matrix4f()
            );
            if (this.firstDrawIndexAfterBlur > 0) {
                this.executeDrawRange(() -> "GUI before blur", renderTarget, gpuBufferSlice, gpubufferslice, gpubuffer, vertexformat$indextype, 0, Math.min(this.firstDrawIndexAfterBlur, this.draws.size()));
            }

            if (this.draws.size() > this.firstDrawIndexAfterBlur) {
                RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(renderTarget.getDepthTexture(), (double) 1.0F);
                this.executeDrawRange(() -> "GUI after blur", renderTarget, gpuBufferSlice, gpubufferslice, gpubuffer, vertexformat$indextype, this.firstDrawIndexAfterBlur, this.draws.size());
            }
        }
    }

    @Override
    public void toolShot$render(GpuBufferSlice gpuBufferSlice, RenderTarget renderTarget) {
        this.prepare();
        this.toolShot$draw(gpuBufferSlice, renderTarget);

        for (MappableRingBuffer mappableringbuffer : this.vertexBuffers.values()) {
            mappableringbuffer.rotate();
        }

        this.draws.clear();
        this.meshesToDraw.clear();
        this.renderState.reset();
        this.firstDrawIndexAfterBlur = Integer.MAX_VALUE;
        this.clearUnusedOversizedItemRenderers();
    }
}
