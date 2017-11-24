package im.wangchao.mrouter.plugin;

import org.gradle.api.Project;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.List;

/**
 * <p>Description  : RouterClassVisitor.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/22.</p>
 * <p>Time         : 下午1:30.</p>
 */
public class RouterClassVisitor extends ClassVisitor {
    private Project project;
    private List<String> loaders;

    public RouterClassVisitor(int api, ClassVisitor cv, Project project, List<String> loaders) {
        super(api, cv);
        this.project = project;
        this.loaders = loaders;
    }

    @Override public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return super.visitField(access, name, desc, signature, value);
    }

    @Override public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (cv != null){
            MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
            mv = new AdviceAdapter(Opcodes.ASM5, mv, access, name, desc) {

                @Override protected void onMethodEnter() {
                    super.onMethodEnter();
                    project.getLogger().error("============ RouterClassVisitor :: onMethodEnter() ============ " + name);
                    if (name.equals("<init>")){
//                        mv.visitLdcInsn("wcwcwc");
//                        mv.visitLdcInsn("inject");
//                        mv.visitMethodInsn(INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
                        if (loaders != null){
                            for (String cls: loaders){
                                mv.visitLdcInsn(cls);
                                mv.visitMethodInsn(
                                        INVOKESTATIC,
                                        "im/wangchao/mrouter/loaders/APP_RouterLoader_AutoGeneration",
                                        "addLoader", "(Ljava/lang/String;)V",
                                        false
                                );
                            }
                        }
                    }
                }

            };

            return mv;
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
