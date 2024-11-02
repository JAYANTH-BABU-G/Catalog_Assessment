import java.io.FileReader;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.math.BigInteger;

public class ShamirSecretSharing {

    public static void main(String[] args) throws Exception {
        // Read both JSON files
        JsonObject testCase1 = readJson("input1.json");
        JsonObject testCase2 = readJson("input2.json");

        // Process each test case
        System.out.println("Constant term for test case 1: " + findConstantTerm(testCase1));
        System.out.println("Constant term for test case 2: " + findConstantTerm(testCase2));
    }

    private static JsonObject readJson(String filePath) throws Exception {
        FileReader reader = new FileReader(filePath);
        return JsonParser.parseReader(reader).getAsJsonObject();
    }

    private static BigInteger findConstantTerm(JsonObject testCase) {
        Gson gson = new Gson();
        int k = testCase.getAsJsonObject("keys").get("k").getAsInt();

        BigInteger[] xValues = new BigInteger[k];
        BigInteger[] yValues = new BigInteger[k];
        int count = 0;

        // Collect (x, y) pairs from the test case JSON object
        for (Map.Entry<String, ?> entry : testCase.entrySet()) {
            if (!entry.getKey().equals("keys") && count < k) {
                int x = Integer.parseInt(entry.getKey());
                JsonObject point = testCase.getAsJsonObject(entry.getKey());
                int base = point.get("base").getAsInt();
                String value = point.get("value").getAsString();

                xValues[count] = BigInteger.valueOf(x);
                yValues[count] = new BigInteger(value, base);
                count++;
            }
        }

        return lagrangeInterpolation(xValues, yValues, BigInteger.ZERO);
    }

    private static BigInteger lagrangeInterpolation(BigInteger[] xValues, BigInteger[] yValues, BigInteger x) {
        BigInteger result = BigInteger.ZERO;
        int k = xValues.length;

        for (int i = 0; i < k; i++) {
            BigInteger term = yValues[i];
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    term = term.multiply(x.subtract(xValues[j]))
                               .divide(xValues[i].subtract(xValues[j]));
                }
            }
            result = result.add(term);
        }
        return result;
    }
}
