package me.drex.mc270325.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class RenameFilterableFix extends DataFix {

    public RenameFilterableFix(Schema schema) {
        super(schema, false);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(References.ITEM_STACK);
        OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
        OpticFinder<?> opticFinder2 = type.findField("components");
        return this.fixTypeEverywhereTyped("ItemStack text to raw component fix", type, typed2 -> {
            Optional<Pair<String, String>> optional = typed2.getOptional(opticFinder);
            Optional<String> optional2 = optional.map(Pair::getSecond);
            if (optional2.isPresent()) {
                return typed2.updateTyped(opticFinder2, typed -> typed.update(DSL.remainderFinder(), RenameFilterableFix::fixComponent));
            }
            return typed2;
        });
    }

    private static <T> Dynamic<T> fixComponent(Dynamic<T> dynamic) {
        dynamic = dynamic.update("minecraft:writable_book_content", RenameFilterableFix::fixPages);
        dynamic = dynamic.update("minecraft:written_book_content", writtenBookContent -> {
            writtenBookContent = fixPages(writtenBookContent);
            writtenBookContent = writtenBookContent.update("title", RenameFilterableFix::fixFilterable);
            return writtenBookContent;
        });
        return dynamic;
    }

    private static <T> Dynamic<T> fixPages(Dynamic<T> dynamic) {
        return dynamic.update("pages", pagesDynamic -> {
            List<? extends Dynamic<?>> pages = pagesDynamic.asList(Function.identity());
            List<Dynamic<?>> fixedPages = new LinkedList<>();
            for (Dynamic<?> page : pages) {
                fixedPages.add(fixFilterable(page));
            }
            return pagesDynamic.createList(fixedPages.stream());
        });
    }

    private static <T> Dynamic<T> fixFilterable(Dynamic<T> dynamic) {
        return dynamic.renameField("text", "raw");
    }

}
