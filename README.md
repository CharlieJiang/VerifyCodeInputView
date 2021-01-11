
# VerifyCodeInputView

自定义验证码输入框，支持自定义验证码输入框的宽高、间距、数量、输入框边框的宽度、颜色，支持输入框宽度自适应。  
目前支持三种输入框类型：线形输入框、矩形输入框、圆形输入框。具体效果如下：

# 使用效果
* **线形输入框**

<img src="https://github.com/CharlieJiang/VerifyCodeInputView/blob/main/screenshot/line.jpg" alt="线形输入框" width="720" height="1280" align="middle"/>

* **矩形输入框**

<img src="https://github.com/CharlieJiang/VerifyCodeInputView/blob/main/screenshot/rect.jpg" alt="矩形输入框" width="720" height="1280" align="middle"/>

* **圆形输入框**

<img src="https://github.com/CharlieJiang/VerifyCodeInputView/blob/main/screenshot/circle.jpg" alt="圆形输入框" width="720" height="1280" align="middle"/>

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
* 添加项目依赖<br/>
在需要调用的module的build.gradle中添加依赖：
```css
implementation ("com.github.CharlieJiang:VerifyCodeInputView:v1.1.2")
```
* 在xml布局中引用
```xml
    <com.cocoas.view.VerifyCodeInputView
        android:id="@+id/main_verifyCodeInputView_customMulti"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:inputType="number"
        app:autoFit="true"
        app:boxCount="4"
        app:boxSpacing="10dp"
        app:boxHeight="60dp"
        app:boxWidth="60dp"
        app:boxBorderHeight="3dp"
        app:boxBorderColorNormal="@android:color/black"
        app:boxBorderColorFocused="@android:color/holo_red_dark"
        app:boxType="circle"
        ></com.cocoas.view.VerifyCodeInputView>
```
# 属性说明
1. **boxWidth、boxHeight**<br/>
> 单个验证码输入框的宽高（暂时只支持宽高相等的设置，当宽高值不同时，取两者中较小值）  

2. **boxSpacing**<br/>
> 单个验证码输入框间的间距（暂时只支持宽高相等的设置，当宽高值不同时，取两者中较小值） 

3. **boxCount**<br/>
> 验证码位数
4. **boxBorderHeight**<br/>
> 单个验证码输入框的边框高度
5. **boxBorderColorNormal**<br/>
> 单个验证码输入框边框的默认颜色
6. **boxBorderColorFocused**<br/>
> 单个验证码输入框边框的高亮颜色
7. **autoFit**<br/>
> 设置单个验证码输入框的宽高是否根据父布局宽度自适应。   
注意：此属性需要与android:layout_width="match_parent"属性一起使用，否则可能无效。
8. **boxType**<br/>
> 设置输入框类型，类型值及说明如下：

| boxType值 | 说明 |
| ---- | ---- |
| line | 线形输入框（输入框底部显示一条横线）（默认值） |
| rect | 矩形输入框（目前只支持正方形） |
| circle | 圆形输入框 |


