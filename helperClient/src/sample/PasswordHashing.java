package sample;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHashing {
    private String password;

    public static void setSalt(String salt) {
        PasswordHashing.salt = salt;
    }

    private static String salt = "Charon";

    public PasswordHashing(String password){
        this.password = password;
    }

    public String MD5plusSalt(){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(getSalt());
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return generatedPassword;
    }

    private static byte[] getSalt()
    {
        return salt.getBytes();
    }
}
