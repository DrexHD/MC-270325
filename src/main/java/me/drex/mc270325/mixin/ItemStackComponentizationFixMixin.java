package me.drex.mc270325.mixin;

import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemStackComponentizationFix.class)
public abstract class ItemStackComponentizationFixMixin {

    // This should be undone, because it will create invalid book items in snapshot 24w09a to 24w13a
    @ModifyConstant(method = "createFilteredText", constant = @Constant(stringValue = "raw"))
    private static String undoIncorrectFix(String constant) {
        return "text";
    }

}
