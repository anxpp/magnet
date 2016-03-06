package com.anxpp.magnet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.anxpp.magnet.Beans.Item;
import com.anxpp.magnet.Beans.MagnetInfo;

public class MainActivity extends AppCompatActivity {
    private Button searchBtn;
    private RadioGroup btGroup;
    private RadioButton selectedRadio;
    private EditText searchEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
        searchBtnMethod();

    }

    private void searchBtnMethod() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class jumpClass=SsbcActivity.class;
                if (searchEt.getText().length() <= 0) {
                    Toast.makeText(MainActivity.this, "请输入关键字", Toast.LENGTH_SHORT).show();
                } else {
                    switch (selectedRadio.getId())
                    {
                        case R.id.radio_btn_ssbc:
                            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                            intent.putExtra("title","手撕包菜");
                            intent.putExtra("info", new String[]{"http://www.shousibaocai.cc/search/"+searchEt.getText().toString()
                                    ,"div.search-item>div.item-title>h3>a","div.search-item>div.item-title>h3>a"});
                            startActivity(intent);
                            return;
                        case R.id.radio_btn_bread:
                            jumpClass=BreadActivity.class;
                            break;
                        case R.id.radio_btn_btIsland:
                            intent = new Intent(MainActivity.this, ResultActivity.class);
                            intent.putExtra("title","BT岛");
                            intent.putExtra("info", new String[]{"http://www.btdao.xyz/list/"+searchEt.getText().toString()
                                    +"-s1d-1.html","li>h3.T1>a","li>h3.T1>a"});
                            startActivity(intent);
                            return;
                    }
                    Intent intent = new Intent(MainActivity.this, jumpClass);
                    intent.putExtra("search_key", searchEt.getText().toString());
                    startActivity(intent);
                }
            }
        });
        searchBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, selectedRadio.getText(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void init() {
        searchEt= (EditText) findViewById(R.id.search_et);
        searchBtn= (Button) findViewById(R.id.search_btn);
        btGroup= (RadioGroup) findViewById(R.id.btGroup);
        RadioButton bt1 = (RadioButton) findViewById(R.id.radio_btn_ssbc);
        btGroup.check(bt1.getId());
        selectedRadio= (RadioButton) findViewById(btGroup.getCheckedRadioButtonId());//设置默认id
        btGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                selectedRadio = (RadioButton) findViewById(btGroup.getCheckedRadioButtonId());
            }
        });

    }
}
