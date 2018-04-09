package com.haocai.itemtouchhelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.blankj.utilcode.util.Utils;

import java.util.List;

import utils.DataUtils;

public class MainActivity extends AppCompatActivity  {

    private RecyclerView recyclerView;
   private ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<QQMessage> list = DataUtils.init();
        QQAdapter adapter = new QQAdapter(list);
        recyclerView.setAdapter(adapter);

//        //条目触目帮助类
//        ItemTouchHelper.Callback callback = new MyItemTouchHelperCallback(adapter);
//        itemTouchHelper = new ItemTouchHelper(callback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);

        //条目触目帮助类
        ItemTouchHelper.Callback callback = new MyItemTouchHelperCallback3(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }



}
