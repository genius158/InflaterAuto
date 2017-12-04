# InflaterAuto
强大的UI适配库(AndroidAutoLayout替代方案),不只是适配！
<br/>
甚至可进行统一的类替换(把所有的TextView替换成ImageView)
#### 图例
以下设计图纸为720_1280(图例分辨率分别为:1080_1920、480_800、1920_1080)，布局中不属于ViewGroup的布局设置都是
采用layout_width="px",android:layout_height="px",android:layout_marginTop="px",android:paddingLeft="px"具体px值设置
<br/>
(ps:只适配px)
<br/>
<br/>
![screen1080_1920](art/screen1080_1920.jpg)
![screen480_800](art/screen480_800.jpg)
<br/>
![screen1920_1080](art/screen1920_1080.jpg)


## 概述
本库实现，在view生成时直接调整内部相关属性，宽度高度等需要父类调整的，则替换原本的viewgroup为可适配的viewgroup
<br/>
是的，本库，可以统一对你想要更改的view全部替换，完成很多其他的事情，所以这不仅仅只是一个适配库

#### 选择切入点
```
view的设置LayoutParams是在LayoutInflater的rInflate方法中执行的
void rInflate(XmlPullParser parser, View parent, Context context,
           AttributeSet attrs, boolean finishInflate) throws XmlPullParserException, IOException {
    ...
    final View view = createViewFromTag(parent, name, context, attrs);
    final ViewGroup viewGroup = (ViewGroup) parent;
    final ViewGroup.LayoutParams params = viewGroup.generateLayoutParams(attrs);
    rInflateChildren(parser, view, attrs, true);//这里是递归调继续创建View
    viewGroup.addView(view, params);
    ...
    }
```
可以看到，LayoutParams是在这里创建的，这个方法是我们最需要更改操作的，然而我们并不能覆写这个方法，AndroidAutoLayout有一系列的Auto开头的ViewGroup
，其重写的也就是generateLayoutParams，直接返回调整过的params，然而它仍然需要在OnMeasure的时候对所有子View内部相关属性做调整，为了提升效率，
2.x不在返回整个View后递归调整，而是，采用View自身的属性，在View生成后直接调整，LayoutParams在父类的生成后直接调整，可调整LayoutParams的父类配置注解，在编译时自动生成。

## gradle
implementation 'com.yan:inflaterauto:2.0.15'
<br/>
annotationProcessor 'com.yan:inflaterauto-compiler:2.0.15'//如果你不需要自动生成适配类的功能，不需要引入

## 使用
```
// application 初始化
public class InflaterAutoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /*
         * 以下可以写在任何地方，只要在生成View之前
         */
        InflaterAuto.init(new InflaterAuto.Builder()
            .width(720)
            .height(1280)
            .baseOnDirection(InflaterAuto.BaseOn.Both)// 宽度根据宽度比例缩放，长度根据长度比例缩放
            // 由 com.yan.inflaterautotest.InflaterConvert 编译生成，自动添加前缀InfAuto
            // 你也可以添加你自己的实现AutoConvert的类，替换任何一种view成为你想替换的view
            .inflaterConvert(new InfAutoInflaterConvert())
            .build()
        );
    }


    /**
     * 如果你使用了LayoutInflater.from(getApplicationContext())或者LayoutInflater.from(getApplication())
     * 就需要以下操作，如果没有，以下方法不必重写
     */
   @Override
    protected void attachBaseContext(Context base) {
        //替换Inflater
        super.attachBaseContext(InflaterAuto.wrap(base));
    }
}

// activity 重写attachBaseContext
public class MainActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context base) {
        //替换Inflater
        super.attachBaseContext(InflaterAuto.wrap(base));
    }
}

// 注解设置，add 你用到的ViewGroup
@Convert({LinearLayout.class
        , FrameLayout.class
        , NestedScrollView.class
        , RecyclerView.class
        , ListView.class
        , ScrollView.class
        , CoordinatorLayout.class
        , ConstraintLayout.class
        , AutoLayout.class
} )
public class InflaterConvert implements AutoConvert {// 类名随便写
 @Override
    public HashMap<String, String> getConvertMap() {
        return null;// 添加映射
    }
}
```
## 说明
view的适配，不包括maxHeight、maxWidth，因为其中涉及反射，影响效率，暂时不打算处理，同时用的其实也很少～
<br/>
<br/>
类型转换接口
```
public interface AutoConvert {
    HashMap<String, String> getConvertMap();
}
```
如果默认的适配效果满足不了需求，或者你想要的不只是适配功能，你可以自己实现该接口，Hashmap kay为你要替换的view在布局文件中标签的名字，value为替换后的类
<br/>
<br/>
例如 动态更新皮肤，你可以重写相关的view，并替换，给它添加一个广播监听，需要换肤的时候，发出广播，然后你重写的view接受到广播
后就可以做相关操作。 
<br/>
是的有了替换view的功能，你可以为所欲为！
<br/>
(ps:Hashmap Key是根据xml里的标签名称对应的，比如LinearLayout没有包名，support包下的是全类名)

## 鸣谢
[hongyangAndroid/AndroidAutoLayout](https://github.com/hongyangAndroid/AndroidAutoLayout)
<br/>
[chrisjenx/Calligraphy](https://github.com/chrisjenx/Calligraphy)

