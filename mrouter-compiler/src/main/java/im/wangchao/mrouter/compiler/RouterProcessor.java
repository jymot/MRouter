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
public class RouterProcessor extends AbstractProcessor {
    private static final String CLASS_SIMPLE_NAME = "Router";

    private Elements mElementUtils;
    private Filer mFiler;

    @Override public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
    }

    @Override public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(Route.class.getCanonicalName());
        supportTypes.add(RouterService.class.getCanonicalName());
        return supportTypes;
    }

    @Override public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        BuildClass buildClass = new BuildClass();

        for (TypeElement element: annotations){
            logMessage("Router >>> " + element.getQualifiedName());
            findTargetClass(roundEnv, element, buildClass);

            try {
                buildClass.brewJava(element).writeTo(mFiler);
            } catch (Exception e) {
                e.printStackTrace();
                logMessage(e.getMessage());
            }
        }

        return true;
    }

    private void findTargetClass(RoundEnvironment env, TypeElement typeElement, BuildClass buildClass){
        final Set<? extends Element> elements = env.getElementsAnnotatedWith(typeElement);
        TypeElement targetClass;
//        for (Element element: elements){
//            if (env.getElementsAnnotatedWith(RoutePath.class).contains(element)){
//                targetClass = (TypeElement) element;
//                RoutePath route = element.getAnnotation(RoutePath.class);
//                buildClass.put(route.value(), targetClass.getQualifiedName().toString());
//                logMessage("findTargetClass >>>>> path: " + route.value() + ", class: " + targetClass.getQualifiedName());
//            }
//        }
    }

    private String getPackageName(TypeElement type) {
        return mElementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private void logMessage(String msg){
        processingEnv.getMessager().printMessage(NOTE, msg);
    }
}
