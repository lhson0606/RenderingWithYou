package com.dy.startinganimation.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.dy.startinganimation.common.DemoDAEListAdapter;
import com.dy.startinganimation.common.DemoData;
import com.example.startinganimation.R;

public class DemoActivity extends Activity implements
        View.OnClickListener,
        AdapterView.OnItemClickListener {
    Button btnClose;
    GridView gvDemos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_showcase);
        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);
        gvDemos = findViewById(R.id.gvDAE);
        gvDemos.setOnItemClickListener(this);
        DemoDAEListAdapter daeListAdapter = new DemoDAEListAdapter(this, DemoData.getInstance().dae_demo_list);
        gvDemos.setAdapter(daeListAdapter);
        gvDemos.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnClose){
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(DemoActivity.this, DemoGLActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("dae", DemoData.getInstance().dae_demo_list.get(i));
        intent.putExtras(b);
        startActivity(intent);
    }
}
