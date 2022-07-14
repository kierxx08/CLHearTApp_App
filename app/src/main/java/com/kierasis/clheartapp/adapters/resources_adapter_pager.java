package com.kierasis.clheartapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.kierasis.clheartapp.fragments.fragment_resources_information;
import com.kierasis.clheartapp.fragments.fragment_resources_about;
import com.kierasis.clheartapp.fragments.fragment_resources_map;

public class resources_adapter_pager extends FragmentStateAdapter {

    private String res_id, res_name, res_desc, res_lat, res_long, res_ratingCount, res_rating;
    private Boolean rateOn;

    public resources_adapter_pager(@NonNull FragmentActivity fragmentActivity, String res_id, String res_name, String desc, String res_lat, String res_long, String res_ratingCount, String res_rating, Boolean rateOn) {
        super(fragmentActivity);
        this.res_id = res_id;
        this.res_name = res_name;
        this.res_desc = desc;
        this.res_lat = res_lat;
        this.res_long = res_long;
        this.res_ratingCount = res_ratingCount;
        this.res_rating = res_rating;
        this.rateOn = rateOn;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return fragment_resources_information.newInstance(res_id ,res_name,res_desc,res_ratingCount,res_rating,rateOn);
            //case 1:
            //    return new fragment_resources_about();
            default:
                return fragment_resources_map.newInstance(res_name, res_lat, res_long);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
