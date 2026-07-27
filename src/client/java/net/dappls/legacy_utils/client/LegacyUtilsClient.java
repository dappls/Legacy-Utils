package net.dappls.legacy_utils.client;

import net.dappls.legacy_utils.client.core.FeatureManager;
import net.dappls.legacy_utils.client.core.LegacyLog;
import net.dappls.legacy_utils.client.core.Safe;
import net.dappls.legacy_utils.client.feature.binary.BinaryFeature;
import net.dappls.legacy_utils.client.feature.honey.HoneyFeature;
import net.dappls.legacy_utils.client.feature.ingenuity.IngenuityFeature;
import net.dappls.legacy_utils.client.feature.sevenxseven.SevenxSevenFeature;
import net.dappls.legacy_utils.client.input.KeyBindings;
import net.fabricmc.api.ClientModInitializer;

/**
 * Client entry point.
 *
 * <p>Deliberately tiny: all it does is register the key binding and hand a list of
 * {@link net.dappls.legacy_utils.client.core.Feature}s to the manager. Puzzle helpers that are
 * purely menu-driven (Ithil, lights out, 7x7 grid entry) need no listeners and so are not listed
 * here — their screens call into their solver directly.
 */
public class LegacyUtilsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Safe.run("Client initialisation", () -> {
            KeyBindings.register();

            FeatureManager.registerAll(
                    new BinaryFeature(),
                    new HoneyFeature(),
                    new IngenuityFeature(),
                    new SevenxSevenFeature()
            );

            LegacyLog.info("Legacy Utils client ready.");
        });
    }
}
