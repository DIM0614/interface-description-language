import com.squareup.javapoet.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;

public class Generator {

    public void generateInterface(JSONObject file, Path path) throws IOException {
        String interfaceName = (String )file.get("name");
        String interfaceDescription = (String )file.get("description");

        JSONArray operations = (JSONArray) file.get("operations");
        Iterable<MethodSpec> methods = new ArrayList<MethodSpec>();
        for(int i = 0; i < operations.size(); ++i){
            JSONObject method = (JSONObject) operations.get(i);
            String methodName = (String) method.get("name");
            String methodDescription = (String) method.get("description");
            String methodReturn = (String) method.get("return");

            JSONArray params = (JSONArray) method.get("params");
            Iterable<ParameterSpec> parameters = new ArrayList<ParameterSpec>();
            for (int j = 0; j < params.size(); j++) {
                JSONObject param = (JSONObject) params.get(j);
                String paramName = (String) param.get("name");
                String paramType = (String) param.get("type");
                String paramDescription = (String) param.get("description");

                methodDescription += "\n@param " + paramName + " " + paramDescription;

                ParameterSpec ps = ParameterSpec.builder(getType(paramType), paramName).build();
                ((ArrayList<ParameterSpec>) parameters).add(ps);
            }

            methodDescription += "\n@return " + methodReturn;

            MethodSpec ms = MethodSpec.methodBuilder(methodName)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(getType(methodReturn))
                    .addParameters(parameters)
                    .addJavadoc(methodDescription)
                    .addException(ClassName.get("", "RemoteError")) // change the real package name of class RemoteError
                    .build();
            ((ArrayList<MethodSpec>) methods).add(ms);
        }

        TypeSpec interfaceType = TypeSpec.interfaceBuilder(interfaceName)
                .addModifiers(Modifier.PUBLIC)
                .addMethods(methods)
                .addJavadoc(interfaceDescription)
                .build();

        JavaFile javaFile = JavaFile.builder("", interfaceType)
                .build();

        javaFile.writeTo(path);
    }

    private Type getType(String type){
        if(type.equals("int")){
            return int.class;
        }else if(type.equals("float")){
            return float.class;
        }else if(type.equals("boolean")){
            return boolean.class;
        }else if(type.equals("string")){
            return String.class;
        }else if(type.equals("char")){
            return char.class;
        }

        return String.class;
    }

}
