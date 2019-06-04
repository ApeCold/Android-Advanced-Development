package com.netease.paint.colorfilter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ColorFilterActivity extends AppCompatActivity {

    RecyclerView       recyclerView;
    ColorFilterAdapter mColorFilterAdapter;
    private List<float[]> filters = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        inItFilters();
        recyclerView = findViewById(R.id.recyclerView);
        mColorFilterAdapter = new ColorFilterAdapter(getLayoutInflater(), filters);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mColorFilterAdapter);
    }

    private void inItFilters() {
        filters.add(ColorFilter.colormatrix_heibai);
        filters.add(ColorFilter.colormatrix_fugu);
        filters.add(ColorFilter.colormatrix_gete);
        filters.add(ColorFilter.colormatrix_chuan_tong);
        filters.add(ColorFilter.colormatrix_danya);
        filters.add(ColorFilter.colormatrix_guangyun);
        filters.add(ColorFilter.colormatrix_fanse);
        filters.add(ColorFilter.colormatrix_hepian);
        filters.add(ColorFilter.colormatrix_huajiu);
        filters.add(ColorFilter.colormatrix_jiao_pian);
        filters.add(ColorFilter.colormatrix_landiao);
        filters.add(ColorFilter.colormatrix_langman);
        filters.add(ColorFilter.colormatrix_ruise);
        filters.add(ColorFilter.colormatrix_menghuan);
        filters.add(ColorFilter.colormatrix_qingning);
        filters.add(ColorFilter.colormatrix_yese);
    }

}
