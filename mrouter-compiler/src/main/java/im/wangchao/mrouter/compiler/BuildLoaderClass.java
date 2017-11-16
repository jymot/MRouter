package im.wangchao.mrouter.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;

import static im.wangchao.mrouter.annotations.Constants.CLASSS_PACKAGE;
import static im.wangchao.mrouter.annotations.Constants.CLASS_ILOADER_NAME;
import static im.wangchao.mrouter.annotations.Constants.CLASS_IINTERCEPTOR;
import static im.wangchao.mrouter.annotations.Constants.CLASS_ILOADER;
import static im.wangchao.mrouter.annotations.Constants.CLASS_IROUTERSERVICE;


/**
 * <p>Description  : BuildClass.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/9/5.</p>
 * <p>Time         : 上午11:15.</p>
 */
/*package*/ class BuildLoaderClass {

    private static final String FIELD_ROUTES = "mRoutes";
    private static final String FIELD_ROUTERSERVICES = "mRouterServices";
    private static final String FIELD_INTERCEPTORS = "mInterceptors";

    private Map<String, Map<String, String>> mRoutes = new HashMap<>();
    private Map<String, String> mRouterServices = new HashMap<>();
    private Map<String, List<String>> mInterceptors = new HashMap<>();

    private Elements mElementUtils;

    BuildLoaderClass(Elements elements){
        this.mElementUtils = elements;
    }

    void putRoute(String routerName, String path, String targetClass){
        Map<String, String> map = mRoutes.computeIfAbsent(routerName, s -> new HashMap<>());
        map.put(path, targetClass);
    }

    void putRouterService(String routerName, String targetClass){
        mRouterServices.put(routerName, targetClass);
    }

    void putInterceptor(String routerName, String targetClass){
        List<String> list = mInterceptors.computeIfAbsent(routerName, s -> new ArrayList<>());
        list.add(targetClass);
    }

    JavaFile brewJava() throws Exception{
        return JavaFile.builder(CLASSS_PACKAGE, createType())
                .addFileComment("Generated code from MRouter. Do not modify!").build();
    }

    private TypeSpec createType() throws Exception{
        TypeSpec.Builder result = TypeSpec.classBuilder(CLASS_ILOADER_NAME)
                .addSuperinterface(ClassName.get(mElementUtils.getTypeElement(CLASS_ILOADER)))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        addFields(result);

        addConstructor(result);

        addMethod(result);

        return result.build();
    }

    private void addFields(TypeSpec.Builder result){
        // private Map<String, List<String>> mInterceptors = new HashMap<>();
        TypeName interceptorMap = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(String.class))
        );
        FieldSpec.Builder interceptorBuilder = FieldSpec.builder(interceptorMap, FIELD_INTERCEPTORS, Modifier.PRIVATE, Modifier.FINAL);
        interceptorBuilder.initializer("new $T<>()", HashMap.class);
        result.addField(interceptorBuilder.build());

        // private Map<String, String> mRouterServices = new HashMap<>();
        TypeName routerServiceMap = ParameterizedTypeName.get(Map.class, String.class, String.class);
        FieldSpec.Builder routerServiceBuilder = FieldSpec.builder(routerServiceMap, FIELD_ROUTERSERVICES, Modifier.PRIVATE, Modifier.FINAL);
        routerServiceBuilder.initializer("new $T<>()", HashMap.class);
        result.addField(routerServiceBuilder.build());


        // private Map<String, Map<String, String>> mRoutes = new HashMap<>();
        TypeName routeMap = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Map.class),
                        ClassName.get(String.class),
                        ClassName.get(String.class)
                )
        );
        FieldSpec.Builder routeBuilder = FieldSpec.builder(routeMap, FIELD_ROUTES, Modifier.PRIVATE, Modifier.FINAL);
        routeBuilder.initializer("new $T<>()", HashMap.class);
        result.addField(routeBuilder.build());
    }

    private void addConstructor(TypeSpec.Builder result){
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        // load all data
        mRouterServices.forEach((key, value) -> constructor.addStatement("$L.put($S, $S)", FIELD_ROUTERSERVICES, key, value));
        mRoutes.forEach((key, value) -> {
            constructor.addStatement("$T<String, String> $LMap = new $T<>()", Map.class, key, HashMap.class);
            value.forEach((path, cls) -> constructor.addStatement("$LMap.put($S, $S)", key, path, cls));
            constructor.addStatement("$L.put($S, $LMap)", FIELD_ROUTES, key, key);
        });
        mInterceptors.forEach((key, value) -> {
            constructor.addStatement("$T<String> $LList = new $T<>()", List.class, key, ArrayList.class);
            value.forEach(item -> constructor.addStatement("$LList.add($S)", key, item));
            constructor.addStatement("$L.put($S, $LList)", FIELD_INTERCEPTORS, key, key);
        });

        result.addMethod(constructor.build());
    }

    private void addMethod(TypeSpec.Builder result){
        // Map<String, List<IInterceptor>>
        ParameterizedTypeName loadInterceptorsParams = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(List.class),
                        ClassName.get(mElementUtils.getTypeElement(CLASS_IINTERCEPTOR))
                )
        );
        // Map<String, IRouterService>
        ParameterizedTypeName loadRouterServicesParams = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(mElementUtils.getTypeElement(CLASS_IROUTERSERVICE))
        );

        loadInterceptors(result, loadInterceptorsParams);

        loadRouterServices(result, loadRouterServicesParams);

        loadInterceptor(result, loadInterceptorsParams);

        loadRouterService(result, loadRouterServicesParams);

        getTargetClass(result);
    }

    /**
     * void loadInterceptors(Map<String, List<IInterceptor>> target){
     *     Set<String> names = mInterceptors.keySet();
     *     for (String name: names){
     *        loadInterceptor(name, target);
     *     }
     * }
     *
     */
    private void loadInterceptors(TypeSpec.Builder result, ParameterizedTypeName loadInterceptorsParams) {
        CodeBlock.Builder codeBlock = CodeBlock.builder();
        codeBlock.addStatement("$T<$T> names = $L.keySet()", Set.class, String.class, FIELD_INTERCEPTORS)
                 .beginControlFlow("for (String name: names)")
                 .addStatement("loadInterceptor(name, target)")
                 .endControlFlow();

        MethodSpec.Builder loadInterceptors = MethodSpec.methodBuilder("loadInterceptors")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(loadInterceptorsParams, "target").build())
                .addCode(codeBlock.build());
        result.addMethod(loadInterceptors.build());
    }

    /**
     * void loadRouterServices(Map<String, IRouterService> target){
     *      Set<String> names = mRouterServices.keySet();
     *      for (String name : names) {
     *          loadRouterService(name, target);
     *      }
     * }
     */
    private void loadRouterServices(TypeSpec.Builder result, ParameterizedTypeName loadRouterServicesParams) {
        CodeBlock.Builder codeBlock = CodeBlock.builder();
        codeBlock.addStatement("$T<$T> names = $L.keySet()", Set.class, String.class, FIELD_ROUTERSERVICES)
                 .beginControlFlow("for (String name: names)")
                 .addStatement("loadRouterService(name, target)")
                 .endControlFlow();

        MethodSpec.Builder loadRouterServices = MethodSpec.methodBuilder("loadRouterServices")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(loadRouterServicesParams, "target").build())
                .addCode(codeBlock.build());
        result.addMethod(loadRouterServices.build());
    }

    /**
     * List<IInterceptor> loadInterceptor(String name, Map<String, List<IInterceptor>> target){
     *     List<IInterceptor> list = new ArrayList<>();
     *
     *     List<String> interceptorClassName = mInterceptors.get(name);
     *     if (interceptorClassName == null || interceptorClassName.size() == 0){
     *          return list;
     *     }
     *     try {
     *          for (String cls: interceptorClassName){
     *              list.add((IInterceptor) Class.forName(cls).newInstance());
     *          }
     *          target.put(name, list);
     *     } catch (Exception ignore){}
     *
     *     return list;
     * }
     */
    private void loadInterceptor(TypeSpec.Builder result, ParameterizedTypeName loadInterceptorsParams) {
        CodeBlock.Builder codeBlock = CodeBlock.builder();
        codeBlock.addStatement("$T<$T> list = new $T<>()", List.class, ClassName.get(mElementUtils.getTypeElement(CLASS_IINTERCEPTOR)), ArrayList.class)
                 .addStatement("$T<$T> interceptorClassName = $L.get(name)", List.class, String.class, FIELD_INTERCEPTORS)
                 .add("if (interceptorClassName == null || interceptorClassName.size() == 0) {\n")
                 .addStatement("return list")
                 .add("}\n")
                 .add("try {\n")
                 .beginControlFlow("for (String cls: interceptorClassName)")
                 .addStatement("list.add(($T) $T.forName(cls).newInstance())", ClassName.get(mElementUtils.getTypeElement(CLASS_IINTERCEPTOR)), Class.class)
                 .endControlFlow()
                 .addStatement("target.put(name, list)")
                 .add("} catch ($T ignore){}\n", Exception.class)
                 .addStatement("return list");

        MethodSpec.Builder loadInterceptor = MethodSpec.methodBuilder("loadInterceptor")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "name").build())
                .addParameter(ParameterSpec.builder(loadInterceptorsParams, "target").build())
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(mElementUtils.getTypeElement(CLASS_IINTERCEPTOR))))
                .addCode(codeBlock.build());
        result.addMethod(loadInterceptor.build());
    }

    /**
     * IRouterService loadRouterService(String name, Map<String, IRouterService> target){
     *     try {
     *          IRouterService service = (IRouterService) Class.forName(mRouterServices.get(name)).newInstance();
     *          target.put(name, service);
     *          return service;
     *     } catch (Exception e) {
     *          return null;
     *     }
     * }
     */
    private void loadRouterService(TypeSpec.Builder result, ParameterizedTypeName loadRouterServicesParams) {
        CodeBlock.Builder codeBlock = CodeBlock.builder();
        ClassName IRouterService = ClassName.get(mElementUtils.getTypeElement(CLASS_IROUTERSERVICE));
        codeBlock.add("try {\n")
                 .addStatement("$T service = ($T) $T.forName($L.get(name)).newInstance()", IRouterService, IRouterService, Class.class, FIELD_ROUTERSERVICES)
                 .addStatement("target.put(name, service)")
                 .addStatement("return service")
                 .add("} catch (Exception e) {\n")
                 .addStatement("return null")
                 .add("}\n");

        MethodSpec.Builder loadRouterService = MethodSpec.methodBuilder("loadRouterService")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "name").build())
                .addParameter(ParameterSpec.builder(loadRouterServicesParams, "target").build())
                .returns(ClassName.get(mElementUtils.getTypeElement(CLASS_IROUTERSERVICE)))
                .addCode(codeBlock.build());
        result.addMethod(loadRouterService.build());
    }

    /**
     * String getTargetClass(String serviceName, String path){
     *     return mRoutes.get(serviceName).get(path);
     * }
     */
    private void getTargetClass(TypeSpec.Builder result) {
        MethodSpec.Builder getTargetClass = MethodSpec.methodBuilder("getTargetClass")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "serviceName").build())
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "path").build())
                .returns(ClassName.get(String.class))
                .addStatement("return $L.get(serviceName).get(path)", FIELD_ROUTES);
        result.addMethod(getTargetClass.build());
    }
}
