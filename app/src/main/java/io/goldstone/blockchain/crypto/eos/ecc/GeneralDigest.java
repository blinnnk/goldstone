package io.goldstone.blockchain.crypto.eos.ecc;

public abstract class GeneralDigest {
  private byte[] xBuf;
  private int xBufOff;

  private long byteCount;

  /**
   * Standard constructor
   */
  GeneralDigest() {
    xBuf = new byte[4];
    xBufOff = 0;
  }

  public void update(byte in) {
    xBuf[xBufOff++] = in;

    if (xBufOff == xBuf.length) {
      processWord(xBuf, 0);
      xBufOff = 0;
    }

    byteCount++;
  }

  public void update(byte[] in, int inOff, int len) {
    //
    // fill the current word
    //
    while ((xBufOff != 0) && (len > 0)) {
      update(in[inOff]);

      inOff++;
      len--;
    }

    //
    // process whole words.
    //
    while (len > xBuf.length) {
      processWord(in, inOff);

      inOff += xBuf.length;
      len -= xBuf.length;
      byteCount += xBuf.length;
    }

    //
    // load in the remainder.
    //
    while (len > 0) {
      update(in[inOff]);

      inOff++;
      len--;
    }
  }

  void finish() {
    long bitLength = (byteCount << 3);

    //
    // add the pad bytes.
    //
    update((byte) 128);

    while (xBufOff != 0) {
      update((byte) 0);
    }

    processLength(bitLength);

    processBlock();
  }

  public void reset() {
    byteCount = 0;

    xBufOff = 0;
    for (int i = 0; i < xBuf.length; i++) {
      xBuf[i] = 0;
    }
  }

  protected abstract void processWord(byte[] in, int inOff);

  protected abstract void processLength(long bitLength);

  protected abstract void processBlock();
}
