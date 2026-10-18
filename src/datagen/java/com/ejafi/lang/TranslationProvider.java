package com.ejafi.lang;

import com.ejafi.Pathlighter;
import java.util.List;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public final class TranslationProvider extends LanguageProvider {
    private final Translation translation;

    private TranslationProvider(PackOutput output, Translation translation) {
        super(output, Pathlighter.MOD_ID, translation.locale());
        this.translation = translation;
    }

    public static List<TranslationProvider> createAll(PackOutput output) {
        return Translation.ALL.stream()
                .map(translation -> new TranslationProvider(output, translation))
                .toList();
    }

    @Override
    protected void addTranslations() {
        translation.entries().forEach(this::add);
    }
}
