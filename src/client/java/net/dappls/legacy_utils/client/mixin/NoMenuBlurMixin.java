package net.dappls.legacy_utils.client.mixin;

import net.dappls.legacy_utils.client.GUI.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class NoMenuBlurMixin {
    @Inject(method = "renderBlur", at = @At("HEAD"), cancellable = true)
    private void disableMenuBlur(CallbackInfo ci) {
        var current = MinecraftClient.getInstance().currentScreen;
        if (current instanceof ModMenu ||
                current instanceof BinaryModMenu ||
                current instanceof HoneyMenu ||
                current instanceof InfoMenu ||
                current instanceof IngenuityMenu ||
                current instanceof LightsOutMenu ||
                current instanceof SevenxSevenMenu ||
                current instanceof TogglesMenu) {

            ci.cancel();
        }

    }
}