package net.dappls.legacy_utils.client.Binary;

import net.dappls.legacy_utils.client.Util.ChatUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ButtonHandler {
    enum BUTTONSTATES {
        WFB1,
        WFB2,
        WFB3
    }

    private static final List<BlockPos> Buttons = new ArrayList<>(Arrays.asList(
            new BlockPos(100, 100, 100),
            new BlockPos(101, 100, 100),
            new BlockPos(102, 100, 100),
            new BlockPos(103, 100, 100)
    ));
    private static int WhatButtonPressed;

    private static BUTTONSTATES State = BUTTONSTATES.WFB1;


    private static void ClearRendering() {
        for (int i = 0; i < 4; i++) {
            MinecraftClient client = MinecraftClient.getInstance();
            BlockState StoneButton = Blocks.STONE_BUTTON.getDefaultState();
            if (client.world != null) {
                client.world.setBlockState(Buttons.get(i), StoneButton);
            }
        }
    }

    private static void RenderButton(int Button) {
            MinecraftClient client = MinecraftClient.getInstance();
            BlockState StoneButton = Blocks.STONE_BUTTON.getDefaultState();
            if (client.world != null) {
                client.world.setBlockState(Buttons.get(Button), StoneButton);
        }
    }

    private static void UpdateState() {
        switch (State) {
            case WFB1:
                if (WhatButtonPressed == 2) {
                  State = BUTTONSTATES.WFB2;
                  ClearRendering();
                  RenderButton(1);
                  ChatUtils.sendClientMessage(String.valueOf(State));
                }
                break;
            case WFB2:
                if (WhatButtonPressed == 3) {
                    //update rendering
                    State = BUTTONSTATES.WFB3;
                    ClearRendering();
                    RenderButton(2);
                    ChatUtils.sendClientMessage(String.valueOf(State));
                }
                else {
                    State = BUTTONSTATES.WFB1;
                    ClearRendering();
                    RenderButton(0);
                    ChatUtils.sendClientMessage(String.valueOf(State));
                }

                break;
            case WFB3:
                if (WhatButtonPressed == 4) {
                    State = BUTTONSTATES.WFB1;
                    ClearRendering();
                    RenderButton(0);
                    ChatUtils.sendClientMessage(String.valueOf(State));
                    ChatUtils.sendClientMessage("YOU DID IT!");

                    //exit stuff
                }
                else {
                    State = BUTTONSTATES.WFB1;
                    ClearRendering();
                    RenderButton(0);
                    ChatUtils.sendClientMessage(String.valueOf(State));
                }
                break;

        }

    }



    public static void register() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.crosshairTarget == null) return;

            if (client.crosshairTarget instanceof BlockHitResult hit) {

                BlockPos pos = hit.getBlockPos();

                if (client.world != null) {
                    BlockState powered = (Blocks.STONE_BUTTON.getDefaultState().with(Properties.POWERED, true));

                        if (pos.equals(Buttons.get(0)) && client.options.useKey.isPressed() && powered == client.world.getBlockState(Buttons.get(0))) {
                            WhatButtonPressed = 1;
                            ChatUtils.sendClientMessage("You pressed button " + WhatButtonPressed);

                        }

                        if (pos.equals(Buttons.get(1))  && client.options.useKey.isPressed() && powered == client.world.getBlockState(Buttons.get(1))) {
                            WhatButtonPressed = 2;
                            ChatUtils.sendClientMessage("You pressed button " + WhatButtonPressed);
                        }

                        if (pos.equals(Buttons.get(2)) && client.options.useKey.isPressed() && powered == client.world.getBlockState(Buttons.get(2))) {
                            WhatButtonPressed = 3;
                            ChatUtils.sendClientMessage("You pressed button " + WhatButtonPressed);
                        }

                        if (pos.equals(Buttons.get(3))&& client.options.useKey.isPressed() && powered == client.world.getBlockState(Buttons.get(3))) {
                            WhatButtonPressed = 4;
                            ChatUtils.sendClientMessage("You pressed button " + WhatButtonPressed);
                        }
                        UpdateState();
                    }
                }
        });
    }
}
