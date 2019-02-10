/*
 * Copyright 2019 Martin Kamp Jensen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mkjensen.tv.util;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import timber.log.Timber;

/**
 * Decrypts parts of encrypted stream URLs. Kudos to the references for finding out the details needed to perform the
 * decryption.
 *
 * @see <a href="https://github.com/clhols/drchannels/commit/df7417eb1b451a7168df20a0457a5e4b9cbd77fd">Fix no stream error</a>
 * @see <a href="https://github.com/rg3/youtube-dl/commit/a2d821d7112fb1423f99ddf309a843c80cc3be2d">[drtv] Improve extraction</a>
 * @see <a href="https://github.com/xbmc-danish-addons/plugin.video.drnu/pull/21/commits/7a30dc43a2ddc6d87371aa9b591295f6c6f60211">Fixing encrypted streams+moving addon ownership</a>
 */
public class DrDecryption {

  private static final Cipher CIPHER = createCipher();

  private static final MessageDigest MESSAGE_DIGEST = createMessageDigest();

  public static String decrypt(String input) {

    if (input == null) {
      return null;
    }

    try {
      return doDecrypt(input);
    } catch (GeneralSecurityException | RuntimeException ex) {
      Timber.e(ex, "Unable to decrypt: %s", input);
      return null;
    }
  }

  private static String doDecrypt(String input) throws GeneralSecurityException {

    String initializationVectorBeginIndexHex = input.substring(2, 10);
    int initializationVectorBeginIndex = Integer.parseInt(initializationVectorBeginIndexHex, 16);
    String initializationVectorHex = input.substring(10 + initializationVectorBeginIndex);
    String encryptedHex = input.substring(10, initializationVectorBeginIndex + 10);

    byte[] key = createHash(initializationVectorHex + ":sRBzYNXBzkKgnjj8pGtkACch");
    byte[] initializationVector = convertHexStringToByteArray(initializationVectorHex);
    byte[] encrypted = convertHexStringToByteArray(encryptedHex);

    byte[] decrypted = decrypt(key, initializationVector, encrypted);

    return new String(decrypted);
  }

  private static Cipher createCipher() {

    String transformation = "AES/CBC/PKCS5PADDING";

    try {
      return Cipher.getInstance(transformation);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
      Timber.e(ex, "Unsupported transformation: %s", transformation);
      return null;
    }
  }

  private static MessageDigest createMessageDigest() {

    String algorithm = "SHA-256";

    try {
      return MessageDigest.getInstance(algorithm);
    } catch (NoSuchAlgorithmException ex) {
      Timber.e(ex, "Unsupported algorithm: %s", algorithm);
      return null;
    }
  }

  private static byte[] createHash(String input) {

    if (MESSAGE_DIGEST == null) {
      return null;
    }

    return MESSAGE_DIGEST.digest(input.getBytes());
  }

  private static byte[] convertHexStringToByteArray(String input) {

    byte[] bytes = new BigInteger(input, 16).toByteArray();

    if (bytes[0] == 0) {
      byte[] bytesWithoutSignBit = new byte[bytes.length - 1];
      System.arraycopy(bytes, 1, bytesWithoutSignBit, 0, bytesWithoutSignBit.length);
      return bytesWithoutSignBit;
    }

    return bytes;
  }

  private static byte[] decrypt(
      byte[] key,
      byte[] initializationVector,
      byte[] encrypted) throws GeneralSecurityException {

    if (CIPHER == null) {
      return null;
    }

    CIPHER.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(initializationVector));

    return CIPHER.doFinal(encrypted);
  }
}
