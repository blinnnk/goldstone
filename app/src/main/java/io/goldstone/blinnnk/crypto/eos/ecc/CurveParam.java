package io.goldstone.blinnnk.crypto.eos.ecc;

import com.subgraph.orchid.encoders.Hex;

import java.math.BigInteger;

public class CurveParam {
  public static final int SECP256_K1 = 0;
  public static final int SECP256_R1 = 1;

  private final int curveParamType;
  private final EcCurve curve;
  private final EcPoint G;
  private final BigInteger n;

  private final BigInteger HALF_CURVE_ORDER;

  public CurveParam(int curveParamType, String pInHex, String aInHex, String bInHex, String GxInHex, String GyInHex, String nInHex) {
    this.curveParamType = curveParamType;
    BigInteger p = new BigInteger(pInHex, 16); //p
    BigInteger b = new BigInteger(bInHex, 16);
    BigInteger a = new BigInteger(aInHex, 16);
    curve = new EcCurve(p, a, b);

    G = curve.decodePoint(Hex.decode("04" + GxInHex + GyInHex));
    n = new BigInteger(nInHex, 16);

    HALF_CURVE_ORDER = n.shiftRight(1);
  }

  public boolean isType(int paramType) {
    return curveParamType == paramType;
  }

  public EcPoint G() {
    return this.G;
  }

  public BigInteger n() {
    return this.n;
  }

  BigInteger halfCurveOrder() {
    return HALF_CURVE_ORDER;
  }

  public EcCurve getCurve() {
    return curve;
  }
}
