
# VerifyCodeInputView

自定义验证码输入框，支持自定义验证码输入框的宽高、间距、数量、输入框边框的宽度、颜色，支持输入框宽度自适应。

# 使用效果
未输入数字效果

<img src="https://github.com/CharlieJiang/VerifyCodeInputView/blob/main/screenshot/UseCase_not_input.jpg" alt="使用效果图——未输入" width="720" height="1280" align="middle"/>

输入数字后效果

<img src="https://github.com/CharlieJiang/VerifyCodeInputView/blob/main/screenshot/UseCase_input.jpg" alt="使用效果图——已输入" width="720" height="1280" align="middle"/>

# 引用方式
* 添加JitPack仓库<br/>
在项目根目录的build.gradle中配置JitPack仓库：
```css
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
* 添加项目依赖
在需要调用的module的build.gradle中添加依赖：
```css
implementation ("com.github.CharlieJiang:VerifyCodeInputView:v1.0")
```
* 在xml布局中引用
```xml
    <com.cocoas.view.VerifyCodeInputView
        android:id="@+id/main_verifyCodeInputView_default"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:inputType="number">
    </com.cocoas.view.VerifyCodeInputView>
```
