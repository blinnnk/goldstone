package io.goldstone.blockchain.crypto.bitcoincash;

/*
 * @author KaySaith
 * @date 2018/8/15 10:58 AM
 */

import android.support.annotation.NonNull;

import java.util.Arrays;

public class BitArray {

  private final byte[] data;

  BitArray(byte[] data) {
    this.data = Arrays.copyOf(data, data.length);
  }

  BitArray(byte[] data, int off, int len) {
    this.data = Arrays.copyOfRange(data, off, off + len);
  }

  byte[] toArray() {
    return Arrays.copyOf(this.data, data.length);
  }

  int bitLength() {
    return data.length * 8;
  }

  public boolean get(int position) {
    if (position >= data.length * 8 || position < 0) {
      throw new ArrayIndexOutOfBoundsException(position);
    }
    int idx = position >> 3;// position / 8
    position = idx << 3 ^ position;// position % 8
    int dt = data[idx] << position & 0xFF;
    dt = dt >>> (8 - 1);
    return dt != 0;
  }

  public BitArray set(int position) {
    if (position >= data.length * 8 || position < 0) {
      throw new ArrayIndexOutOfBoundsException(position);
    }
    int idx = position >> 3;// position / 8
    position = idx << 3 ^ position;// position % 8
    data[idx] |= 1 << (8 - 1 - position);
    return this;
  }

  @NonNull
  @Override
  public String toString() {
    return this.toString(" ");
  }

  public String toString(String spide) {
    StringBuilder buf = new StringBuilder();
    for (byte b : this.data) {
      String bn = "00000000" + Integer.toBinaryString(b);
      buf.append(bn.substring(bn.length() - 8)).append(spide);
    }
    return buf.toString();
  }
}