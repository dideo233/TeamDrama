package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.fragment.KbsFragment;
import com.example.myapplication.fragment.MainFragment;
import com.example.myapplication.fragment.MbcFragment;
import com.example.myapplication.fragment.MbceveryFragment;
import com.example.myapplication.fragment.SbsFragment;
import com.example.myapplication.fragment.TvnFragment;

public class VPAdapter extends FragmentStateAdapter {

//    public VPAdapter(@NonNull FragmentActivity fragmentActivity) {
//        super(fragmentActivity);
//    }

    public VPAdapter(MainFragment mainFragment) {
        super(mainFragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                KbsFragment kbsFragment = new KbsFragment();
                return kbsFragment;
            case 1:
                MbcFragment mbcFragment = new MbcFragment();
                return mbcFragment;
            case 2:
                SbsFragment sbsFragment = new SbsFragment();
                return sbsFragment;
            case 3:
                TvnFragment tvnFragment = new TvnFragment();
                return tvnFragment;
            case 4:
                MbceveryFragment mbceveryFragment = new MbceveryFragment();
                return mbceveryFragment;
            default:return null;
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
