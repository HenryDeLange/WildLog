package wildlog.encryption;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;


// Based on https://stackoverflow.com/questions/1132567/encrypt-password-in-configuration-files
public class TokenEncryptor {
//    private static final String SALT = "3XbswwUah3frUDwO+WeA+EwrsY9yc3MfxQb5y7aaanKwLd3KY593QdLuHjgV4fmZ5sz1DjhPS0HKi8YcPopbvJSOkGAUYSnI/5NHmFXM4Kprw07lltCnBlxRTI1MyYdXspVg/BUcG5IMEYU6jOhKB74uxsig8PIcu3UNOgxxXwHUwAkJxhEugaIPE74iJh1IHdbBnL9RLzHlHldfES4SRqGgdxEX+jhSLp4AOsBIMLS0wKV9bgNPiqPcCm30pjG/h5+Slf+5t2ji3sIaf0bDvuw9209rD/McFbWNY9vKL4RzPFGIc3ZeeNSv/TY0OUo7m0tQut3IE+9qEtGZoaGBE7egMTEpqRAOpGoQjosTQ+OVw65JuEQLGnjiVbeljlpF9MtLKa+d4T84n5dF2iTaG71lHxbSBhGSHLxCXp+dhnf+lSJXyeajr6FQvJ+Fwd+++WgRDzUvBmKSNkGZMklN+msJRzvhIfGEzYg99hWx2iZoeCKY5ovRfkmD+IgQeUSw4PCNeBLHi/1Bfk1/1wGNJoOxS+PueESYJCmFMC7MYn9CAFA/a48XeGyMCjAvh8Nc70wSiHfqs4z18DBRlLJdx7FQ0jiXs7s+mhi5Bk4yyLWSvPg9ZaqYdCq7gCIUZUKEoNTf+8BJrd9R3eQ9yHqhdRGWfrdSBCfAXmQgfGRE8k0=";
    private static final String KEY = "xGB2+kG/Uyl0bEvnycor1A==";

    
    public static String encrypt(String inText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(KEY), "AES");
            Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            pbeCipher.init(Cipher.ENCRYPT_MODE, keySpec);
            AlgorithmParameters parameters = pbeCipher.getParameters();
            IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
            byte[] cryptoText = pbeCipher.doFinal(inText.getBytes("UTF-8"));
            byte[] iv = ivParameterSpec.getIV();
            return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(cryptoText);
        }
        catch (GeneralSecurityException | UnsupportedEncodingException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        return null;
    }

    public static String decrypt(String inText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(KEY), "AES");
            String iv = inText.split(":")[0];
            String cryptoText = inText.split(":")[1];
            Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            pbeCipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(Base64.getDecoder().decode(iv)));
            return new String(pbeCipher.doFinal(Base64.getDecoder().decode(cryptoText)), "UTF-8");
        }
        catch (GeneralSecurityException | IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        return null;
    }
    
    
//    public static void main(String[] args) throws Exception {
////        // Generate the Salt
////        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
////        byte[] salt = new byte[512];
////        for (long t = 0; t < random.nextInt(100) + 100; t++) {
////            random.nextBytes(salt);
////        }
////        System.out.println("BASE64 SALT = " + Base64.getEncoder().encodeToString(salt));
////        // Generate the raw Key
////        byte[] keyPassword = new byte[512];
////        random.nextBytes(keyPassword);
////        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
////        PBEKeySpec keySpec = new PBEKeySpec(new String(keyPassword).toCharArray(), Base64.getDecoder().decode(SALT), 50000, 128);
////        SecretKey keyTmp = keyFactory.generateSecret(keySpec);
////        System.out.println("BASE64 KEY = " + Base64.getEncoder().encodeToString(keyTmp.getEncoded()));
//        // Test the Key
//        String originalText = "test Secret 123Baaa321";
//        System.out.println("Original Text: " + originalText);
//        String encryptedText = encrypt(originalText);
//        System.out.println("Encrypted Text: " + encryptedText);
//        String decryptedText = decrypt(encryptedText);
//        System.out.println("Decrypted Text: " + decryptedText);
//    }

}
