package com.example.swishbddriver.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabPaggerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragmentList =new ArrayList();

    public TabPaggerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return (Fragment) fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void  addFragment(Fragment fragment){
        fragmentList.add(fragment);
    }
}
