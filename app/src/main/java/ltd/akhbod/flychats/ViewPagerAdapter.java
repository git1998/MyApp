package ltd.akhbod.flychats;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Switch;

/**
 * Created by ibm on 26-01-2018.
 */

class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {


        switch (position)
        {
            case 0: return new RequestFragment();
            case 1: return new ChatsFragment();
            case 2: return new FriendsFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position)
        {
            case 0: return "REQUEST";
            case 1:return  "CHATS";
            case 2: return  "FRIENDS";
        }
        return null;
    }
}
