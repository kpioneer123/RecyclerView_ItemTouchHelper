package com.haocai.itemtouchhelper;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import utils.MyApplication;


/**
 * Created by Xionghu on 2018/4/8.
 * Desc:
 */

public class MyItemTouchHelperCallback2 extends ItemTouchHelper.Callback {

    private ItemTouchMoveListener moveListener;

    public MyItemTouchHelperCallback2(ItemTouchMoveListener moveListener) {
        this.moveListener = moveListener;
    }

//    /**
//     * 设置滑动类型标记
//     *
//     * @param recyclerView
//     * @param viewHolder
//     * @return
//     *          返回一个整数类型的标识，用于判断Item那种移动行为是允许的
//     */
//    @Override
//    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        //START  右向左 END左向右 LEFT  向左 RIGHT向右  UP向上
//        //如果某个值传0，表示不触发该操作
//        return makeMovementFlags(ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.END );
//    }

    /**
     * Callback回调监听时先调用的，用来判断当前是什么动作，比如判断方向
     * 作用：哪个方向的拖动
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //方向：up，down，left，right
        //常量
        // ItemTouchHelper.UP    0x0001
        // ItemTouchHelper.DOWN  0x0010
        // ItemTouchHelper.LEFT
        // ItemTouchHelper.RIGHT

        //我要监听的拖拽方向是哪个方向
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        //我要监听的swipe侧滑方向是哪个方向
        int swipeFlags = ItemTouchHelper.RIGHT ;


        int flags = makeMovementFlags(dragFlags, swipeFlags);
        return flags;
    }


    /**
     * 是否打开长按拖拽效果
     *
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
    /**
     * Item是否支持滑动
     *
     * @return
     *          true  支持滑动操作
     *          false 不支持滑动操作
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
    //当上下移动的时候回调的方法
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
        // 在拖拽过程中不断地调用adapter.notifyItemMoved(from,to);
        if (srcHolder.getItemViewType() != targetHolder.getItemViewType()) {
            return false;
        }
        //在拖拽的过程中不断调用adapter.notifyItemMoved(from,to);
        boolean result = moveListener.onItemMove(srcHolder.getAdapterPosition(), targetHolder.getAdapterPosition());
        return result;
    }

    //侧滑的时候回调的方法
    @Override
    public void onSwiped(RecyclerView.ViewHolder holder, int direction) {
        //监听侧滑，1.删除数据 2.调用adapter.notifyItemRemove(position);
        moveListener.onItemRemove(holder.getAdapterPosition());

    }

    //设置滑动item的背景
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        //判断选中状态
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setBackgroundColor(viewHolder.itemView.getContext().getResources().getColor(R.color.colorC));
        }
        super.onSelectedChanged(viewHolder, actionState);

    }

    //清除滑动item的背景
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // 恢复
        viewHolder.itemView.setBackgroundColor(Color.WHITE);

        //防止出现复用问题 而导致条目不显示 方式一
        viewHolder.itemView.setAlpha(1);//1-0
        //设置滑出大小
//            viewHolder.itemView.setScaleX(1);
//            viewHolder.itemView.setScaleY(1);
        super.clearView(recyclerView, viewHolder);
    }

    //设置滑动时item的背景透明度
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //滑动时自己实现背景及图片
        if (actionState==ItemTouchHelper.ACTION_STATE_SWIPE){

            //dX大于0时向右滑动，小于0向左滑动
            View itemView=viewHolder.itemView;//获取滑动的view
            Resources resources= MyApplication.getAppContext().getResources();
            Bitmap bitmap= BitmapFactory.decodeResource(resources, R.drawable.delete);//获取删除指示的背景图片
            int padding =10;//图片绘制的padding
            int maxDrawWidth=2*padding+bitmap.getWidth();//最大的绘制宽度
            Paint paint=new Paint();
            paint.setColor(resources.getColor(R.color.btninvalid));
            int x=Math.round(Math.abs(dX));
            int drawWidth=Math.min(x,maxDrawWidth);//实际的绘制宽度，取实时滑动距离x和最大绘制距离maxDrawWidth最小值
            int itemTop=itemView.getBottom()-itemView.getHeight();//绘制的top位置
            //向右滑动
            if(dX>0){
                //根据滑动实时绘制一个背景
                c.drawRect(itemView.getLeft(),itemTop,drawWidth,itemView.getBottom(),paint);
                //在背景上面绘制图片
                if (x>padding){//滑动距离大于padding时开始绘制图片
                    //指定图片绘制的位置
                    Rect rect=new Rect();//画图的位置
                    rect.left=itemView.getLeft()+padding;
                    rect.top=itemTop+(itemView.getBottom()-itemTop-bitmap.getHeight())/2;//图片居中
                    int maxRight=rect.left+bitmap.getWidth();
                    rect.right=Math.min(x,maxRight);
                    rect.bottom=rect.top+bitmap.getHeight();
                    //指定图片的绘制区域
                    Rect rect1=null;
                    if (x<maxRight){
                        rect1=new Rect();//不能再外面初始化，否则dx大于画图区域时，删除图片不显示
                        rect1.left=0;
                        rect1.top = 0;
                        rect1.bottom=bitmap.getHeight();
                        rect1.right=x-padding;
                    }
                    c.drawBitmap(bitmap,rect1,rect,paint);
                }

                float alpha = 1.0f - Math.abs(dX) / (float) itemView.getWidth();
                itemView.setAlpha(alpha);
                //绘制时需调用平移动画，否则滑动看不到反馈
                itemView.setTranslationX(dX);
            }else {
                //如果在getMovementFlags指定了向左滑动（ItemTouchHelper。START）时则绘制工作可参考向右的滑动绘制，也可直接使用下面语句交友系统自己处理
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }else {
            //拖动时有系统自己完成
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
