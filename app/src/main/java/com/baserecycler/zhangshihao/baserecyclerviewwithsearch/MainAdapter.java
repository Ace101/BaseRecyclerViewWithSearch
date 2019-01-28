package com.baserecycler.zhangshihao.baserecyclerviewwithsearch;

import android.support.annotation.Nullable;

import com.baserecycler.zhangshihao.baserecyclerviewwithsearch.utils.PinYinComparator;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class MainAdapter extends BaseQuickAdapter<PinYinComparator.CityBean, BaseViewHolder> {

    private List<PinYinComparator.CityBean> mData;


    public MainAdapter(int layoutResId, @Nullable List<PinYinComparator.CityBean> data) {
        super(layoutResId, data);
        this.mData = data;
    }

    @Override
    protected void convert(BaseViewHolder helper, PinYinComparator.CityBean item) {
        helper.setText(R.id.tv_city, item.getName());
        char selection = item.getSortLetter().charAt(0);
        int letterPosition = getPositionForSelection(selection);

        if (letterPosition == mData.indexOf(item)) {
            helper.setText(R.id.tv_letter, item.getSortLetter());
            helper.setGone(R.id.tv_letter, true);
            helper.itemView.setTag(1);
        } else {
            helper.setGone(R.id.tv_letter, false);
            helper.itemView.setTag(2);
        }

        helper.itemView.setContentDescription(item.getSortLetter());
    }

    /**
     * 判断是否为首字母
     *
     * @param selection
     * @return 返回字母第一次出现的位置
     */
    private int getPositionForSelection(int selection) {
        for (int i = 0; i < mData.size(); i++) {
            char firstLetter = mData.get(i).getSortLetter().toUpperCase().charAt(0);
            if (selection == firstLetter) {
                return i;
            }
        }
        return -1;
    }
}
