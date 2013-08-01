package com.wehack.syncedQ;

import com.soundcloud.android.service.LocalBinder;
import com.soundcloud.android.service.playback.CloudPlaybackService;
import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class LLFragment extends ListFragment {
    SlideExpandableListAdapter mSlideExpandableListAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSlideExpandableListAdapter = new SlideExpandableListAdapter(
                getLLQueue(),
                R.id.progress_overlay,
                R.id.waveform_controller
        );
        setListAdapter(mSlideExpandableListAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().bindService(new Intent(getActivity(), CloudPlaybackService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unbindService(mServiceConnection);
    }

    private LLQueue getLLQueue() {
        return LLQueue.get();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mSlideExpandableListAdapter.toggleExpanded(v, position);
        getLLQueue().play(position);
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName classname, IBinder obj) {
            if (obj instanceof LocalBinder) {
                getLLQueue().setPlaybackService((CloudPlaybackService) ((LocalBinder) obj).getService());
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName classname) {
            getLLQueue().setPlaybackService(null);
        }
    };

}
