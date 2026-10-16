package com.ejafi;

import com.ejafi.lang.TranslationProvider;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Pathlighter.MOD_ID)
public final class PathlighterData {
    private PathlighterData() {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        for (TranslationProvider provider : TranslationProvider.createAll(generator.getPackOutput())) {
            generator.addProvider(event.includeClient(), provider);
        }
    }
}
