package com.dy.startinganimation.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.dy.startinganimation.common.MenuListAdapter;
import com.dy.startinganimation.utils.Loader;
import com.example.startinganimation.R;

public class MainActivity extends Activity
implements AdapterView.OnItemClickListener {

    private final String[] menuItems = {"Demo", "Load Obj", "Load DAE", "Snake", "Quit"};
    private View menuScreen;
    ListView menuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        menuScreen = findViewById(R.id.menuScreen);
        menuScreen.setBackground(Loader.getInstance().loadImage(getResources().openRawResource(R.raw.menu_background)));

        menuList = findViewById(R.id.menuList);
        menuList.setAdapter(new MenuListAdapter(this, menuItems));

        menuList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final String item = menuItems[i];

        switch (item){
            case "Demo":
                startDemo();
                break;
            case "Load Obj":
                break;
            case "Load DAE":
                break;
            case "Snake":
                break;
            case "Quit":
                finish();
                break;
        }
    }

    private void startDemo(){
        Intent intent = new Intent(this, DemoActivity.class);
        Bundle b = new Bundle();
        intent.putExtras(b);
        startActivity(intent);
    }
}