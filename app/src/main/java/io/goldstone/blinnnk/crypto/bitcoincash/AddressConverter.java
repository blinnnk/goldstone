package io.goldstone.blinnnk.crypto.bitcoincash;

/*
 * @author KaySaith
 * @date 2018/8/20 7:54 PM
 */

import java.util.ArrayList;
import java.util.Arrays;

import io.goldstone.blinnnk.crypto.litecoin.ChainPrefix;
public class AddressConverter {

  private static final String SEPARATOR = ":";

  public static String toLegacyAddress(String cashAddress, ChainPrefix prefix) {
    if (cashAddress.contains(SEPARATOR))
      cashAddress = cashAddress.split(SEPARATOR)[1];

    int[] decoded = Base32.decode(cashAddress);
    int[] converted = convertBits(decoded);
    int[] payload = Arrays.copyOfRange(converted, 1, converted.length - 6);
    byte[] payloadBytes = new byte[payload.length];
    int i = 0;
    while (i < payloadBytes.length) {
      payloadBytes[i] = (byte) payload[i++];
    }

    return Base58.encodeToStringChecked(payloadBytes, prefix.getPublickeyInt());
  }

  private static int[] convertBits(int[] bytes8Bits) {
    int mask = ((1 << 8) - 1);
    int accumulator = 0;
    int bits = 0;
    int max_acc = (1 << (5 + 8 - 1)) - 1;
    ArrayList<Integer> list = new ArrayList<>();
    for (int value : bytes8Bits) {
      accumulator = ((accumulator << 5) | value) & max_acc;
      bits += 5;
      while (bits >= 8) {
        bits -= 8;
        list.add((accumulator >> bits) & mask);
      }
    }

    if (bits > 0) {
      list.add(((accumulator << (8 - bits)) & mask));
    }

    int[] result = new int[list.size()];
    for (int i = 0; i < list.size(); i++) {
      result[i] = list.get(i);
    }

    return result;
  }
}
