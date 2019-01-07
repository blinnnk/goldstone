package io.goldstone.blinnnk.crypto.bitcoincash;

/*
 * @author KaySaith
 * @date 2018/8/20 7:55 PM
 */

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2018 Igor Kiulian
 * <p>
 * Distributed under the MIT software license, see the accompanying file LICENSE
 * or http://www.opensource.org/licenses/mit-license.php.
 */
class Base32 {

  private static final String CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";
  private static final char[] CHARS = CHARSET.toCharArray();

  private static Map<Character, Integer> charPositionMap;

  static {
    charPositionMap = new HashMap<>();
    for (int i = 0; i < CHARS.length; i++) {
      charPositionMap.put(CHARS[i], i);
    }

  }

  static int[] decode(String base32String) {
    int[] bytes = new int[base32String.length()];

    char[] charArray = base32String.toCharArray();
    for (int i = 0; i < charArray.length; i++) {
      Integer position = charPositionMap.get(charArray[i]);
      if (position == null) {
        throw new RuntimeException("There seems to be an invalid char: " + charArray[i]);
      }
      bytes[i] = (byte) ((int) position);
    }

    return bytes;
  }


}
