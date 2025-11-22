package dev.sirpaws.searchjei;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.sirpaws.searchjei.api.SlotHandler;
import dev.sirpaws.searchjei.api.SlotViewWrapper;
import dev.sirpaws.searchjei.query.Query;
import dev.sirpaws.searchjei.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;

import java.util.Map;
import java.util.Optional;

import static dev.sirpaws.searchjei.SearchJEI.CONFIG;

public class GuiRenderer {
    public static final GuiRenderer INSTANCE = new GuiRenderer();
    public static Optional<TooltipParameters> LAST_TOOLTIP = Optional.empty();
    public static boolean IS_RENDERING = false;

    private static final float FRAME_RADIUS = 1.0F;

    private static boolean enabled = false;

    private static String lastFilterText = "";
    private static boolean emptyFilter = true;
    private static BiMap<Slot, SlotViewWrapper> views = HashBiMap.create();

    private boolean allowRender = false;
    private int guiOffsetX = 0;
    private int guiOffsetY = 0;
    private boolean drewOverlayInTooltipPhase = false;
    private boolean drewFrameInTooltipPhase = false;

    private String query = "";

    public static void setQuery(String query) {
        INSTANCE.query = query;
    }

    public void guiInit(Screen gui) {
        if (!canShowIn(gui)) {
            return;
        }

        guiOffsetX = GuiUtils.getGuiLeft((AbstractContainerScreen<?>) gui);
        guiOffsetY = GuiUtils.getGuiTop((AbstractContainerScreen<?>) gui);

        if (enabled && gui instanceof AbstractContainerScreen<?>) {
            checkSlots((AbstractContainerScreen<?>) gui);
        }
    }

    public void guiOpen(Screen gui) {

    }

    public void preDraw(GuiGraphics guiGraphics) {
        LAST_TOOLTIP = Optional.empty();
        Screen guiscr = Minecraft.getInstance().screen;
        if (canShowIn(guiscr)) {
            allowRender = true;
            IS_RENDERING = true;
        }
    }

    public void postDraw(GuiGraphics guiGraphics) {
        Screen guiscr = Minecraft.getInstance().screen;

        if (allowRender && canShowIn(guiscr)) {
            allowRender = false;
            if (enabled) {
                EditBox textField = JeiEntry.getJEITextField();
                if (textField != null) {
                    if (!drewFrameInTooltipPhase) {
                        drawSearchFrame(textField, guiGraphics);
                    }
                }
            }
            if (!drewOverlayInTooltipPhase) {
                drawSlotOverlay(guiGraphics, (AbstractContainerScreen<?>) guiscr);
            }
            drewOverlayInTooltipPhase = false;
            drewFrameInTooltipPhase = false;
            if (LAST_TOOLTIP.isPresent()) {
                LAST_TOOLTIP.get().render(guiGraphics);
                LAST_TOOLTIP = Optional.empty();
            }

            IS_RENDERING = false;
        }
    }

    private void drawSearchFrame(EditBox textField, GuiGraphics guiGraphics) {
        int x = textField.getX() - 2;
        int y = textField.getY() - 4;
        int width = textField.getWidth() + 8;
        int height = textField.getHeight() - 4;

        int boxColor = CONFIG.search_searchBoxColor.getRGB();
        int rgb = boxColor & 0xFFFFFF;
        int color = 0xFF000000 | rgb;

        int left = x - (int) FRAME_RADIUS;
        int top = y - (int) FRAME_RADIUS;
        int right = x + width + (int) FRAME_RADIUS;
        int bottom = y + height + (int) FRAME_RADIUS;

        guiGraphics.fill(left, top, right, y, color);
        guiGraphics.fill(left, y + height, right, bottom, color);
        guiGraphics.fill(left, y, x, y + height, color);
        guiGraphics.fill(x + width, y, right, y + height, color);
    }

