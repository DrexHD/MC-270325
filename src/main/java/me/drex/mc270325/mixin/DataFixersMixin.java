package me.drex.mc270325.mixin;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import me.drex.mc270325.datafix.fixes.RenameFilterableFix;
import net.minecraft.util.datafix.DataFixers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiFunction;

@Mixin(DataFixers.class)
public abstract class DataFixersMixin {

    @Shadow
    @Final
    private static BiFunction<Integer, Schema, Schema> SAME_NAMESPACED;

    // Add fixer for the correct version
    @Inject(method = "addFixers", at = @At(value = "TAIL"))
    private static void add3827Schema(DataFixerBuilder dataFixerBuilder, CallbackInfo ci) {
        Schema schema = dataFixerBuilder.addSchema(3827, SAME_NAMESPACED);
        dataFixerBuilder.addFixer(new RenameFilterableFix(schema));
    }

}
