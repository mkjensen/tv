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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DrDecryptionTest {

  @Test
  void decrypt() {

    assertEquals(
        "https://drtesty-lh.akamaihd.net/i/drtestytext_0@668184/master.m3u8?subtestbyteje",
        DrDecryption.decrypt("01000000c09b2fd5b4ffcb615af5555f571928bdad01cb1b0157af52c08a3673fae605f37c0e0a486f68f322d11e2df22fecb3d99fd30fb404021ba932076a2031401d527679ce066da7f71020a3f756cd8f123cdbf94fc3201ab1c02fc2b2176c2ebe38fa45f34f7f7bf8435657e2d6771073db21")
    );

    assertEquals(
        "http://drevent-lh.akamaihd.net/z/DRweb12_1@427365",
        DrDecryption.decrypt("010000008076d98499ee1c6ba33016e5024bcec3b045f055679593a4e58638d6284ca893e1daad391ae068127801ec2a972b6a0cf1ba70f9c194f4bdfb979e4f69eefeb826101fa3a9848e9a0c398649bb767a42ff")
    );

    assertEquals(
        "manifest.f4m?b=100-5000",
        DrDecryption.decrypt("0100000040f3268cc98c223904c365e89d9a69c7cc171942d8d1996b3e0ecb611364252b9c7e9f8798b174dd4b2ce5136134f0b83f")
    );

    assertEquals(
        "manifest.f4m",
        DrDecryption.decrypt("0100000020197cbf452f5c755bf9e4c98c46172579d5e357f855630a8ecfe6d5cd01049fc2")
    );
  }
}
