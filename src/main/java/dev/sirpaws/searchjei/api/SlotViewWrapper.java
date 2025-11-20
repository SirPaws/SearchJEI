package dev.sirpaws.searchjei.api;

public class SlotViewWrapper {
    private final IViewSlot view;
    private boolean enableOverlay = false;

    public SlotViewWrapper(IViewSlot view) {
        this.view = view;
    }

    public boolean isEnableOverlay() {
        return enableOverlay;
    }

    public void setEnableOverlay(boolean enableOverlay) {
        this.enableOverlay = enableOverlay;
    }

    public IViewSlot getView() {
        return view;
    }
}
