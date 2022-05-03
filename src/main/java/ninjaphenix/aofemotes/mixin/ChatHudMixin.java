package ninjaphenix.aofemotes.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import ninjaphenix.aofemotes.Constants;
import ninjaphenix.aofemotes.render.EmoteRenderHelper;
import ninjaphenix.aofemotes.render.RenderEmote;
import ninjaphenix.aofemotes.text.TextReaderVisitor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(value = {ChatHud.class}, priority = 1010)
public abstract class ChatHudMixin {
    @Shadow @Final private MinecraftClient client;

    @ModifyArgs(
            method = {"render"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/OrderedText;FFI)I"
            )
    )
    private void drawEmotes(Args args) {
        MatrixStack matrices = args.get(0);
        OrderedText text = args.get(1);
        float x = args.get(2);
        float y = args.get(3);
        int color = args.get(4);

        TextReaderVisitor textReaderVisitor = new TextReaderVisitor();
        text.accept(textReaderVisitor);
        float emoteSize = (float) this.client.textRenderer.getWidth(Constants.EMOTE_PLACEHOLDER);
        float emoteAlpha = (float) (color >> 24 & 255) / 255.0f;
        List<RenderEmote> renderEmoteList = EmoteRenderHelper.extractEmotes(textReaderVisitor, this.client.textRenderer, x, y);

        // draw emotes
        matrices.push();
        matrices.translate(0.0D, -0.5D, 0.0D);
        for (RenderEmote renderEmote : renderEmoteList) {
            EmoteRenderHelper.drawEmote(matrices, renderEmote, emoteSize, emoteAlpha, 1.05F, 1.5F);
        }
        matrices.pop();

        // adjust text
        args.set(1, textReaderVisitor.getOrderedText());
    }
}
