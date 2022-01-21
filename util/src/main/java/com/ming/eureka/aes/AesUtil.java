package com.ming.eureka.aes;

/**
 * 对称加密（默认16位密钥，16位密钥向量）
 */
public class AesUtil {

    private static final byte[] PRIVATE_KEY = {-23, 29, -45, 63, 84, -5, -39, 105,
            45, 19, 99, -29, 43, 37, -57, 4};

    private static final String key = "a1*&12h5r8s9h%$x";

    /**
     * 加密
     */
    public static String encryption(String content) {
        byte[] result = Cryptos.aesEncrypt(content.getBytes(), key.getBytes(), PRIVATE_KEY);
        return Encodes.encodeBase64(result);
    }

    /**
     * 解密
     **/
    public static String decryption(String content) {
        return Cryptos.aesDecrypt(Encodes.decodeBase64(content), key.getBytes(), PRIVATE_KEY);
    }

    public static void main(String args[]) {
        String fsafsa = encryption("fsafsa");
        System.out.println(fsafsa);
        System.out.println(decryption(fsafsa));
    }

}
