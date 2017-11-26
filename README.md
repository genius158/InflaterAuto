# InflaterAuto
一个小而精致的UI适配库

## 说明
本库由LayoutInflater入手，更改获取布局解析服务的方法，返回我们自己的布局解析器，在创建View的完成时，就对View的LayoutParams进行调整，来做适配，这个步骤是在
View开始测量绘制之前，不会造成二次绘制，性能上除了View创建完成时对其递归调整LayoutParams之外，是没有
任何影响的。

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
可以看到，LayoutParams是在这里创建的，这个方法是我们最需要更改操作的，然而我们并不能覆写这个方法，想要操作到这行代码，需要我们完全重写LayoutInflater，
，一些内部方法，我们并不能使用，同时在Android自身的升级过程中，这个类的各种更改，难以把控。最终还是选择在inflate返回View以后直接对View做调整，来实现适配。
## 使用
```
// application 初始化
public class InflaterAutoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        InflaterAuto.init(new InflaterAuto.Builder()
                .width(720)
                .height(800)
                .addException(AppBarLayout.class)//add do not need adjust view type
                .build()
        );
    }

 
    @Override
    protected void attachBaseContext(Context base) {
        //替换Infater
        super.attachBaseContext(InflaterAuto.wrap(base));
    }
}

// activity 重写attachBaseContext
public class MainActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context base) {
        //替换Infater
        super.attachBaseContext(InflaterAuto.wrap(base));
    }
}
```
