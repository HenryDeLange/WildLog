package wildlog.encryption;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;

public class PasswordEncryptor {

    private static final String SALT = "RFD7tkasgymXE/JPUg59mh3/B3e+9A06NMSmAyzAbFI1cgpH3jV56hkKWMp65W97fGeS0Ziw0FiDXbXAJ3JQkJxT5QikJxnmIhfN3IC8p017yg8RlkL/4MA6UVCawnwtbmWwy3Mu67cN4lnArE/+WTDwdt+x52yrCDLdayxXSkEhqigdDXkDjadAKEqjIqCaXJJdBbY5EEYtEi+n3k/MTZ8bMoBgdgL6bErZYwhA3q6AfbqOL8TXq0IJi+7WdqO9wyUUEL+ZlHELmml+zDgTLaouag/LFF/E8vuFpG5pvxABtmprNk0pvq8Xi7PwTqV3m8Xo3qZd8XmMVikuKEMJTkiHZcvQeIcSdYuPdpAM5F1Z3xTFnrjEbcarDGC8TP8frUBNi76M/U1tOGeJdKhdXIYd2DJoMWyDi89bio27mbFX/5emGCG+3biDaVN0OX+1gLvOfcz3+ApSeAiQE1Rf1yd+hJ2KmbUz2GrM8DKhTPUvKJv8CMWZsAAFh7cCV26ZMA0d5Lse655zi5s0riB9WLOOEWeZXe6h4cJMsWvtRKeLeCA6YgeGJgIJPryyDQFOzytnToKKGlyo9v5NQZ9ykQAH4Frd2ZzMWXcqFJuXWte0p7+1FSeUKf+HntiScvfuXbeERmjXVMIeeEXu3mkEs+PCeEAEe0qpL0Bkkg1vM6s=";

    public static String generateHashedPassword(String inPassword) {
        try {
            byte[] salt = Base64.getDecoder().decode(SALT);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec keySpec = new PBEKeySpec(inPassword.toCharArray(), salt, 50000, 512);
            byte[] hashedPassword = secretKeyFactory.generateSecret(keySpec).getEncoded();
            return Base64.getEncoder().encodeToString(hashedPassword);
        }
        catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        return null;
    }

}
