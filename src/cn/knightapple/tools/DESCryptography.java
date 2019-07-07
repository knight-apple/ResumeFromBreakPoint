package cn.knightapple.tools;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
/**
 * @author kngihtapple
 * @version 1.1
 */
public class DESCryptography {
    public static void main(String[] args) {

        String content = "aaaaaaaabbbbbbbbaaaaaaaa";
        String key = "knightapple";

        System.out.println("加密前 "+ content.getBytes());

        byte[] encrypted = DES_CBC_Encrypt(content.getBytes());
        System.out.println("加密后："+encrypted);

        byte[] decrypted=DES_CBC_Decrypt(encrypted);
        System.out.println("解密后："+decrypted);

    }

    public static byte[] DES_CBC_Encrypt(byte[] content) {

        try {
            DESKeySpec keySpec = new DESKeySpec(ConfigReader.getPropertie("secretKey").getBytes());
            String algorithm =  "DES";//指定使什么样的算法
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
            SecretKey key = keyFactory.generateSecret(keySpec);

            String transformation = "DES/CBC/PKCS5Padding"; //用什么样的转型方式
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(keySpec.getKey()));

            byte[] result = cipher.doFinal(content);

            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static byte[] DES_CBC_Decrypt(byte[] content) {

        try {
            DESKeySpec keySpec = new DESKeySpec(ConfigReader.getPropertie("secretKey").getBytes());


            String algorithm = "DES";
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm );
            SecretKey key = keyFactory.generateSecret(keySpec);

            String transformation = "DES/CBC/PKCS5Padding";
            Cipher cipher = Cipher.getInstance(transformation );
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ConfigReader.getPropertie("secretKey").getBytes()));
            byte[] result = cipher.doFinal(content);

            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
