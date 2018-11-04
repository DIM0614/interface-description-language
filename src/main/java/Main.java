import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {
    public static void main(String[] args) {
        Generator generator = new Generator();

        JSONParser parser = new JSONParser();
        File directory = new File("./");
        try {
            String jsonURL = directory.getCanonicalPath() + "/src/main/java/example.json";
            String interfaceURL = directory.getCanonicalPath() + "/src/main/java/";
            Object obj = parser.parse(new FileReader(jsonURL));
            JSONObject jsonObject = (JSONObject) obj;
            Path path = Paths.get(interfaceURL);
            generator.generateInterface(jsonObject, path);
            generator.generateClass(jsonObject, path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
