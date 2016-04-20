# ScrollRulerView
 android 滑动的尺子控件 gif图暂时不会搞，，先凑合着看静态的，如下：
 ![](https://github.com/messnoTrace/ScrollRulerView/raw/master/ScrollRulerView/screenshots/pic1.png)
  ![](https://github.com/messnoTrace/ScrollRulerView/raw/master/ScrollRulerView/screenshots/pic2.png)
  
  用法如下     
  
project的gradle    

            maven {
                url 'https://dl.bintray.com/messnotrace/maven'
             }
             
 然后项目的gradle   
     compile 'com.notrace.scrollrulerview:scrollrulerview:1.0.0'     
     
    
    
    
    
 布局文件    
 
 
 
     <com.notrace.scrollrulerview.ScrollRulerView
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/tuneWheelView"
        android:layout_width="match_parent"
        android:layout_height="60dp"
        android:layout_marginTop="16dp"
        app:srDivider="6.5dp"
        app:srMaxHeightLine="15dp"
        app:srMaxValue="5000000"
        app:srMinHeightLine="8dp"
        app:srMinModDivider="100"
        app:srTextColor="#4A4A4A"
        app:srTextColorHint="#9B9B9B"
        app:srTextSize="11sp"
        app:srType="THOU"
        app:srValue="0"
        />
        
        
 属性解释    
  srDivider:刻度之间的间距    
  srMaxHeight长刻度高度
  srMaxValue尺子最大刻度
  srMinModDivider刻度分值
  srMinHeightLine短刻度高度
  
        
 在activity或者fragment中实现onValuechangeListener实现数据的监听，     
 
 过几天再 研究如何弄到jcenter上，，先凑合着看，，
