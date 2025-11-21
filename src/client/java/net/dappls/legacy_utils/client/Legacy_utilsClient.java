package net.dappls.legacy_utils.client;

import net.dappls.legacy_utils.Toggles.DungeonParticleTrail;
import net.dappls.legacy_utils.Toggles.SpiritParticleTrail;
import net.dappls.legacy_utils.Toggles.WaterParticleTrail;
import net.dappls.legacy_utils.client.Binary.BinarySolver;
import net.dappls.legacy_utils.client.Binary.ButtonHandler;
import net.dappls.legacy_utils.client.Honey.HoneySolver;

import net.dappls.legacy_utils.client.Ingenuity.PuzzleListeners;
import net.dappls.legacy_utils.client.Ingenuity.RenderPuzzle;
import net.dappls.legacy_utils.client.SevenxSeven.GlowingOutlineRenderer;
import net.dappls.legacy_utils.client.Util.RegisterKeyBinds;
import net.fabricmc.api.ClientModInitializer;


public class Legacy_utilsClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        GlowingOutlineRenderer.register();
        RegisterKeyBinds.init();
        ButtonHandler.register();
        HoneySolver.registerTrailListener();
        GlowingOutlineRenderer.register();
        PuzzleListeners.register();
        RenderPuzzle.register();
        WaterParticleTrail.registerTrailListener();
        WaterParticleTrail.register();
        DungeonParticleTrail.register();
        SpiritParticleTrail.register();
        BinarySolver.registerListeners();

    }
}
