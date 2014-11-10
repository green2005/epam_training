package com.samples.epamtraining.viewpagersample;

import android.support.v4.app.FragmentActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;

public class DetailActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i = getIntent();
        Pages itemType;
        Object item;
        if ((i.hasExtra("itemType"))&&(i.hasExtra("item"))){
            itemType = (Pages) i.getSerializableExtra("itemType");
            item = (Object) i.getSerializableExtra("item");
            FrameLayout frameLayout = (FrameLayout) findViewById(R.id.DetailFrame);
            Fragment fragment = DetailFragment.newInstance(itemType, item);
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.DetailFrame, fragment);
            ft.commit();
            setTitle("Detail for " + itemType.toString());
        } else
        {
            finish();
        }
    }
}
