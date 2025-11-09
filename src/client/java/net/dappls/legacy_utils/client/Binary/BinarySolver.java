package net.dappls.legacy_utils.client.Binary;

import java.util.*;

import net.dappls.legacy_utils.client.Util.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BinarySolver {

    private static final Map<List<Integer>, List<Boolean>> tableOn = new HashMap<>();
    private static final Map<List<Integer>, List<Boolean>> tableOff = new HashMap<>();
    public static boolean LAMP_STATE_IS_ON;


    public static boolean getLampState(World world, BlockPos lampPos) {
        return (world.getBlockState(lampPos).isOf(net.minecraft.block.Blocks.REDSTONE_LAMP) &&
                world.getBlockState(lampPos).get(net.minecraft.block.RedstoneLampBlock.LIT));
    }


    public static List<String> solveBinaryPuzzle(List<Integer> DesiredState) {
        LAMP_STATE_IS_ON = getLampState(Objects.requireNonNull(MinecraftClient.getInstance().world), new BlockPos(10049,61,49998));

        tableOn.put(Arrays.asList(0,0), Arrays.asList(true, true, true));
        tableOn.put(Arrays.asList(0,1), Arrays.asList(false, false, false));
        tableOn.put(Arrays.asList(1,0), Arrays.asList(false, true, true));
        tableOn.put(Arrays.asList(1,1), Arrays.asList(true, false, false));

        tableOff.put(Arrays.asList(0,0), Arrays.asList(false, false, false));
        tableOff.put(Arrays.asList(0,1), Arrays.asList(true, true, false));
        tableOff.put(Arrays.asList(1,0), Arrays.asList(false, true, true));
        tableOff.put(Arrays.asList(1,1), Arrays.asList(true, false, true));

        for(int i = 0;i<4;i++) {
            int LampSectionID = 2*(3-i);
            List<Integer> LampSection = new ArrayList<>(Arrays.asList(DesiredState.get(LampSectionID), DesiredState.get(LampSectionID+1)));

            if(LAMP_STATE_IS_ON) {
                ChatUtils.sendClientMessage(formatLampStates(tableOn.get(LampSection)));
                ChatUtils.sendClientMessage("Press Button");

            }

            else {
                ChatUtils.sendClientMessage(formatLampStates(tableOff.get(LampSection)));
                ChatUtils.sendClientMessage("Press Button");
            }

            LAMP_STATE_IS_ON = (LampSection.getFirst() ==1);
        }



        return null;
    }

    private static String formatLampStates(List<Boolean> states) {
        if (states == null) return "null";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < states.size(); i++) {
            sb.append(states.get(i) ? "on" : "off");
            if (i < states.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }


}
