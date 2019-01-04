package io.goldstone.blockchain.crypto.eos.ecc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class Hmac {

  private static final String SHA256 = "SHA-256";
  private static final int SHA256_BLOCK_SIZE = 64;

  static byte[] hmacSha256(byte[] key, byte[] message) {
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance(SHA256);
    } catch (NoSuchAlgorithmException e) {
      // Only happens if the platform does not support SHA-256
      throw new RuntimeException(e);
    }
    return hmac(digest, key, message);
  }

  private static byte[] hmac(MessageDigest digest, byte[] key, byte[] message) {

    // Ensure sufficient key length
    if (key.length > Hmac.SHA256_BLOCK_SIZE) {
      key = hash(digest, key);
    }
    if (key.length < Hmac.SHA256_BLOCK_SIZE) {
      // Zero pad
      byte[] temp = new byte[Hmac.SHA256_BLOCK_SIZE];
      System.arraycopy(key, 0, temp, 0, key.length);
      key = temp;
    }

    // Prepare o key pad
    byte[] o_key_pad = new byte[Hmac.SHA256_BLOCK_SIZE];
    for (int i = 0; i < Hmac.SHA256_BLOCK_SIZE; i++) {
      o_key_pad[i] = (byte) (0x5c ^ key[i]);
    }

    // Prepare i key pad
    byte[] i_key_pad = new byte[Hmac.SHA256_BLOCK_SIZE];
    for (int i = 0; i < Hmac.SHA256_BLOCK_SIZE; i++) {
      i_key_pad[i] = (byte) (0x36 ^ key[i]);
    }

    return hash(digest, o_key_pad, hash(digest, i_key_pad, message));
  }

  private static byte[] hash(MessageDigest digest, byte[] data) {
    digest.reset();
    digest.update(data, 0, data.length);
    return digest.digest();
  }

  private static byte[] hash(MessageDigest digest, byte[] data1, byte[] data2) {
    digest.reset();
    digest.update(data1, 0, data1.length);
    digest.update(data2, 0, data2.length);
    return digest.digest();
  }
}
