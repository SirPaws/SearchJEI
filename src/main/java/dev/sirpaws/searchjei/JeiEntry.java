package dev.sirpaws.searchjei;

import dev.sirpaws.searchjei.utils.ReflectionUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.runtime.IIngredientFilter;
import mezz.jei.api.runtime.IIngredientListOverlay;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class JeiEntry implements IModPlugin {
    public static IIngredientListOverlay overlay;
    public static IIngredientFilter filter;
    private static IJeiHelpers jeiHelpers;
    private static EditBox textField;
    private static boolean missingTextFieldLogged = false;

    public static void updateModule() {
        if (overlay != null) {
            EditBox found = ReflectionUtil.findFieldsWithClass(overlay, EditBox.class)
                    .findFirst()
                    .orElse(null);
            if (found == null) {
                textField = null;
                if (!missingTextFieldLogged) {
                    SearchJEI.LOGGER.warn("JEI search text field not found via reflection (possibly due to other UI mods). Using fallback: no text field.");
                    missingTextFieldLogged = true;
                }
            } else {
                textField = found;
                missingTextFieldLogged = false;
            }
        } else {
            textField = null;
            missingTextFieldLogged = false;
        }
    }

    public static EditBox getJEITextField() {
        return textField;
    }

    public static boolean logMissingSearchTextFieldOnce() {
        if (overlay != null && textField == null && !missingTextFieldLogged) {
            SearchJEI.LOGGER.warn("JEI search text field not found via reflection (possibly due to other UI mods). Using fallback: no text field.");
            missingTextFieldLogged = true;
            return true;
        }
        return false;
    }

    public static boolean areItemsEqualInterpreter(ItemStack stack1, ItemStack stack2) {
        if (jeiHelpers == null) {
            return ItemUtils.matchNBT(stack1, stack2);
        }
        return jeiHelpers.getStackHelper().isEquivalent(stack1, stack2, UidContext.Ingredient);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        overlay = jeiRuntime.getIngredientListOverlay();
        filter = jeiRuntime.getIngredientFilter();
        missingTextFieldLogged = false;
        updateModule();
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        jeiHelpers = registration.getJeiHelpers();
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(SearchJEI.MOD_ID, "jei_module");
    }
}
