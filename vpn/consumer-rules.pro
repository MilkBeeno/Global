# 基本指令
# *************************************************************************************************/

# 代码混淆压缩比，在0和7之间，默认为5，一般不需要改
-optimizationpasses 5

# 混淆时不使用大小写混合，混淆后的类名为小写，Windows用户必须指定，否则当你的项目中有超过26个类的
#话，ProGuard就会默认混用大小写文件名，而导致class文件相互覆盖。
-dontusemixedcaseclassnames

# 指定不去忽略非公共的库和类，不要跳过对非公开类的处理，默认情况下是跳过的
-dontskipnonpubliclibraryclasses

# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers

# 不做预校验，preverify是proguard的4个步骤之一
# Android不需要preverify，去掉这一步可加快混淆速度
-dontpreverify

# 有了verbose这句话，混淆后就会生成映射文件
# 包含有类名->混淆后类名的映射关系
# 然后使用printmapping指定映射文件的名称
-verbose
-printmapping proguardMapping.txt

# 指定混淆时采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# 保护代码中的Annotation不被混淆，这在JSON实体映射时非常重要，比如fastJson
-keepattributes *Annotation*

# 避免混淆泛型，这在JSON实体映射时非常重要，比如fastJson
-keepattributes Signature

# 抛出异常时保留代码行号，在异常分析中可以方便定位
-keepattributes SourceFile,LineNumberTable

# 特殊保留
# *************************************************************************************************/

# 避免混淆Android基本组件
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View

# 保留support下的所有类及其内部类
-keep class android.support.** {*;}
-keep interface android.support.** {*;}
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**
-dontwarn android.support.**

# 保留androidx下的所有类及其内部类
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-keep class com.google.android.material.** {*;}
-dontwarn androidx.**
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**

# 保持Activity中与View相关方法不被混淆
-keepclassmembers class * extends android.app.Activity{
        public void *(android.view.View);
}

# 避免混淆所有native的方法,涉及到C、C++
-keepclasseswithmembernames class * {
        native <methods>;
}

# 避免混淆自定义控件类的get/set方法和构造函数
-keep public class * extends android.view.View{
        *** get*();
        void set*(***);
        public <init>(android.content.Context);
        public <init>(android.content.Context,android.util.AttributeSet);
        public <init>(android.content.Context,android.util.AttributeSet,int);
}
-keepclasseswithmembers class * {
        public <init>(android.content.Context, android.util.AttributeSet);
        public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 避免混淆枚举类
-keepclassmembers enum * {
        public static **[] values();
        public static ** valueOf(java.lang.String);
}

# 避免混淆序列化类-不混淆Parcelable和它的实现子类，还有Creator成员变量
-keep class * implements android.os.Parcelable {
        public static final android.os.Parcelable$Creator *;
}

# 不混淆Serializable和它的实现子类、其成员变量
-keep public class * implements java.io.Serializable {*;}
-keepclassmembers class * implements java.io.Serializable {
        static final long serialVersionUID;
        private static final java.io.ObjectStreamField[] serialPersistentFields;
        private void writeObject(java.io.ObjectOutputStream);
        private void readObject(java.io.ObjectInputStream);
        java.lang.Object writeReplace();
        java.lang.Object readResolve();
}

# 资源ID不被混淆
-keep class **.R$* {*;}

# 回调函数事件不能混淆
-keepclassmembers class * {
        void *(**On*Event);
        void *(**On*Listener);
}

# 使用GSON、fastjson等框架时，所写的JSON对象类不混淆，否则无法将JSON解析成对应的对象
-keepclassmembers class * {
         public <init>(org.json.JSONObject);
}

#不混淆泛型
-keepattributes Signature

#避免混淆注解类
-dontwarn android.annotation
-keepattributes *Annotation*

#避免混淆内部类
-keepattributes InnerClasses

#（可选）避免Log打印输出
-assumenosideeffects class android.util.Log {
        public static *** v(...);
        public static *** d(...);
        public static *** i(...);
        public static *** w(...);
        public static *** e(...);
}

# Java Bean类处理
-keep public class com.freetech.vpn.data.** {
    public void set*(***);
    public *** get*();
}
-keep class com.freetech.vpn.logic.** { *; }