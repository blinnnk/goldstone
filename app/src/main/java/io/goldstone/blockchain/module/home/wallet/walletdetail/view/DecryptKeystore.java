package io.goldstone.blockchain.module.home.wallet.walletdetail.view;

import org.web3j.crypto.WalletFile;

import io.goldstone.blockchain.crypto.KeystoreModel;

/**
 * @author KaySaith
 * @date 05/04/2018 1:57 PM
 */
public class DecryptKeystore {

    public static WalletFile GenerateFile(KeystoreModel model) {
        WalletFile walletFile = new WalletFile();
        walletFile.setAddress(model.getAddress());

        WalletFile.Crypto crypto = new WalletFile.Crypto();
        crypto.setCipher(model.getCipher());
        crypto.setCiphertext(model.getCiphertext());
        walletFile.setCrypto(crypto);

        WalletFile.CipherParams cipherParams = new WalletFile.CipherParams();
        cipherParams.setIv(model.getIv());
        crypto.setCipherparams(cipherParams);

        crypto.setKdf(model.getKdf());
        WalletFile.ScryptKdfParams kdfParams = new WalletFile.ScryptKdfParams();
        kdfParams.setDklen(model.getDklen());
        kdfParams.setN(model.getN());
        kdfParams.setP(model.getP());
        kdfParams.setR(model.getR());
        kdfParams.setSalt(model.getSalt());
        crypto.setKdfparams(kdfParams);

        crypto.setMac(model.getMac());
        walletFile.setCrypto(crypto);
        walletFile.setId(model.getId());
        walletFile.setVersion(model.getVersion());

        return walletFile;
    }

}
