package com.baserecycler.zhangshihao.baserecyclerviewwithsearch;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.baserecycler.zhangshihao.baserecyclerviewwithsearch.utils.CityModel;
import com.baserecycler.zhangshihao.baserecyclerviewwithsearch.utils.PinYinComparator;
import com.baserecycler.zhangshihao.baserecyclerviewwithsearch.utils.ProvinceModel;
import com.baserecycler.zhangshihao.baserecyclerviewwithsearch.utils.XmlParserHandler;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rv_name)
    RecyclerView rvName;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.tv_recycler_letter)
    TextView tvRecyclerLetter;
    @BindView(R.id.tv_recycler_dialog)
    TextView tvRecyclerDialog;

    private BaseQuickAdapter mainAdapter;
    private List<PinYinComparator.CityBean> mCityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getCity();
        Collections.sort(mCityList, new PinYinComparator());

        final LinearLayoutManager layout = new LinearLayoutManager(this);
        rvName.setLayoutManager(layout);

        mainAdapter = new MainAdapter(R.layout.item_name, mCityList);
        rvName.setAdapter(mainAdapter);
        rvName.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                letterScroll();
            }
        });
        navigationView.setDialog(tvRecyclerDialog);
        navigationView.setOnTouchItemListener(new NavigationView.OnTouchItemListener() {
            @Override
            public void onTouch(String var1) {
                int position = getPositionForSelection(var1.charAt(0));
                layout.scrollToPositionWithOffset(position, 0);
            }
        });

        mainAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });

    }

    /**
     * 监听滑动，使头字母停留
     */
    private void letterScroll() {
        View letterView = rvName.findChildViewUnder(tvRecyclerLetter.getMeasuredWidth() / 2, 3);
        if (letterView != null && letterView.getContentDescription() != null) {
            tvRecyclerLetter.setText(String.valueOf(letterView.getContentDescription()));
        }

        View recyclerItem = rvName.findChildViewUnder(tvRecyclerLetter.getMeasuredWidth() / 2,
                tvRecyclerLetter.getMeasuredHeight() + 1);

        if (recyclerItem != null && recyclerItem.getTag() != null) {
            int deltaY = recyclerItem.getTop() - tvRecyclerLetter.getMeasuredHeight();

            if ((int) recyclerItem.getTag() == 1) {
                if (recyclerItem.getTop() > 0) {
                    tvRecyclerLetter.setTranslationY(deltaY);
                } else {
                    tvRecyclerLetter.setTranslationY(0);
                }
            } else if ((int) recyclerItem.getTag() == 2) {
                tvRecyclerLetter.setTranslationY(0);
            }
        }
    }

    /**
     * 判断是否为首字母
     *
     * @param selection
     * @return 返回字母第一次出现的位置
     */
    public int getPositionForSelection(int selection) {
        for (int i = 0; i < mCityList.size(); i++) {
            char firstLetter = mCityList.get(i).getSortLetter().toUpperCase().charAt(0);
            if (selection == firstLetter) {
                return i;
            }
        }
        return -1;
    }

    private void getCity() {
        List<ProvinceModel> provinceList = null;
        mCityList = new ArrayList<>();
        AssetManager asset = getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
            for (int i = 0; i < provinceList.size(); i++) {
                // 遍历所有省的数据
                List<CityModel> cityModels = provinceList.get(i).getCityList();
                for (int j = 0; j < cityModels.size(); j++) {
                    // 遍历省下面的所有市的数据
                    String name = cityModels.get(j).getName();
                    String pinYin = PinYinComparator.getPinYin(name);
                    if (pinYin.length() > 0) {
                        String sortLetter = pinYin.substring(0, 1).toUpperCase();
                        PinYinComparator.CityBean cityBean = new PinYinComparator.CityBean();
                        cityBean.setName(name);
                        cityBean.setSortLetter(sortLetter);
                        mCityList.add(cityBean);
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
    }
}
