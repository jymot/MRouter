package im.wangchao.mrouter.compiler;

import com.google.auto.service.AutoService;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import im.wangchao.mrouter.annotations.Interceptor;
import im.wangchao.mrouter.annotations.Provider;
import im.wangchao.mrouter.annotations.Route;
import im.wangchao.mrouter.annotations.RouterService;

import static javax.tools.Diagnostic.Kind.NOTE;

/**
 * <p>Description  : RouterProcessor.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/11.</p>
 * <p>Time         : 下午3:17.</p>
 */
@AutoService(Processor.class)
public class RouterLoaderProcessor extends AbstractProcessor {

    private Elements mElementUtils;
    private Filer mFiler;

    @Override public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
    }

    @Override public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(Interceptor.class.getCanonicalName());
        supportTypes.add(Route.class.getCanonicalName());
        supportTypes.add(RouterService.class.getCanonicalName());
        supportTypes.add(Provider.class.getCanonicalName());
        return supportTypes;
    }

    @Override public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        logMessage("annotations >>> " +annotations.size());

        final int size = annotations.size();
        if (size == 0){
            return false;
        }

        BuildLoaderClass buildClass = new BuildLoaderClass(mElementUtils);

        for (TypeElement element: annotations){
            logMessage("process >>> " + element.getQualifiedName() + " <<< ");
            findTargetClass(roundEnv, element, buildClass);
        }

        try {
            buildClass.brewJava().writeTo(mFiler);
        } catch (Exception e) {
            e.printStackTrace();
            logMessage(e.getMessage());
        }

        return true;
    }

    private void parseRouterService(TypeElement typeElement, BuildLoaderClass buildClass){
        String targetClass = typeElement.getQualifiedName().toString();
        RouterService routerService = typeElement.getAnnotation(RouterService.class);
        buildClass.putRouterService(routerService.value(), targetClass);
        logMessage("        >>> parseRouterService -> name: " + routerService.value() + ", class: " + targetClass);
    }

    private void parseRoute(TypeElement typeElement, BuildLoaderClass buildClass){
        String targetClass = typeElement.getQualifiedName().toString();
        Route route = typeElement.getAnnotation(Route.class);
        buildClass.putRoute(route.routerName(), route.path(), targetClass);
        logMessage("        >>> parseRoute -> name: " + route.routerName() + ", path: " + route.path() + ", class: " + targetClass);
    }

    private void parseInterceptor(TypeElement typeElement, BuildLoaderClass buildClass){
        String targetClass = typeElement.getQualifiedName().toString();
        Interceptor interceptor = typeElement.getAnnotation(Interceptor.class);
        buildClass.putInterceptor(interceptor.routerName(), interceptor.priority(), targetClass);
        logMessage("        >>> parseInterceptor -> name: " + interceptor.routerName() + ", class: " + targetClass);
    }

    private void parseProvider(TypeElement typeElement, BuildLoaderClass buildClass){
        String targetClass = typeElement.getQualifiedName().toString();
        Provider provider = typeElement.getAnnotation(Provider.class);
        final String key = provider.routerName().concat("://").concat(provider.name());
        buildClass.putProvider(key, targetClass);
        logMessage("        >>> parseProvider -> name: " + key + ", class: " + targetClass);
    }

    private void findTargetClass(RoundEnvironment env, TypeElement typeElement, BuildLoaderClass buildClass){
        final Set<? extends Element> elements = env.getElementsAnnotatedWith(typeElement);
        for (Element element: elements){
            if (env.getElementsAnnotatedWith(RouterService.class).contains(element)){
                parseRouterService((TypeElement) element, buildClass);
            } else if (env.getElementsAnnotatedWith(Route.class).contains(element)){
                parseRoute((TypeElement) element, buildClass);
            } else if (env.getElementsAnnotatedWith(Interceptor.class).contains(element)){
                parseInterceptor((TypeElement) element, buildClass);
            } else if (env.getElementsAnnotatedWith(Provider.class).contains(element)){
                parseProvider((TypeElement) element, buildClass);
            }
        }
    }

    private String getPackageName(TypeElement type) {
        return mElementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private void logMessage(String msg){
        processingEnv.getMessager().printMessage(NOTE, msg);
    }
}
