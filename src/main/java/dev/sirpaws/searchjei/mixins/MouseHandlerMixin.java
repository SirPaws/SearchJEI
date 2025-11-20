package dev.sirpaws.searchjei.mixins;

import dev.sirpaws.searchjei.ClickManager;
import dev.sirpaws.searchjei.GuiRenderer;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "onButton", at = @At("HEAD"))
    private void onButton(long window, MouseButtonInfo buttonInfo, int action, CallbackInfo cir) {
        if (buttonInfo.input() == 0) {
            switch (action) {
                case 1:
                    ClickManager.click();
                    break;
                case 0:
                    ClickManager.release();
                    break;
                default:
                    break;
            }
        }
    }
}
