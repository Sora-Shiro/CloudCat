package com.sorashiro.cloudcat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.sorashiro.cloudcat.data.UserData;
import com.sorashiro.cloudcat.fragment.FragmentHome;
import com.sorashiro.cloudcat.fragment.FragmentMe;
import com.sorashiro.cloudcat.fragment.FragmentNotification;
import com.sorashiro.cloudcat.pager.adapter.MainPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager            viewPager;
    private MenuItem             menuItem;
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(UserData.getCurrentUsername().equals("")) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.navigation_notifications:
                        viewPager.setCurrentItem(1);
                        return true;
                    case R.id.navigation_me:
                        viewPager.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        });

        navigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                MainPagerAdapter mainPagerAdapter = (MainPagerAdapter) viewPager.getAdapter();
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        FragmentHome fragmentHome = (FragmentHome) mainPagerAdapter.getItem(0);
                        fragmentHome.getDataFromServer(true);
                        break;
                    case R.id.navigation_notifications:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.navigation_me:
                        FragmentMe fragmentMe = (FragmentMe) mainPagerAdapter.getItem(2);
                        fragmentMe.getDataFromServer(true);
                        break;
                }
            }
        });
        // 默认 >3 的选中效果会影响ViewPager的滑动切换时的效果，故利用反射去掉
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            BottomNavigationViewHelper.disableShiftMode(navigation);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                menuItem = navigation.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new FragmentHome());
        fragments.add(new FragmentNotification());
        fragments.add(new FragmentMe());
        MainPagerAdapter adapter = new MainPagerAdapter(
                getSupportFragmentManager(), fragments);

        viewPager.setAdapter(adapter);
    }

}
