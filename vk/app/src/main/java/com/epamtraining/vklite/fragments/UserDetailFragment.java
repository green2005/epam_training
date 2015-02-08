package com.epamtraining.vklite.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.DataSource;
import com.epamtraining.vklite.ErrorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.activities.ListActivity;
import com.epamtraining.vklite.activities.MessagesActivity;
import com.epamtraining.vklite.bo.UserInfoFull;
import com.epamtraining.vklite.db.UsersDBHelper;
import com.epamtraining.vklite.loader.ImageLoader;
import com.epamtraining.vklite.processors.Processor;
import com.epamtraining.vklite.processors.UserInfoFullProcessor;

public class UserDetailFragment extends WallFragment {
    public static final String USER_ID = "userId";
    private static final String ZERO = "0";
    private UserInfoFull mUserInfo;

    private String mUserId;
    private ImageLoader mImageLoader;

    public static UserDetailFragment newInstance(Bundle bundle) {
        UserDetailFragment userDetailFragment = new UserDetailFragment();
        userDetailFragment.setArguments(bundle);
        return userDetailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mImageLoader = ImageLoader.get(getActivity());
        Bundle bundle = getArguments();
        if (bundle != null) {
            mUserId = bundle.getString(USER_ID);
            if (TextUtils.isEmpty(mUserId)) {
                throw new IllegalArgumentException("User Id cannot be null");
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public String getWallOwnerId() {
        return mUserId;
    }

    @Override
    public String getDataUrl(int offset, String next_id) {
        return Api.getWallUrl(getActivity(), String.valueOf(offset), mUserId);
    }

    @Override
    protected void onAfterCreateView(View view) {
        super.onAfterCreateView(view);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View headerView = inflater.inflate(R.layout.user_detail_header_view, null);
        loadUserInfo(headerView);
    }

    private void loadUserInfo(final View headerView) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        Processor processor = new UserInfoFullProcessor(activity);
        DataSource ds = new DataSource(processor, new DataSource.DataSourceCallbacksResult() {
            @Override
            public void onError(Exception e) {
                ErrorHelper.showError(activity, e);
            }

            @Override
            public void onLoadEnd(int recordsFetched) {
            }


            @Override
            public void onBeforeStart() {
            }

            @Override
            public void onResult(Bundle result) {
                if (result != null) {
                    updateUserHeaderUI(headerView, result);
                    UserDetailFragment.this.getCollectionViewWrapper().getCollectionView().addHeaderView(headerView);
                }
            }
        });
        ds.fillData(Api.getUserInfoWide(mUserId, activity), activity);
    }

    private void updateUserHeaderUI(View headerView, Bundle userData) {
        if (!userData.containsKey(UserInfoFullProcessor.USER_INFO)) {
            throw new IllegalArgumentException("Invalid arguments in response");
        }
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        mUserInfo = (UserInfoFull) userData.get(UserInfoFullProcessor.USER_INFO);
        ImageView userImage = (ImageView) headerView.findViewById(R.id.userPhoto);
        mImageLoader.loadImage(userImage, mUserInfo.getUserImage());
        TextView userName = (TextView) headerView.findViewById(R.id.userName);
        userName.setText(mUserInfo.getUserName());

        TextView tvLastSeen = (TextView) headerView.findViewById(R.id.lastseen);
        String lastSeen = activity.getResources().getString(R.string.last_seen);
        tvLastSeen.setText(String.format(lastSeen, mUserInfo.getTime()));

        TextView status = (TextView) headerView.findViewById(R.id.status);
        status.setText(mUserInfo.getStatus());

        Button friends = (Button) headerView.findViewById(R.id.friends);
        if (isEmpty(mUserInfo.getFriends())) {
            friends.setVisibility(View.GONE);
        } else {
            friends.setText(
                    getCaseTitle(activity,
                            mUserInfo.getFriends(),
                            R.string.friend_count1,
                            R.string.friend_count2,
                            R.string.friend_count3
                    )
            );
        }
        friends.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ListActivity.class);
                intent.putExtra(UsersDBHelper.ID, mUserId);
                intent.putExtra(ListActivity.FRAGMENT_TYPE, ListActivity.ListFragments.FRIENDS.ordinal());
                String title = activity.getString(R.string.friends);
                intent.putExtra(ListActivity.TITLE, title);
                startActivity(intent);
            }
        });


        Button signers = (Button) headerView.findViewById(R.id.signers);
        if (isEmpty(mUserInfo.getFollowers())) {
            signers.setVisibility(View.GONE);
        } else {
            signers.setText(
                    getCaseTitle(activity,
                            mUserInfo.getFollowers(),
                            R.string.signer_count1,
                            R.string.signer_count2,
                            R.string.signer_count3
                    )
            );
        }

        Button foto = (Button) headerView.findViewById(R.id.foto);
        if (isEmpty(mUserInfo.getFoto())) {
            foto.setVisibility(View.GONE);
        } else {
            foto.setText(String.format(activity.getResources().getString(R.string.foto_count), mUserInfo.getFoto()));
        }

        Button video = (Button) headerView.findViewById(R.id.video);
        if (isEmpty(mUserInfo.getVideos())) {
            video.setVisibility(View.GONE);
        } else {
            video.setText(String.format(activity.getResources().getString(R.string.video_count), mUserInfo.getVideos()));
        }

        Button audio = (Button) headerView.findViewById(R.id.audio);
        if (isEmpty(mUserInfo.getAudios())) {
            audio.setVisibility(View.GONE);
        } else {
            audio.setText(String.format(activity.getResources().getString(R.string.audio_count), mUserInfo.getAudios()));
        }

        Button groups = (Button) headerView.findViewById(R.id.groups);
        if (isEmpty(mUserInfo.getGroups())) {
            groups.setVisibility(View.GONE);
        } else {
            groups.setText(
                    getCaseTitle(activity,
                            mUserInfo.getGroups(),
                            R.string.group_count1,
                            R.string.group_count2,
                            R.string.group_count3
                    )
            );
        }

        Button sendMessageBtn = (Button) headerView.findViewById(R.id.sendmessage);
        if (mUserInfo.getCanWriteMessage().isEmpty()) {
            sendMessageBtn.setVisibility(View.GONE);
        } else
            sendMessageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage();
                }
            });
    }

    private void sendMessage() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, MessagesActivity.class);
        intent.putExtra(UsersDBHelper.NAME, mUserInfo.getUserName());
        intent.putExtra(UsersDBHelper.ID, mUserId);
        startActivity(intent);
    }

    private boolean isEmpty(String s) {
        return TextUtils.isEmpty(s) || ZERO.equals(s);
    }

    private String getCaseTitle(Context context, String sCount, int res1, int res2, int res3) {
        if (TextUtils.isEmpty(sCount)) {
            return "";
        }
        int i = Integer.parseInt(sCount);
        i = i % 100;
        if (i >= 10 && i <= 20) {
            return String.format(context.getResources().getString(res1), sCount);
        } else if (i > 20 || i < 5) {
            if (i % 10 == 1) {
                return String.format(context.getResources().getString(res2), sCount);
            } else if ((i % 10) == 2 || (i % 10) == 3 || (i % 10) == 4) {
                return String.format(context.getResources().getString(res3), sCount);
            }
        }
        return String.format(context.getResources().getString(res1), sCount);
    }


}
