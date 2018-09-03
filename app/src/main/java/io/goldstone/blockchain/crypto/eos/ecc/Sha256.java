package io.goldstone.blockchain.crypto.eos.ecc;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import org.spongycastle.util.encoders.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Sha256 {

  private static final int HASH_LENGTH = 32;

  final private byte[] mHashBytes;

  private Sha256(byte[] bytes) {
    Preconditions.checkArgument(bytes.length == HASH_LENGTH);
    this.mHashBytes = bytes;
  }

  private static MessageDigest getSha256Digest() {
    try {
      return MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e); //cannot happen
    }
  }

  public static Sha256 from(byte[] data) {
    MessageDigest digest;
    digest = getSha256Digest();
    digest.update(data, 0, data.length);
    return new Sha256(digest.digest());
  }

  public static Sha256 from(byte[] data, int offset, int length) {
    MessageDigest digest;
    digest = getSha256Digest();
    digest.update(data, offset, length);
    return new Sha256(digest.digest());
  }

  public static Sha256 from(byte[] data1, byte[] data2) {
    MessageDigest digest;
    digest = getSha256Digest();
    digest.update(data1, 0, data1.length);
    digest.update(data2, 0, data2.length);
    return new Sha256(digest.digest());

  }

  public static Sha256 doubleHash(byte[] data) {
    MessageDigest digest;
    digest = getSha256Digest();
    digest.update(data, 0, 33);
    return new Sha256(digest.digest(digest.digest()));
  }


  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof Sha256))
      return false;
    return Arrays.equals(mHashBytes, ((Sha256) other).mHashBytes);
  }


  @NonNull
  @Override
  public String toString() {
    return Hex.toHexString(mHashBytes);
  }

  public byte[] getBytes() {
    return mHashBytes;
  }

  boolean equalsFromOffset(byte[] toCompareData, int offsetInCompareData) {
    if ((null == toCompareData) || (offsetInCompareData < 0)
      || (mHashBytes.length <= 4)
      || (toCompareData.length <= offsetInCompareData)) {
      return false;
    }

    for (int i = 0; i < 4; i++) {

      if (mHashBytes[i] != toCompareData[offsetInCompareData + i]) {
        return false;
      }
    }

    return true;
  }

  public int length() {
    return HASH_LENGTH;
  }
}

