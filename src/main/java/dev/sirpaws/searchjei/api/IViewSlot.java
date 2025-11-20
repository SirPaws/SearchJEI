package dev.sirpaws.searchjei.api;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.phys.Vec2;

public interface IViewSlot {
    /*
     * The Slot
     */
    Slot slot();

    /*
     * Position offset for the Gui
     */
    Vec2 getRenderPos(int guiLeft, int guiTop);

    /*
     * false if the ItemSearch should ignore this slot
     */
    boolean canSearch();
}
