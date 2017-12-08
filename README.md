# MRouter
A Modularization Component.

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
android {
    ...
    defaultConfig {
        ...
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = [ moduleName : project.getName(), appModule: "1" ]
            }
        }
    }
}
...
dependencies {
    implementation 'im.wangchao:mrouter:0.1.5'
    annotationProcessor 'im.wangchao:mrouter-compiler:0.1.3'
}
```
And other modules.
```gradle
android {
    ...
    defaultConfig {
        ...
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = [ moduleName : project.getName() ]
            }
        }
    }
}
...
dependencies {
    compileOnly 'im.wangchao:mrouter:0.1.5'
    annotationProcessor 'im.wangchao:mrouter-compiler:0.1.3'
}
```

## How to use
### 1.Initialization
```java
public class App extend Application {

    ...

    @Override public void onCreate() {
        super.onCreate();
        Router.init();
    }

    ...
}
```
### 2.Configuration
#### 2.1 Register Route
**Use Custom RouterService**
```java
@Route(path = "/one", routerName = "one")
public class ModuleOneActivity extends AppCompatActivity {
    ...
}
```
**Use Default RouterService**
```java
@Route(path = "/two")
public class ModuleTwoActivity extends AppCompatActivity {
    ...
}
```
#### 2.2 Register RouterService
```java
@RouterService("one")
public class ModuleOneService implements IRouterService {
    ...
}
```
#### 2.3 Register Interceptor
**Global Interceptor**
```java
@Interceptor(priority = 1)
public class GlobalLevelOneInterceptor implements IInterceptor{
    ...
}
```
**Child Interceptor**
```java
@Interceptor(routerName = "one")
public class OneInterceptor implements IInterceptor {
    ...
}
```
#### 2.4 Register Provider
```java
@Provider(name = "test", routerName = "two")
public class ModuleTwoProvider implements IProvider {
    ...
}
```
### 3.Use
#### 3.1 Push/Pop
```java
// push to Activity that configure @Route(path = "/two")
Router.push(this, "router:///two"));
...
// push to Activity that configure @Route(path = "/one", routerName = "one")
Router.push(this, "one:///one")
...
// Pop
Router.pop(this);
```
#### 3.2 Request
```java
Router.request("two://test", new RouterCallback() {
    @Override public void onSuccess(RouteIntent route) {
        String result = route.bundle().getString("result");
        Log.e("wcwcwc", "result =>> " + result);
    }

    @Override public void onFailure(RouteIntent route, Exception e) {

    }
});
```

### License

    Copyright 2017 Mot. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
