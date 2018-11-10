package implementation;

import com.squareup.javapoet.*;
import com.squareup.javapoet.MethodSpec.Builder;

import interfaces.Requestor;
import model.AbsoluteObjectReference;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import implementation.Invocation;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * This class generate interfaces, client proxies and invokers.
 *
 * @author Vinícius Campos
 * @author Artur Curinga
 */

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
                    .addException(ClassName.get("", "exception.RemoteError")) // change the real package name of class exception.RemoteError
                    .addException(IOException.class)
                    .addException(ClassNotFoundException.class)
                    .build();
            ((ArrayList<MethodSpec>) methods).add(ms);
        }

        TypeSpec interfaceType = TypeSpec.interfaceBuilder(interfaceName)
                .addModifiers(Modifier.PUBLIC)
                .addMethods(methods)
                .addJavadoc(interfaceDescription)
                .build();

        JavaFile javaFile = JavaFile.builder("generated", interfaceType)
                .build();

        javaFile.writeTo(path);
    }

    public void generateClass(JSONObject file, Path path) throws IOException {
        String className = (String )file.get("name");
        String classDescription = (String )file.get("description");

        JSONArray operations = (JSONArray) file.get("operations");
        Iterable<MethodSpec> methods = new ArrayList<>();
        for(int i = 0; i < operations.size(); ++i){
            JSONObject method = (JSONObject) operations.get(i);
            String methodName = (String) method.get("name");
            String methodDescription = (String) method.get("description");
            String methodReturn = (String) method.get("return");

            JSONArray params = (JSONArray) method.get("params");
            Iterable<ParameterSpec> parameters = new ArrayList<ParameterSpec>();
            String stringParams = "";
            for (int j = 0; j < params.size(); j++) {
                JSONObject param = (JSONObject) params.get(j);
                String paramName = (String) param.get("name");
                String paramType = (String) param.get("type");
                String paramDescription = (String) param.get("description");

                methodDescription += "\n@param " + paramName + " " + paramDescription;

                ParameterSpec ps = ParameterSpec.builder(getType(paramType), paramName).build();
                ((ArrayList<ParameterSpec>) parameters).add(ps);

                stringParams += paramName;
                if(j + 1 < params.size())
                    stringParams += ",";
            }

            methodDescription += "\n@return " + methodReturn;

            MethodSpec ms = MethodSpec.methodBuilder(methodName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(getType(methodReturn))
                    .addParameters(parameters)
                    .addStatement("return " + getCastType(methodReturn) + " r.request(aor,\"" + methodName + "\"," + stringParams + ")")
                    .addJavadoc(methodDescription)
                    .addException(ClassName.get("", "exception.RemoteError"))
                    .addException(IOException.class)
                    .addException(ClassNotFoundException.class)
                    .build();
            ((ArrayList<MethodSpec>) methods).add(ms);
        }

        // Creating fields of aor and requestor
        FieldSpec aor = FieldSpec.builder(AbsoluteObjectReference.class, "aor")
                .addModifiers(Modifier.PRIVATE)
                .build();
        FieldSpec r = FieldSpec.builder(Requestor.class, "r")
                .addModifiers(Modifier.PRIVATE)
                .build();

        // Defining constructor of class
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(AbsoluteObjectReference.class, "aor")
                .addStatement("this.aor = aor")
                .addStatement("this.r = new $T()", UnsyncRequestor.class)
                .build();

        TypeSpec classType = TypeSpec.classBuilder("Client" + className)
                .addModifiers(Modifier.PUBLIC)
                .addField(aor)
                .addField(r)
                .addMethod(constructor)
                .addMethods(methods)
                .addJavadoc(classDescription)
                .addSuperinterface(ClassName.get("", className))
                .build();

        JavaFile javaFile = JavaFile.builder("generated", classType)
                .build();

        javaFile.writeTo(path);
    }
    
    public void generateInvoker(JSONObject file, Path path)throws IOException {
    	String className = (String )file.get("name");
        String classDescription = (String )file.get("description");

        Builder invoke = MethodSpec.methodBuilder("invoke")
        		.returns(Object.class)
    		   	.addModifiers(Modifier.PUBLIC)
    		   	.addParameter(Invocation.class, "invocation")
                .addException(ClassName.get("", "exception.RemoteError"))
                .addException(IOException.class)
                .addException(ClassNotFoundException.class)
        		.addStatement("Object[] params = invocation.getInvocationData().getActualParams()");
        
        JSONArray operations = (JSONArray) file.get("operations");
        Iterable<MethodSpec> methods = new ArrayList<MethodSpec>();
        for(int i = 0; i < operations.size(); ++i){
            JSONObject method        = (JSONObject) operations.get(i);
            String methodName        = (String) method.get("name");
            String methodDescription = (String) method.get("description");
            String methodReturn      = (String) method.get("return");
            
            String methodInReturn    = "return " + methodName + "(";
            
            invoke.beginControlFlow("if (invocation.getInvocationData().getOperationName().equals( \"" + methodName + "\" ))" );
            	  
            JSONArray params = (JSONArray) method.get("params");
            Iterable<ParameterSpec> parameters = new ArrayList<ParameterSpec>();
            for (int j = 0; j < params.size(); j++) {
                JSONObject param        = (JSONObject) params.get(j);
                String paramName        = (String) param.get("name");
                String paramType        = (String) param.get("type");
                String paramDescription = (String) param.get("description");            
               
                if(j != params.size() -1)
                	methodInReturn += " (" + paramType + ") params[" + j + "], ";
                else
                	methodInReturn += " (" + paramType + ") params[" + j + "] ";
                methodDescription += "\n@param " + paramName + " " + paramDescription;

                ParameterSpec ps = ParameterSpec.builder(getType(paramType), paramName).build();
                ((ArrayList<ParameterSpec>) parameters).add(ps);
                
                
            }
            	methodInReturn += ")";
            invoke.addStatement(methodInReturn)
                  .endControlFlow();

            methodDescription += "\n@return " + methodReturn;

            MethodSpec ms = MethodSpec.methodBuilder(methodName)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(getType(methodReturn))
                    .addParameters(parameters)
                    .addJavadoc(methodDescription)
                    .addException(ClassName.get("", "exception.RemoteError")) // change the real package name of class exception.RemoteError
                    .addException(IOException.class)
                    .addException(ClassNotFoundException.class)
                    .build();
            ((ArrayList<MethodSpec>) methods).add(ms);
        }

        invoke.addStatement("return null");
        
        // Creating fields of aor and requestor
        FieldSpec id = FieldSpec.builder(Integer.class, "id")
                .addModifiers(Modifier.PRIVATE)
                .build();

        // Defining constructor of class
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Integer.class, "id")
                .addStatement("this.id = id")
                .build();
        
        MethodSpec getId = MethodSpec.methodBuilder("getId")
        				             .addModifiers(Modifier.PUBLIC)
        				             .returns(Integer.class)
        				             .addStatement("return id")
        				             .build();
        
        MethodSpec setId = MethodSpec.methodBuilder("setId")
	             .addModifiers(Modifier.PUBLIC)
	             .addParameter(Integer.class, "id")
	             .addStatement("this.id =  id")
	             .build();

        TypeSpec classType = TypeSpec.classBuilder("Invoker")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addField(id)
                .addMethod(constructor)
                .addMethod(getId)
                .addMethod(setId)
                .addMethods(methods)
                .addMethod(invoke.build())
                .addJavadoc(classDescription)
                .addSuperinterface(ClassName.get("", className))
                .build();

        JavaFile javaFile = JavaFile.builder("generated", classType)
                .build();

        javaFile.writeTo(path);
    }

    private Type getType(String type){
        if(type.equals("int")){
            return Integer.class;
        }else if(type.equals("float")){
            return Float.class;
        }else if(type.equals("boolean")){
            return Boolean.class;
        }else if(type.equals("string")){
            return String.class;
        }else if(type.equals("char")){
            return Character.class;
        }

        return String.class;
    }

    private String getCastType(String type){
        if(type.equals("int")){
            return "(Integer)";
        }else if(type.equals("float")){
            return "(Float)";
        }else if(type.equals("boolean")){
            return "(Boolean)";
        }else if(type.equals("string")){
            return "(String)";
        }else if(type.equals("char")){
            return "(Character)";
        }

        return "(String)";
    }

}
