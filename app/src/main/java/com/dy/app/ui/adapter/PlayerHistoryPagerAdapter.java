package com.dy.app.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.dy.app.ui.dialog.PlayerHistoryDialog;
import com.dy.app.ui.view.FragmentAllGamePlayHistory;
import com.dy.app.ui.view.FragmentPvEHistory;
import com.dy.app.ui.view.FragmentPvPHistory;

public class PlayerHistoryPagerAdapter extends FragmentStateAdapter {
    private final PlayerHistoryDialog playerHistoryDialog;

    public PlayerHistoryPagerAdapter(@NonNull FragmentActivity fragmentActivity, PlayerHistoryDialog playerHistoryDialog) {
        super(fragmentActivity);
        this.playerHistoryDialog = playerHistoryDialog;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return playerHistoryDialog.getFragmentAllGamePlayHistory();
            case 1:
                return playerHistoryDialog.getFragmentPvPHistory();
            case 2:
                return playerHistoryDialog.getFragmentPvEHistory();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return playerHistoryDialog.getPagerTabs().length;
    }
}
