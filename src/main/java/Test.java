import org.javaswift.joss.client.factory.AccountConfig;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.client.factory.AuthenticationMethod;
import org.javaswift.joss.model.Account;

public class Test {

    public static void main(String[] args) {
        AccountConfig config = new AccountConfig();
//        config.setTenantName("service");
        config.setUsername("swift");
        config.setPassword("password");
        config.setAuthUrl("http://115.68.47.185:5000/v2.0/tokens");
        config.setAuthenticationMethod(AuthenticationMethod.KEYSTONE);
//        config.setDomain("default");
        Account account = new AccountFactory(config).createAccount();


    }
}
