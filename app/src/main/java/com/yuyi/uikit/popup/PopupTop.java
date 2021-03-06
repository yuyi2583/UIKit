package com.yuyi.uikit.popup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.yuyi.uikit.R;

public class PopupTop extends PopupWindow implements PopupType{
    public static final String TOP="top";
    public static final String BOTTOM="bottom";
    public static final String LEFT="left";
    public static final String RIGHT="right";

    private static final String TRANSLATION_Y="translationY";
    private static final String TRANSLATION_X="translationX";
    private static final int APPEAR_DURATION=200;
    private static final int CLOSE_DURATION=100;
    private static final int HANDLER_CLOSE_POPUP=1;

    private TextView tvTitle;
    private ImageView ivClose;
    private RelativeLayout rlRoot;

    private int contentViewWidth;
    private int contentViewHeight;

    private Context context;
    private int gravity= Gravity.TOP;
    private boolean dismissed=false;

    private final Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what==HANDLER_CLOSE_POPUP&&!dismissed){
                dismiss();
            }
        }
    };
    private long delayMillis=2000;

    private Popup.OnClick onClickListener=null;
    private Popup.OnClose onCloseListener=null;

    PopupTop(Context context) {
        //设置view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popup_dialog_top, null);

        tvTitle=(TextView)view.findViewById(R.id.tv_title);
        ivClose=(ImageView)view.findViewById(R.id.iv_close);
        rlRoot=(RelativeLayout)view.findViewById(R.id.rl_root);

        this.context=context;

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        rlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener!=null){
                    onClickListener.onClick();
                }
            }
        });


        setContentView(view);
        initView();
        //activity的contentView的高度
        contentViewWidth = ((Activity) context).findViewById(android.R.id.content).getWidth();
        contentViewHeight = ((Activity) context).findViewById(android.R.id.content).getHeight();
        //其他设置
//        setWidth(dp2px(100));//必须设置宽度
        setWidth(contentViewWidth);
//        setHeight(height);//必须设置高度
        setHeight(dp2px(100));
        setFocusable(false);//是否获取焦点
        setOutsideTouchable(true);//是否可以通过点击屏幕外来关闭
    }

    PopupTop(Context context,String title){
        this(context);
        setTitle(title);
    }

    PopupTop(Context context,String title,@ColorInt int color){
        this(context);
        setTitle(title);
        setBackgroundColor(color);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        ObjectAnimator.ofFloat(getContentView(),TRANSLATION_Y,-getHeight(),0)
                .setDuration(APPEAR_DURATION)
                .start();
        handler.sendEmptyMessageDelayed(HANDLER_CLOSE_POPUP,delayMillis);
    }

    @Override
    public void dismiss() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(getContentView(), TRANSLATION_Y, 0, -getHeight())
                .setDuration(CLOSE_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                PopupTop.super.dismiss();
                if(onCloseListener!=null){
                    onCloseListener.onClose();
                }
                dismissed=true;
            }
        });
        animator.start();
    }

    /**
     * 获取屏幕宽高
     *
     * @param context
     * @return
     */
    private static int[] getScreenSize(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    /**
     * Value of dp to value of px.
     *
     * @param dpValue The value of dp.
     * @return value of px
     */
    public static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void initView() {

    }

    @Override
    public void error() {
        rlRoot.setBackgroundColor(context.getResources().getColor(R.color.error));
        showAtLocation(((Activity) context).getWindow().getDecorView(), gravity,0,0);
    }


    @Override
    public void success() {
        rlRoot.setBackgroundColor(context.getResources().getColor(R.color.success));
        showAtLocation(((Activity) context).getWindow().getDecorView(), gravity,0,0);
    }



    @Override
    public void warning() {
        rlRoot.setBackgroundColor(context.getResources().getColor(R.color.warning));
        showAtLocation(((Activity) context).getWindow().getDecorView(), gravity,0,0);
    }

    @Override
    public void info() {
        rlRoot.setBackgroundColor(context.getResources().getColor(R.color.info));
        showAtLocation(((Activity) context).getWindow().getDecorView(), gravity,0,0);
    }


    @Override
    public void show(int color) {
        rlRoot.setBackgroundColor(color);
        showAtLocation(((Activity) context).getWindow().getDecorView(), gravity,0,0);
    }

    @Override
    public PopupType duration(long millis) {
        delayMillis=millis;
        return this;
    }

    @Override
    public PopupType onClick(Popup.OnClick onClick) {
        this.onClickListener=onClick;
        return this;
    }

    @Override
    public PopupType onClose(Popup.OnClose onClose) {
        this.onCloseListener=onClose;
        return this;
    }

    public void setTitle(String title){
        tvTitle.setText(title);
    }


    public void setBackgroundColor(@ColorInt int color){
        rlRoot.setBackgroundColor(color);
    }


}
