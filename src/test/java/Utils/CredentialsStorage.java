package Utils;

public class CredentialsStorage {
    private static String registeredEmail;
    private static String registeredPassword;

    public static void storeCredentials(String email, String password) {
        registeredEmail = email;
        registeredPassword = password;
        System.out.println("Credentials stored - Email: " + email + ", Password: " + password);
    }

    public static String getRegisteredEmail() {
        return registeredEmail;
    }

    public static String getRegisteredPassword() {
        return registeredPassword;
    }

    public static void clearCredentials() {
        registeredEmail = null;
        registeredPassword = null;
    }
}