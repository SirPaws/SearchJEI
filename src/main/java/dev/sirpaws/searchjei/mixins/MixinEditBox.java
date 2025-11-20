package dev.sirpaws.searchjei.mixins;

import dev.sirpaws.searchjei.ClickManager;
import dev.sirpaws.searchjei.GuiRenderer;
import dev.sirpaws.searchjei.JeiEntry;
import dev.sirpaws.searchjei.SearchJEI;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.sirpaws.searchjei.SearchJEI.JEI_LOADED;
import static dev.sirpaws.searchjei.SearchJEI.LOGGER;
import static dev.sirpaws.searchjei.utils.MixinUtils.asTarget;

@Mixin(EditBox.class)
public abstract class MixinEditBox extends AbstractWidget {
    public MixinEditBox(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Inject(method = "onClick", at = @At("HEAD"))
    private void onClick(MouseButtonEvent event, boolean isDoubleClick, CallbackInfo cir) {
        EditBox textField = asTarget(this);

        if(JEI_LOADED && JeiEntry.getJEITextField() != null && textField.getClass() == JeiEntry.getJEITextField().getClass()){
            if (ClickManager.isDoubleClick) {
                GuiRenderer.INSTANCE.toggleMode();
                GuiRenderer.setQuery(textField.getValue());
            }
        }
    }
}
