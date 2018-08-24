package io.goldstone.blockchain.crypto.bitcoincash;

/*
 * @author KaySaith
 * @date 2018/8/15 11:04 AM
 */

/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.nio.charset.StandardCharsets;

/**
 * Base58 is a way to encode Bitcoin addresses (or arbitrary data) as
 * alphanumeric strings.
 * <p>
 * Note that this is not the same base58 as used by Flickr, which you may find
 * referenced around the Internet.
 * <p>
 * instead, which adds support for testing the prefix and suffix bytes commonly
 * found in addresses.
 * <p>
 * Satoshi explains: why base-58 instead of standard base-64 encoding?
 * <ul>
 * <li>Don't want 0OIl characters that look the same in some fonts and could be
 * used to create visually identical looking account numbers.</li>
 * <li>A string with non-alphanumeric characters is not as easily accepted as an
 * account number.</li>
 * <li>E-mail usually won't line-break if there's no punctuation to break
 * at.</li>
 * <li>Doubleclicking selects the whole number as one word if it's all
 * alphanumeric.</li>
 * </ul>
 * <p>
 * However, note that the encoding/decoding runs in O(n&sup2;) time, so it is
 * not useful for large data.
 * <p>
 * The basic idea of the encoding is to treat the data bytes as a large number
 * represented using base-256 digits, convert the number to be represented using
 * base-58 digits, preserve the exact number of leading zeros (which are
 * otherwise lost during the mathematical operations on the numbers), and
 * finally represent the resulting base-58 digits as alphanumeric ASCII
 * characters.
 */


class Base58 {

  private static int[] mIndexes;
  private static char[] mAlphabet;

  static {
    mAlphabet = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();

    mIndexes = new int[128];
    for (int i = 0; i < mIndexes.length; i++) {
      mIndexes[i] = -1;
    }
    for (int i = 0; i < mAlphabet.length; i++) {
      mIndexes[mAlphabet[i]] = i;
    }
  }

  static String encodeToStringChecked(byte[] input, int version) {
    return encodeToStringChecked(input, new byte[]{(byte) version});
  }

  private static String encodeToStringChecked(byte[] input, byte[] version) {
    return new String(encodeToBytesChecked(input, version), StandardCharsets.US_ASCII);
  }

  private static byte[] encodeToBytesChecked(byte[] input, byte[] version) {
    byte[] buffer = new byte[input.length + version.length];
    System.arraycopy(version, 0, buffer, 0, version.length);
    System.arraycopy(input, 0, buffer, version.length, input.length);
    byte[] checkSum = copyOfRange(HashUtils.doubleDigest(buffer), 0, 4);
    byte[] output = new byte[buffer.length + checkSum.length];
    System.arraycopy(buffer, 0, output, 0, buffer.length);
    System.arraycopy(checkSum, 0, output, buffer.length, checkSum.length);
    return encodeToBytes(output);
  }

  /**
   * Encodes the given bytes in base58. No checksum is appended.
   */
  private static byte[] encodeToBytes(byte[] input) {
    if (input.length == 0) {
      return new byte[0];
    }
    input = copyOfRange(input, 0, input.length);
    // Count leading zeroes.
    int zeroCount = 0;
    while (zeroCount < input.length && input[zeroCount] == 0) {
      ++zeroCount;
    }
    // The actual encoding.
    byte[] temp = new byte[input.length * 2];
    int j = temp.length;

    int startAt = zeroCount;
    while (startAt < input.length) {
      byte mod = divmod58(input, startAt);
      if (input[startAt] == 0) {
        ++startAt;
      }
      temp[--j] = (byte) mAlphabet[mod];
    }

    // Strip extra '1' if there are some after decoding.
    while (j < temp.length && temp[j] == mAlphabet[0]) {
      ++j;
    }
    // Add as many leading '1' as there were leading zeros.
    while (--zeroCount >= 0) {
      temp[--j] = (byte) mAlphabet[0];
    }

    byte[] output;
    output = copyOfRange(temp, j, temp.length);
    return output;
  }

  static byte[] decode(String input) throws EncodingFormatException {
    if (input.length() == 0) {
      return new byte[0];
    }
    byte[] input58 = new byte[input.length()];
    // Transform the String to a base58 byte sequence
    for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);

      int digit58 = -1;
      if (c < 128) {
        digit58 = mIndexes[c];
      }
      if (digit58 < 0) {
        throw new EncodingFormatException("Illegal character " + c + " at " + i);
      }

      input58[i] = (byte) digit58;
    }
    // Count leading zeroes
    int zeroCount = 0;
    while (zeroCount < input58.length && input58[zeroCount] == 0) {
      ++zeroCount;
    }
    // The encoding
    byte[] temp = new byte[input.length()];
    int j = temp.length;

    int startAt = zeroCount;
    while (startAt < input58.length) {
      byte mod = divmod256(input58, startAt);
      if (input58[startAt] == 0) {
        ++startAt;
      }

      temp[--j] = mod;
    }
    // Do no add extra leading zeroes, move j to first non null byte.
    while (j < temp.length && temp[j] == 0) {
      ++j;
    }

    return copyOfRange(temp, j - zeroCount, temp.length);
  }

  //
  // number -> number / 58, returns number % 58
  //
  private static byte divmod58(byte[] number, int startAt) {
    int remainder = 0;
    for (int i = startAt; i < number.length; i++) {
      int digit256 = (int) number[i] & 0xFF;
      int temp = remainder * 256 + digit256;

      number[i] = (byte) (temp / 58);

      remainder = temp % 58;
    }

    return (byte) remainder;
  }

  //
  // number -> number / 256, returns number % 256
  //
  private static byte divmod256(byte[] number58, int startAt) {
    int remainder = 0;
    for (int i = startAt; i < number58.length; i++) {
      int digit58 = (int) number58[i] & 0xFF;
      int temp = remainder * 58 + digit58;

      number58[i] = (byte) (temp / 256);

      remainder = temp % 256;
    }

    return (byte) remainder;
  }

  private static byte[] copyOfRange(byte[] source, int from, int to) {
    byte[] range = new byte[to - from];
    System.arraycopy(source, from, range, 0, range.length);

    return range;
  }
}