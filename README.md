# InflaterAuto
一个够小够简单的UI适配库（仅仅 四个class加一个内部类还有一个enum）

#### 已下设计图纸为720_1280(图例分辨率分别为:1080_1920、480_800、1920_1080)
 布局中不属于ViewGroup的布局设置都是采用layout_width="px",android:layout_height="px",android:layout_marginTop="px",android:paddingLeft="px"具体px值设置
<br/>
![screen1080_1920](art/screen1080_1920.jpg)
![screen480_800](art/screen480_800.jpg)
<br/>
![screen1920_1080](art/screen1920_1080.jpg)


## 概述
本库由LayoutInflater入手，更改获取布局解析服务的方法，返回我们自己的布局解析器，在创建View的完成时
，就对View（包括子View，如果有）的LayoutParams进行调整，来做适配，这个步骤是在View开始测量绘制之前，不会造成二次
绘制，性能上除了View创建完成时对其递归调整LayoutParams之外，是没有任何影响的。

#### 选择切入点
```
view的设置LayoutParams是在LayoutInflater的rInflate方法中执行的
void rInflate(XmlPullParser parser, View parent, Context context,
           AttributeSet attrs, boolean finishInflate) throws XmlPullParserException, IOException {
    ...
    final View view = createViewFromTag(parent, name, context, attrs);
    final ViewGroup viewGroup = (ViewGroup) parent;
    final ViewGroup.LayoutParams params = viewGroup.generateLayoutParams(attrs);
    rInflateChildren(parser, view, attrs, true);//这里是归调继续创建View
    viewGroup.addView(view, params);
    ...
    }
```
可以看到，LayoutParams是在这里创建的，这个方法是我们最需要更改操作的，然而我们并不能覆写这个方法，AndroidAutoLayout有一系列的Auto开头的ViewGroup
，其重写的也就是generateLayoutParams，直接返回调整过的params，然而它仍然需要在OnMeasure的时候对所有子View内部相关属性做调整，
如果想在rInflate方法里，在创建完View后直接做调整，需要我们完全重写LayoutInflater，然而一些内部方法，我们并不能使用，同时在
Android自身的升级过程中，这个类的各种更改，难以把控。最终还是选择在inflate返回View以后直接对View做调整，来实现适配。

## gradle
compile 'com.yan:inflaterauto:1.0.1'

## 使用
```
// application 初始化
public class InflaterAutoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        InflaterAuto.init(new InflaterAuto.Builder(this)
            .width(720)
            .height(1280)
            .baseOnDirection(InflaterAuto.BaseOn.Both)// 宽度根据宽度比例缩放，长度根据长度比例缩放
            .addException(AppBarLayout.class)//add do not need adjust view type
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //如果app支持旋转，请加上supportScreenRotation方法，且在布局设置之前调用
        //同时保证界面正常销毁重新加载
        InflaterAuto.getInstance().supportScreenRotation(savedInstanceState, this);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void attachBaseContext(Context base) {
        //替换Inflater
        super.attachBaseContext(InflaterAuto.wrap(base));
    }
}
```
