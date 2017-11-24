package im.wangchao.mrouter.plugin.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import im.wangchao.mrouter.plugin.RouterClassVisitor
import org.gradle.api.Project
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile
/**
 * <p>Description  : RouterTransform.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/22.</p>
 * <p>Time         : 下午3:28.</p>
 */
class RouterTransform extends Transform{

    Project project

    RouterTransform(Project project){
        this.project = project
    }

    @Override String getName() {
        return "RouterTransform"
    }

    @Override Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override boolean isIncremental() {
        return false
    }

    @Override void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        def inputs = transformInvocation.getInputs()
        def outputProvider = transformInvocation.getOutputProvider()

        List<String> loaders = new ArrayList<>();

        inputs.each { TransformInput input ->

            project.logger.error(" ===== 遍历 jar ->")

            //对类型为jar文件的input进行遍历
            input.jarInputs.each { JarInput jarInput->

                JarFile jarFile = new JarFile(jarInput.file)
                jarFile.stream()
                        .forEach({ JarEntry entry ->
                    if (entry.name.endsWith("_RouterLoader_AutoGeneration.class")){
                        project.logger.error("jar:" + entry.getName())
                        loaders.add(entry.getName())
                    }
                })

                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if(jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                //生成输出路径
                def dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)

                //将输入内容复制到输出
                FileUtils.copyFile(jarInput.file, dest)
            }

            project.logger.error(" ===== 遍历 directory ->")

            input.directoryInputs.each { DirectoryInput directoryInput->

                if (directoryInput.file.isDirectory()){
                    directoryInput.file.eachFileRecurse { File file ->
                        def name = file.name

                        if (name.endsWith(".class")
                                && !name.startsWith("R\$")
                                && !name.equals("R.class")
                                && !name.equals("BuildConfig.class")){

                            project.logger.error("file ---> class: ${file.parentFile.absolutePath}/${name}")
                            if (name.equals("APP_RouterLoader_AutoGeneration.class")){
                                ClassReader cr = new ClassReader(file.bytes)
                                ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
                                ClassVisitor cv = new RouterClassVisitor(Opcodes.ASM5, cw, project, loaders)

                                cr.accept(cv, ClassReader.EXPAND_FRAMES)

                                byte[] code = cw.toByteArray()

                                FileOutputStream fos = new FileOutputStream(file.parentFile.absolutePath + File.separator + name)
                                fos.write(code)
                                fos.close()
                            }
                        }

                    }
                }

                // 获取output目录
                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)

                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
        }

    }
}
