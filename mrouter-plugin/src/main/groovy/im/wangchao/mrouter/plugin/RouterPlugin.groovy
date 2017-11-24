package im.wangchao.mrouter.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import im.wangchao.mrouter.plugin.transform.RouterTransform
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * <p>Description  : RouterPlugin.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/22.</p>
 * <p>Time         : 下午3:29.</p>
 */
class RouterPlugin implements Plugin<Project>{

    @Override void apply(Project project) {
        if (!project.plugins.hasPlugin(AppPlugin)) {
            throw new GradleException("Plugin requires the 'com.android.application' plugin to be configured.", null)
        }

        AppExtension android = project.extensions.getByType(AppExtension)
        android.registerTransform(new RouterTransform(project))
    }
}
