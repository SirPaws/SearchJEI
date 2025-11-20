package dev.sirpaws.searchjei;

import org.spongepowered.asm.mixin.Unique;

public class ClickManager {
    public static final ClickManager INSTANCE = new ClickManager();
    public static boolean isPressed = false;
    public static boolean isReleased = false;
    public static boolean isDoubleClick = false;

    public static void click() {
        INSTANCE.handleClick();
    }

    public static void release() {
        INSTANCE.handleReleased();
    }

    private long clickTime = 0;

    private void handleClick() {
        if (isReleased) {
            long now = System.currentTimeMillis();
            if (Math.abs(now - clickTime) < 500) {
                isDoubleClick = true;
                isPressed = false;
                isReleased = false;
            } else {
                isPressed = true;
                isReleased = false;
                isDoubleClick = false;
            }
        } else {
            isPressed = true;
            isDoubleClick = false;
        }
        clickTime = System.currentTimeMillis();
    }
    private void handleReleased() {
        isPressed = false;
        isDoubleClick = false;
        isReleased = true;
    }
}
