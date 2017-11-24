# MRouter

## Gradle
Add mrouter-plugin as a dependency in your main build.gradle in the root of your project:
```gradle
buildscript {
    dependencies {
        classpath 'im.wangchao:mrouter-plugin:0.1.0'
    }
}
```

Then you need to "apply" the plugin and add dependencies by adding the following lines to your app/build.gradle.
```gradle
apply plugin: 'im.wangchao.mrouter'
...
...
dependencies {
    implementation 'im.wangchao:mrouter:0.1.2'
    annotationProcessor 'im.wangchao:mrouter-compiler:0.1.2'
}
```