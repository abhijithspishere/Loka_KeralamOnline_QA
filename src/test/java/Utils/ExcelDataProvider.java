package Utils;

import org.testng.annotations.DataProvider;
import java.io.IOException;

public class ExcelDataProvider {

    @DataProvider(name = "registrationData")
    public static Object[][] getRegistrationData() throws IOException {
        return ExcelUtils.getTestData(
                "src/test/resources/testdata/RegistrationData.xlsx",
                "Sheet1"
        );
    }
}