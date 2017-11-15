package im.wangchao.mrouter.compiler;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static im.wangchao.mrouter.compiler.Constants.CLASSS_NAME;
import static im.wangchao.mrouter.compiler.Constants.CLASSS_PACKAGE;


/**
 * <p>Description  : BuildClass.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/9/5.</p>
 * <p>Time         : 上午11:15.</p>
 */
/*package*/ class BuildClass {

    private HashMap<String, String> mRouterMap = new HashMap<>();

    BuildClass(){

    }

    void put(String key, String clsName){
        mRouterMap.put(key, clsName);
    }

    JavaFile brewJava(TypeElement typeElement) throws Exception{
        return JavaFile.builder(CLASSS_PACKAGE, createType(typeElement))
                .addFileComment("Generated code from MRouter. Do not modify!").build();
    }

    private TypeSpec createType(TypeElement typeElement) throws Exception{
        TypeSpec.Builder result = TypeSpec.classBuilder(CLASSS_NAME)
                .addOriginatingElement(typeElement)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);


        TypeName map = ParameterizedTypeName.get(Map.class, String.class, String.class);

        FieldSpec.Builder fieldBuilder = FieldSpec.builder(map, "sRoutes", Modifier.STATIC, Modifier.PRIVATE, Modifier.FINAL);
        fieldBuilder.initializer("new $T<>()", HashMap.class);
        result.addField(fieldBuilder.build());

        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        for (Map.Entry<String, String> entry: mRouterMap.entrySet()){
            codeBlockBuilder.add("sRoutes.put($S, $S);\n", entry.getKey(), entry.getValue());
        }
        result.addStaticBlock(codeBlockBuilder.build());

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("get");
        methodBuilder.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        methodBuilder.returns(String.class);
        methodBuilder.addParameter(String.class, "path");
        methodBuilder.addStatement("return sRoutes.get(path)");

        result.addMethod(methodBuilder.build());

        return result.build();
    }
}
