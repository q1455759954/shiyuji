//package com.example.administrator.shiyuji.ui.fragment.mainFragment.life;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v4.view.ViewPager;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.Toast;
//
//
//import com.example.administrator.shiyuji.R;
//import com.example.administrator.shiyuji.ui.widget.LoopViewPager;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
///**
// * Created by Administrator on 2019/10/12.
// */
//
//public class life_commodity_detail_info extends AppCompatActivity implements LoopViewPager.OnViewPagerTouchListener, ViewPager.OnPageChangeListener {
//    private boolean isShoucang = false;
//    private boolean mIsTouch = false;//触摸判断
//    private Handler mHandler;
//    private LoopViewPager mLoopPager;
//    private LooperPagerAdapter mLooperPagerAdapter;
//    private LinearLayout mPointContainer;
//    private static List<Integer> sPics = new ArrayList<>();
//    static {
//        sPics.add(R.drawable.draw2);
//        sPics.add(R.drawable.draw3);
//        sPics.add(R.drawable.draw4);
//    }
//   @Override
//    protected void onCreate(Bundle savedInstanceState){
//       super.onCreate(savedInstanceState);
//       setContentView(R.layout.life_commodity_detail_info);
//       initBt();
//       initView();
//
//       //准备数据
////       Random random = new Random();
////       for(int i=0;i<5;i++){
////           sColors.add(Color.argb(random.nextInt(255),random.nextInt(255),random.nextInt(255),random.nextInt(255)));
////       }
//       //给适配器设置数据
////       mlooperPagerAdapter.setData(sColors);
//       //
////       mlooperPagerAdapter.notifyDataSetChanged();
//       mHandler= new Handler();
//   }
//    public void initBt(){
//        ((ImageView)findViewById(R.id.detailcommodity_shoucang)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!isShoucang){
//
//                    ((ImageView)findViewById(R.id.detailcommodity_shoucang)).setImageDrawable(getResources().getDrawable(R.drawable.yishoucang));
//                }else {
//                    ((ImageView)findViewById(R.id.detailcommodity_shoucang)).setImageDrawable(getResources().getDrawable(R.drawable.shoucang));
//                }
//                isShoucang = !isShoucang;
//            }
//        });
//    }
//    @Override
//    public void onAttachedToWindow(){
//        super.onAttachedToWindow();
//        //绑定到窗口时
//        mHandler.post(mLooperTask);
//    }
//    @Override
//    public void onDetachedFromWindow(){
//        super.onDetachedFromWindow();
//        mHandler.removeCallbacks(mLooperTask);
//    }
//    private Runnable mLooperTask = new Runnable() {
//        @Override
//        public void run() {
//            if(!mIsTouch){
//                //切换viewPager到下一个
//                int currentItem = mLoopPager.getCurrentItem();
//                mLoopPager.setCurrentItem(++currentItem,true);
//            }
//            mHandler.postDelayed(this,3000);
//        }
//    };
//    private void initView(){
//        mLoopPager = (LoopViewPager) this.findViewById(R.id.looper_pager);
//        //为什么设置适配器
//        mLooperPagerAdapter = new LooperPagerAdapter();
//        mLooperPagerAdapter.setData(sPics);
//        mLoopPager.setAdapter(mLooperPagerAdapter);
//        mLoopPager.setOnViewPagerTouchListener(this);
//        mLoopPager.addOnPageChangeListener(this);
//        mPointContainer = (LinearLayout) this.findViewById(R.id.looper_point);
//        //加点
//        insertPoint();
//        mLoopPager.setCurrentItem(mLooperPagerAdapter.getDataRealSize()*100,false);
//    }
//    private void insertPoint(){
//        for (int i=0;i<sPics.size();i++){
//            View point = new View(this);
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(40,40);
//            layoutParams.leftMargin = 20;
//            point.setBackground(getResources().getDrawable(R.drawable.shape_looper_normal_point));
//            point.setLayoutParams(layoutParams);
//            mPointContainer.addView(point);
//        }
//    }
//    @Override
//    public void onPagerTouch(boolean isTouch) {
//        mIsTouch = isTouch;
//    }
//
//    @Override
//    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//    }
//
//    @Override
//    public void onPageSelected(int position) {
//        //viewPager停下来以后选中的位置，要改变他到真证的位置
//        int realPosition;
//        if(mLooperPagerAdapter.getDataRealSize()!=0){
//            realPosition = position % mLooperPagerAdapter.getDataRealSize();
//        }else {
//            realPosition = 0;
//        }
//        setSelectPoint(realPosition);
//    }
//
//    private void setSelectPoint(int realPosition) {
//        for(int i=0;i<mPointContainer.getChildCount();i++){
//            View point = mPointContainer.getChildAt(i);
//            if(i!=realPosition){
//                point.setBackgroundResource(R.drawable.shape_looper_normal_point);
//            }else {
//                point.setBackgroundResource(R.drawable.shape_looper_selected_point);
//            }
//        }
//    }
//
//    @Override
//    public void onPageScrollStateChanged(int state) {
//
//    }
//}
