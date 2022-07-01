package fr.hyriode.rtf.utils;

/**
 * Project: Hyriode-Development
 * Created by Akkashi
 * on 17/06/2022 at 20:53
 */
public class InventoryUtil {

    public static int[] getAvailableSlots() {
        return new int[] {
                11, 12, 13, 14, 15, 16,
                20, 21, 22, 23, 24, 25,
                29, 30 ,31, 32, 33, 34,
                38, 39, 40, 41, 42, 43
        };
    }

    public static int[] getSeparatorSlots() {
        return new int[] {
                1, 2, 3, 4, 5, 6, 7, 8,
                10, 17,
                19, 26,
                28, 35,
                37, 44,
                46, 47,
                48, 49, 50, 51, 52, 53
        };
    }
}
