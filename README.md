# Ruler
尺子选择器：弧度尺和横向直尺


[![](https://jitpack.io/v/WXFY/Ruler.svg)](https://jitpack.io/#WXFY/Ruler)

<img src="https://s27.aconvert.com/convert/p3r68-cdx67/rsqfc-3t7jy.gif" width="320" height="600" alt="图例"/>



**XMl文件**
```
    <com.zyf.ruler.rulerlibrary.WeightChoiceView
        android:id="@+id/height"
        app:arc="false"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:max="300"
        app:min="100"
        app:mSelectorValue="250"/>
```

**代码文件**
```
  WeightChoiceView weight = findViewById(R.id.weight);
  weightValue = findViewById(R.id.weight_value);
  weight.setListener(new WeightChoiceView.OnValueChangeListener() {
      @Override
      public void onValueChange(float value) {
          weightValue.setText("体重："+value+"Kg");
      }
  });
```

也可以在代码中设置最大值，最小值和选中的值

```
  WeightChoiceView weight = findViewById(R.id.weight);
  weight.setValue(250,300,100,1.0f);
```

最后面的数据是刻度间隔1.0表示间隔1,0.1表示间隔为0.1 


| 名称        | 值   | 
| :--------:   | :-----:  | 
| 刻度颜色 | mBgPaintColor | 
| 默认字体颜色 | mTextPaintColor | 
| 默认字体大小 | mTextSize | 
| 选中字体大小 | mTextSelectSize | 
| 选中字体颜色 | mTextSelectColor | 
| 最小值 | min | 
| 最大值 | max | 
| 刻度间距 | spacing | 
| 是否弧度尺 | arc |  
| 选中刻度 | mSelectorValue |  

选中刻度只能在最大值和最小值之间，否则刻度不绘制。


**因为自己项目用到的是弧度尺，看到[RuleView](https://github.com/panacena/RuleView)实现直尺思路**
