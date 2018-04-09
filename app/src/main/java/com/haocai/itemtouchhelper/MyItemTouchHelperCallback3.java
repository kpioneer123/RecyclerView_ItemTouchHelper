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
import android.view.ViewGroup;
import android.widget.FrameLayout;

import utils.MyApplication;


/**
 * Created by Xionghu on 2018/4/8.
 * Desc:
 */

public class MyItemTouchHelperCallback3 extends ItemTouchHelper.Callback {

    //限制ImageView长度所能增加的最大值
    private double ICON_MAX_SIZE = 40;
    //ImageView的初始长宽
    private int fixedWidth = 120;

    private ItemTouchMoveListener moveListener;

    public MyItemTouchHelperCallback3(ItemTouchMoveListener moveListener) {
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
        int swipeFlags = ItemTouchHelper.LEFT ;


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

        QQAdapter.MyViewHolder myViewHolder = (QQAdapter.MyViewHolder)viewHolder;
        //重置改变，防止由于复用而导致的显示问题
        viewHolder.itemView.setScrollX(0);
        myViewHolder.tvDetele.setText("左滑删除");
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) myViewHolder.ivDetele.getLayoutParams();
        params.width = 150;
        params.height = 150;
        myViewHolder.ivDetele.setLayoutParams(params);
        myViewHolder.ivDetele.setVisibility(View.INVISIBLE);

        super.clearView(recyclerView, viewHolder);
    }

    //设置滑动时item的背景透明度
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        QQAdapter.MyViewHolder myViewHolder = (QQAdapter.MyViewHolder)viewHolder;
        //仅对侧滑状态下的效果做出改变
        if (actionState ==ItemTouchHelper.ACTION_STATE_SWIPE){
            Log.d("http","4444");
            //如果dX小于等于删除方块的宽度，那么我们把该方块滑出来
            if (Math.abs(dX) <= getSlideLimitation(viewHolder)){
                viewHolder.itemView.scrollTo(-(int) dX,0);
            }
            //如果dX还未达到能删除的距离，此时慢慢增加“眼睛”的大小，增加的最大值为ICON_MAX_SIZE
            else if (Math.abs(dX) <= recyclerView.getWidth() / 2){
                double distance = (recyclerView.getWidth() / 2 -getSlideLimitation(viewHolder));
                double factor = ICON_MAX_SIZE / distance;
                double diff =  (Math.abs(dX) - getSlideLimitation(viewHolder)) * factor;
                if (diff >= ICON_MAX_SIZE)
                    diff = ICON_MAX_SIZE;
                myViewHolder.tvDetele.setText("");   //把文字去掉
                myViewHolder.ivDetele.setVisibility(View.VISIBLE);  //显示眼睛
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)    myViewHolder.ivDetele.getLayoutParams();
                params.width = (int) (fixedWidth + diff);
                params.height = (int) (fixedWidth + diff);
                myViewHolder.ivDetele.setLayoutParams(params);
            }
        }else {
            //拖拽状态下不做改变，需要调用父类的方法
            super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive);
        }
    }
    /**
     * 获取删除方块的宽度
     */
    public int getSlideLimitation(RecyclerView.ViewHolder viewHolder){
        ViewGroup viewGroup = (ViewGroup) viewHolder.itemView;
        return viewGroup.getChildAt(2).getLayoutParams().width;
    }
}
