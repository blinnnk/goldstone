package io.goldstone.blinnnk.crypto.eos.ecc;

import java.math.BigInteger;

public class EcCurve {

  private EcFieldElement _a;
  private EcFieldElement _b;
  private BigInteger _q;
  private EcPoint _infinity;

  EcCurve(BigInteger q, BigInteger a, BigInteger b) {
    this._q = q;
    this._a = fromBigInteger(a);
    this._b = fromBigInteger(b);
    this._infinity = new EcPoint(this, null, null);
  }

  public EcFieldElement getA() {
    return _a;
  }

  public EcFieldElement getB() {
    return _b;
  }

  BigInteger getQ() {
    return _q;
  }

  public EcPoint getInfinity() {
    return _infinity;
  }

  public int getFieldSize() {
    return _q.bitLength();
  }

  EcFieldElement fromBigInteger(BigInteger x) {
    return new EcFieldElement(this._q, x);
  }

  public EcPoint decodePoint(byte[] encodedPoint) {
    EcPoint point;
    // Switch on encoding type
    switch (encodedPoint[0]) {
      case 0x00:
        point = getInfinity();
        break;
      case 0x02:
      case 0x03:
        int ytilde = encodedPoint[0] & 1;
        byte[] i = new byte[encodedPoint.length - 1];
        System.arraycopy(encodedPoint, 1, i, 0, i.length);
        EcFieldElement x = new EcFieldElement(this._q, new BigInteger(1, i));
        EcFieldElement alpha = x.multiply(x.square().add(_a)).add(_b);
        EcFieldElement beta = alpha.sqrt();
        if (beta == null) {
          throw new RuntimeException("Invalid compression");
        }
        int bit0 = (beta.toBigInteger().testBit(0) ? 1 : 0);
        if (bit0 == ytilde) {
          point = new EcPoint(this, x, beta, true);
        } else {
          point = new EcPoint(this, x, new EcFieldElement(this._q, _q.subtract(beta.toBigInteger())), true);
        }
        break;
      case 0x04:
      case 0x06:
      case 0x07:
        byte[] xEnc = new byte[(encodedPoint.length - 1) / 2];
        byte[] yEnc = new byte[(encodedPoint.length - 1) / 2];
        System.arraycopy(encodedPoint, 1, xEnc, 0, xEnc.length);
        System.arraycopy(encodedPoint, xEnc.length + 1, yEnc, 0, yEnc.length);
        point = new EcPoint(this, new EcFieldElement(this._q, new BigInteger(1, xEnc)), new EcFieldElement(this._q,
          new BigInteger(1, yEnc)));
        break;
      default:
        throw new RuntimeException("Invalid encoding 0x" + Integer.toString(encodedPoint[0], 16));
    }
    return point;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof EcCurve)) {
      return false;
    }
    EcCurve other = (EcCurve) obj;
    return this._q.equals(other._q) && _a.equals(other._a) && _b.equals(other._b);
  }

  @Override
  public int hashCode() {
    return _a.hashCode() ^ _b.hashCode() ^ _q.hashCode();
  }

}
