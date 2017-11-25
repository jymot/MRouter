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
import static im.wangchao.mrouter.annotations.Constants.CLASS_IINTERCEPTOR;
import static im.wangchao.mrouter.annotations.Constants.CLASS_ILOADER;
import static im.wangchao.mrouter.annotations.Constants.CLASS_IPROVIDER;
import static im.wangchao.mrouter.annotations.Constants.CLASS_IROUTERSERVICE;
import static im.wangchao.mrouter.annotations.Constants.INTERCEPTOR_DEFAULT_PRIORITY;
import static im.wangchao.mrouter.annotations.Constants.getLoaderClassName;


/**
 * <p>Description  : BuildClass.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/9/5.</p>
 * <p>Time         : 上午11:15.</p>
 */
/*package*/ class BuildLoaderClass {

    private static class InterceptorPriority{
        String cls;
        int priority = INTERCEPTOR_DEFAULT_PRIORITY;
        InterceptorPriority(int priority, String cls){
            this.priority = priority;
            this.cls = cls;
        }

        int getPriority(){
            return priority;
        }
    }

    private static final String FIELD_ROUTES = "mRoutes";
    private static final String FIELD_ROUTERSERVICES = "mRouterServices";
    private static final String FIELD_INTERCEPTORS = "mInterceptors";
    private static final String FIELD_PROVIDERS = "mProviders";
    private static final String FIELD_LOADERS = "mLoaders";

    private Map<String, Map<String, String>> mRoutes = new HashMap<>();
    private Map<String, String> mRouterServices = new HashMap<>();
    private Map<String, Map<Integer, List<String>>> mInterceptors = new HashMap<>();
    private Map<String, String> mProviders = new HashMap<>();

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

    void putInterceptor(String routerName, int priority, String targetClass){
        Map<Integer, List<String>> map = mInterceptors.computeIfAbsent(routerName, s -> new HashMap<>());
        List<String> list = map.computeIfAbsent(priority, integer -> new ArrayList<>());
        list.add(targetClass);
    }

    void putProvider(String key, String targetClass) {
        mProviders.put(key, targetClass);
    }

    JavaFile brewJava(String moduleName) throws Exception{
        return JavaFile.builder(CLASSS_PACKAGE, createType(moduleName))
                .addFileComment("Generated code from MRouter. Do not modify!").build();
    }

    private TypeSpec createType(String moduleName) throws Exception{
        TypeSpec.Builder result = TypeSpec.classBuilder(getLoaderClassName(moduleName))
                .addSuperinterface(ClassName.get(mElementUtils.getTypeElement(CLASS_ILOADER)))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        addFields(result);

        addConstructor(result);

        addMethod(result);

        return result.build();
    }

    private void addFields(TypeSpec.Builder result){
        // private Map<String, Map<Integer, List<String>>> mInterceptors = new HashMap<>();
        TypeName interceptorMap = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Map.class),
                        ClassName.get(Integer.class),
                        ParameterizedTypeName.get(List.class, String.class))
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

        // private Map<String, String> mProviders = new HashMap<>();
        TypeName providersMap = ParameterizedTypeName.get(Map.class, String.class, String.class);
        FieldSpec.Builder providersBuilder = FieldSpec.builder(providersMap, FIELD_PROVIDERS, Modifier.PRIVATE, Modifier.FINAL);
        providersBuilder.initializer("new $T<>()", HashMap.class);
        result.addField(providersBuilder.build());

        // private static final List<String> mLoaders = new ArrayList<>();
        TypeName list = ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(String.class));
        FieldSpec.Builder sLoaders = FieldSpec.builder(list, FIELD_LOADERS, Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC);
        sLoaders.initializer("new $T<>()", ArrayList.class);
        result.addField(sLoaders.build());
    }

    private void addConstructor(TypeSpec.Builder result){
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        // load all data
        constructor.addComment("Load RouterService");
        mRouterServices.forEach((key, value) -> constructor.addStatement("$L.put($S, $S)", FIELD_ROUTERSERVICES, key, value));
        constructor.addCode(CodeBlock.builder().add("\n").build());

        constructor.addComment("Load Routes");
        mRoutes.forEach((key, value) -> {
            constructor.addStatement("$T<String, String> $LMapRoutes = new $T<>()", Map.class, key, HashMap.class);
            value.forEach((path, cls) -> constructor.addStatement("$LMapRoutes.put($S, $S)", key, path, cls));
            constructor.addStatement("$L.put($S, $LMapRoutes)", FIELD_ROUTES, key, key);
        });
        constructor.addCode(CodeBlock.builder().add("\n").build());

        constructor.addComment("Load Interceptor");
        mInterceptors.forEach((key, value) -> {
            constructor.addStatement("$T<$T, $T> $LMapInterceptors = new $T<>()",
                    Map.class, Integer.class, ParameterizedTypeName.get(List.class, String.class), key, HashMap.class);

            value.forEach((index, list) -> {
                constructor.addStatement("$T<String> $LList$LInterceptors = new $T<>()", List.class, key, index, ArrayList.class);
                list.forEach(item -> constructor.addStatement("$LList$LInterceptors.add($S)", key, index, item));
                constructor.addStatement("$LMapInterceptors.put($L, $LList$LInterceptors)", key, index, key, index);
            });

            constructor.addStatement("$L.put($S, $LMapInterceptors)", FIELD_INTERCEPTORS, key, key);
        });
        constructor.addCode(CodeBlock.builder().add("\n").build());

        constructor.addComment("Load Provider");
        mProviders.forEach((key, value) -> constructor.addStatement("$L.put($S, $S)", FIELD_PROVIDERS, key, value));

        result.addMethod(constructor.build());
    }

    private void addMethod(TypeSpec.Builder result){
        // Map<String, Map<Integer, List<IInterceptor>>>
        ParameterizedTypeName loadInterceptorsParams = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Map.class),
                        ClassName.get(Integer.class),
                        ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(mElementUtils.getTypeElement(CLASS_IINTERCEPTOR)))
                )
        );
        // Map<String, IRouterService>
        ParameterizedTypeName loadRouterServicesParams = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(mElementUtils.getTypeElement(CLASS_IROUTERSERVICE))
        );
        // Map<String, IProvider>
        ParameterizedTypeName loadProvidersParams = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(mElementUtils.getTypeElement(CLASS_IPROVIDER))
        );

        loadInterceptors(result, loadInterceptorsParams);

        loadRouterServices(result, loadRouterServicesParams);

        loadProviders(result, loadProvidersParams);

        loadInterceptor(result, loadInterceptorsParams);

        loadRouterService(result, loadRouterServicesParams);

        getTargetClass(result);

        loadProvider(result, loadProvidersParams);

        loaderClass(result);


        MethodSpec.Builder loadProvider = MethodSpec.methodBuilder("addLoader")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "cls").build())
                .addStatement("$L.add(cls)", FIELD_LOADERS);
        result.addMethod(loadProvider.build());
    }

    /**
     * void loadInterceptors(Map<String, Map<Integer, List<IInterceptor>>> target){
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
     * void loadProviders(Map<String, IProvider> target){
     *      Set<String> names = mProviders.keySet();
     *      for (String name: names){
     *          loadProvider(name, target);
     *      }
     * }
     */
    private void loadProviders(TypeSpec.Builder result, ParameterizedTypeName loadProvidersParams) {
        CodeBlock.Builder codeBlock = CodeBlock.builder();
        codeBlock.addStatement("$T<$T> names = $L.keySet()", Set.class, String.class, FIELD_PROVIDERS)
                .beginControlFlow("for (String name: names)")
                .addStatement("loadProvider(name, target)")
                .endControlFlow();

        MethodSpec.Builder loadProviders = MethodSpec.methodBuilder("loadProviders")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(loadProvidersParams, "target").build())
                .addCode(codeBlock.build());
        result.addMethod(loadProviders.build());
    }

    /**
     * Map<Integer, List<IInterceptor>> loadInterceptor(String name, Map<String, Map<Integer, List<IInterceptor>>> target){
     *      Map<Integer, List<IInterceptor>> map = target.get(name);
     *      if (map == null){
     *          map = new HashMap<>();
     *          target.put(name, map);
     *      }
     *
     *      Map<Integer, List<String>> orderMap = mInterceptors.get(name);
     *      if (orderMap == null || orderMap.size() == 0){
     *          return map;
     *      }
     *      int index;
     *      List<String> interceptorsCls;
     *      List<IInterceptor> interceptors;
     *      for (Map.Entry<Integer, List<String>> entry: orderMap.entrySet()){
     *          index = entry.getKey();
     *          interceptors = map.get(index);
     *          if (interceptors == null){
     *              interceptors = new ArrayList<>();
     *              map.put(index, interceptors);
     *          }
     *          interceptorsCls = entry.getValue();
     *          if (interceptorsCls == null){
     *              continue;
     *          }
     *          for (String cls: interceptorsCls){
     *              try {
     *                  interceptors.add((IInterceptor) Class.forName(cls).newInstance());
     *              } catch (Exception ignore){}
     *          }
     *      }
     *
     *      return map;
     * }
     */
    private void loadInterceptor(TypeSpec.Builder result, ParameterizedTypeName loadInterceptorsParams) {
        CodeBlock.Builder codeBlock = CodeBlock.builder();
        codeBlock.addStatement("$T<$T, $T> map = target.get(name)",
                Map.class, Integer.class,
                ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(mElementUtils.getTypeElement(CLASS_IINTERCEPTOR))))
                 .add("if (map == null){\n")
                 .addStatement("map = new $T<>()", HashMap.class)
                 .addStatement("target.put(name, map)")
                 .add("}\n")
                 .addStatement("$T<$T, $T> orderMap = $L.get(name)",
                         Map.class, Integer.class, ParameterizedTypeName.get(List.class, String.class), FIELD_INTERCEPTORS)
                 .add("if (orderMap == null || orderMap.size() == 0) {\n")
                 .addStatement("return map")
                 .add("}\n")
                 .addStatement("int index")
                 .addStatement("$T<$T> interceptorsCls", List.class, String.class)
                 .addStatement("$T<$T> interceptors", List.class, ClassName.get(mElementUtils.getTypeElement(CLASS_IINTERCEPTOR)))
                 .beginControlFlow("for($T<$T, $T> entry: orderMap.entrySet())",
                         Map.Entry.class, Integer.class, ParameterizedTypeName.get(List.class, String.class))
                 .addStatement("index = entry.getKey()")
                 .addStatement("interceptors = map.get(index)")
                 .add("if (interceptors == null) {\n")
                 .addStatement("interceptors = new $T<>()", ArrayList.class)
                 .addStatement("map.put(index, interceptors)")
                 .add("}\n")
                 .addStatement("interceptorsCls = entry.getValue()")
                 .add("if (interceptorsCls == null) {\n")
                 .addStatement("continue")
                 .add("}\n")
                 .beginControlFlow("for (String cls: interceptorsCls)\n")
                 .add("try {\n")
                 .addStatement("interceptors.add(($T) $T.forName(cls).newInstance())", ClassName.get(mElementUtils.getTypeElement(CLASS_IINTERCEPTOR)), Class.class)
                 .add("} catch ($T ignore){}\n", Exception.class)
                 .endControlFlow()
                 .endControlFlow()
                 .addStatement("return map");

        MethodSpec.Builder loadInterceptor = MethodSpec.methodBuilder("loadInterceptor")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "name").build())
                .addParameter(ParameterSpec.builder(loadInterceptorsParams, "target").build())
                .returns(ParameterizedTypeName.get(
                        ClassName.get(Map.class), ClassName.get(Integer.class),
                        ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(mElementUtils.getTypeElement(CLASS_IINTERCEPTOR)))))
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

    /**
     * IProvider loadProvider(String key, Map<String, IProvider> target){
     *     try {
     *          IProvider provider = (IProvider) Class.forName(mProviders.get(key)).newInstance();
     *          target.put(key, provider);
     *          return provider;
     *     } catch (Exception ignore) {
     *          return null;
     *     }
     * }
     */
    private void loadProvider(TypeSpec.Builder result, ParameterizedTypeName loadProvidersParams) {
        CodeBlock.Builder codeBlock = CodeBlock.builder();
        ClassName IProvider = ClassName.get(mElementUtils.getTypeElement(CLASS_IPROVIDER));
        codeBlock.add("try {\n")
                .addStatement("$T provider = ($T) $T.forName($L.get(key)).newInstance()", IProvider, IProvider, Class.class, FIELD_PROVIDERS)
                .addStatement("target.put(key, provider)")
                .addStatement("return provider")
                .add("} catch (Exception e) {\n")
                .addStatement("return null")
                .add("}\n");

        MethodSpec.Builder loadProvider = MethodSpec.methodBuilder("loadProvider")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "key").build())
                .addParameter(ParameterSpec.builder(loadProvidersParams, "target").build())
                .returns(ClassName.get(mElementUtils.getTypeElement(CLASS_IPROVIDER)))
                .addCode(codeBlock.build());
        result.addMethod(loadProvider.build());
    }

    /**
     * List<String> loaderClass(){
     *     return mLoaders;
     * }
     */
    private void loaderClass(TypeSpec.Builder result){
        TypeName list = ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(String.class));

        MethodSpec.Builder loaderClass = MethodSpec.methodBuilder("loaderClass")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(list)
                .addStatement("return $L", FIELD_LOADERS);
        result.addMethod(loaderClass.build());
    }
}
