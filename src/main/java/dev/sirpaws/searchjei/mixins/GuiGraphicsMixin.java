package dev.sirpaws.searchjei.mixins;

import dev.sirpaws.searchjei.GuiRenderer;
import dev.sirpaws.searchjei.TooltipParameters;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    @Inject(at = @At("HEAD"), method = "renderTooltip")
    public void renderTooltip(Font font, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner, @Nullable ResourceLocation background, CallbackInfo cir) {
        if (GuiRenderer.IS_RENDERING)
            GuiRenderer.LAST_TOOLTIP = Optional.of(new TooltipParameters(font, components, x, y, positioner, background));
    }
}