    public void renderTooltip(GuiGraphics guiGraphics) {
        Screen guiscr = Minecraft.getInstance().screen;
        if (enabled && canShowIn(guiscr)) {
            EditBox textField = JeiEntry.getJEITextField();
            if (textField != null) {
                drawSearchFrame(textField, guiGraphics);
                drewFrameInTooltipPhase = true;
            }
            drawSlotOverlay(guiGraphics, (AbstractContainerScreen<?>) guiscr);
            drewOverlayInTooltipPhase = true;
        }
    }

    private void drawSlotOverlay(GuiGraphics guiGraphics, AbstractContainerScreen<?> gui) {
        if (!enabled || views == null || views.isEmpty())
            return;

        float r = (float) CONFIG.search_filteredSlotColor.getRed()   / 255F;
        float g = (float) CONFIG.search_filteredSlotColor.getGreen() / 255F;
        float b = (float) CONFIG.search_filteredSlotColor.getBlue()  / 255F;
        float a = (float) CONFIG.search_filteredSlotTransparency;

        for (Map.Entry<Slot, SlotViewWrapper> entry : views.entrySet()) {
            if (entry.getValue().isEnableOverlay()) {
                Vec2 pos = entry.getValue().getView().getRenderPos(guiOffsetX, guiOffsetY);
                int left = Math.round(pos.x) + guiOffsetX;
                int top = Math.round(pos.y) + guiOffsetY;
                int right = left + 16;
                int bottom = top + 16;
                int argb = ((int)(a * 255.0f) << 24) | ((int)(r * 255.0f) << 16) | ((int)(g * 255.0f) << 8) | (int)(b * 255.0f);
                guiGraphics.fill(left, top, right, bottom, argb);
            }
        }
    }

    public boolean canShowIn(Screen gui) {
        return (gui instanceof AbstractContainerScreen<?>) && ((AbstractContainerScreen<?>) gui).getMenu() != null && !((AbstractContainerScreen<?>) gui).getMenu().slots.isEmpty();
    }

    private void checkSlots(AbstractContainerScreen<?> container) {
        if (views == null) {
            views = HashBiMap.create();
        } else {
            views.clear();
        }
        Query q = QueryBuilder.fromText(lastFilterText);
        for (Slot slot : container.getMenu().slots) {
            SlotViewWrapper wrapper;
            if (!views.containsKey(slot)) {
                wrapper = new SlotViewWrapper(SlotHandler.INSTANCE.getViewSlot(container, slot));
                views.put(slot, wrapper);
            } else {
                wrapper = views.get(slot);
            }

            wrapper.setEnableOverlay(wrapper.getView().canSearch() && !isSearchedItem(q, slot.getItem()));
        }
    }

    private boolean isSearchedItem(Query q, ItemStack stack) {
        if (emptyFilter) return true;
        else if (stack.isEmpty()) return false;
        if (q.matches(stack)) return true;
        return JeiEntry.filter.getFilteredItemStacks().stream().anyMatch(i -> JeiEntry.areItemsEqualInterpreter(i, stack));
    }

    public void tick() {
        final Screen screen = Minecraft.getInstance().screen;
        if (!canShowIn(screen))
            return;
        if (enabled && JeiEntry.filter != null && !JeiEntry.filter.getFilterText().equals(lastFilterText)) {
            lastFilterText = JeiEntry.filter.getFilterText();
            emptyFilter = lastFilterText.replace(" ", "").isEmpty();
        }

        if (enabled && screen instanceof AbstractContainerScreen<?>) {
            checkSlots((AbstractContainerScreen<?>) screen);
            guiOffsetX = GuiUtils.getGuiLeft((AbstractContainerScreen<?>) screen);
            guiOffsetY = GuiUtils.getGuiTop((AbstractContainerScreen<?>) screen);
        } else if (views != null) {
            views.clear();
        }
    }

    public void toggleMode() {
        enabled = !enabled;
        if (enabled && JeiEntry.filter != null) {
            lastFilterText = JeiEntry.filter.getFilterText();
            emptyFilter = lastFilterText.replace(" ", "").isEmpty();
        } else {
            lastFilterText = "";
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
}
