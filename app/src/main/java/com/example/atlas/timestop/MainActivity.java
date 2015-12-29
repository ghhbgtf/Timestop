package com.example.atlas.timestop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    ActionBar actionBar;
    DrawerLayout drawerLayout;
    RelativeLayout layout_top;
    LinearLayout layout_left;
    LinearLayout layout_right;
    LinearLayout layout_bottom;
    Button btnSubmit;
    ListView listViewLeft;
    ListView listViewRight;
    ActionBarDrawerToggle toggle;
    String title = "魔方计时器";
    TextView tv_show;
    Timer timer;

    int time = 0;
    boolean up = false;
    boolean flag_left;
    boolean flag_right;
    ArrayList<String> arrayListLeft;

    public String translate(int i) {
        int sec = i / 100;
        int msec = i % 100;
        return "" + sec / 10 + sec % 10 + ":" + msec / 10 + msec % 10;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123 && up) {
                time++;
                tv_show.setText(translate(time));
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.home);
        layout_top = (RelativeLayout) findViewById(R.id.layout_top);
        layout_left = (LinearLayout) findViewById(R.id.layout_left);
        layout_right = (LinearLayout) findViewById(R.id.layout_right);
        layout_bottom = (LinearLayout) findViewById(R.id.layout_bottom);
        tv_show = (TextView) findViewById(R.id.timer);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setEnabled(false);
        listViewLeft = (ListView) findViewById(R.id.leftMenu);
        listViewRight = (ListView) findViewById(R.id.rightMenu);
        arrayListLeft = new ArrayList<String>();

        actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setIcon(R.drawable.icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.argb(255, 70, 128, 22)));

        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (view == layout_left) {
                        flag_left = true;
                        if (flag_right) {
                            System.out.println("___________yes");
                            tv_show.setTextColor(Color.YELLOW);
                            up = false;
                        }
                    }
                    if (view == layout_right) {
                        flag_right = true;
                        if (flag_left) {
                            System.out.println("___________yes");
                            tv_show.setTextColor(Color.YELLOW);
                            up = false;
                        }
                    }
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (flag_left && flag_right) {
                        System.out.println("___________up");
                        tv_show.setTextColor(Color.CYAN);
                        up = true;
                        time = 0;
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(0x123);
                            }
                        }, 0, 10);
                        layout_left.setVisibility(View.INVISIBLE);
                        layout_right.setVisibility(View.INVISIBLE);
                    }
                    flag_left = false;
                    flag_right = false;
                }
                return true;
            }
        };

        layout_left.setOnTouchListener(listener);
        layout_right.setOnTouchListener(listener);
        layout_bottom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                System.out.println(motionEvent.getPointerCount() + "points been touched");
                if (motionEvent.getPointerCount() == 2) {
                    timer.cancel();
                    btnSubmit.setEnabled(true);
                    layout_left.setVisibility(View.VISIBLE);
                    layout_right.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
        layout_bottom.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                tv_show.setTextColor(Color.GREEN);
                return true;
            }
        });

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (drawerView == listViewLeft) {
                    actionBar.setTitle("成绩列表");
                } else if (drawerView == listViewRight) {
                    actionBar.setTitle("技术分析");
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                actionBar.setTitle(title);
            }
        };

        drawerLayout.setDrawerListener(toggle);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSubmit.setEnabled(false);
                arrayListLeft.add(translate(time));
                ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, arrayListLeft);
                listViewLeft.setAdapter(adapter);
            }
        });

        listViewLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("是否要删除：\n" + arrayListLeft.get(position).toString())
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                arrayListLeft.remove(position);
                                drawerLayout.closeDrawer(listViewLeft);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                builder.create()
                        .show();


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
