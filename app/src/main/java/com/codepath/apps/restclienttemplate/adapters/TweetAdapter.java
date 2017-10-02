package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.DateTimeUtil;

import jp.wasabeef.glide.transformations.BlurTransformation;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets = new ArrayList<>();;
    Context context;

    private ItemClickListener mClickListener;

    public interface ItemClickListener {
        void onItemClicked(View v, Tweet tweet);
    }

    public TweetAdapter(List<Tweet> tweets, ItemClickListener listener) {
        mClickListener = listener;
        mTweets = tweets;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);

        return new ViewHolder(tweetView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);

        holder.tvUsername.setText(tweet.user.name);
        holder.tvScreenName.setText("@" + tweet.user.screenName);
        holder.tvBody.setText(tweet.body);
        String relativeTime = new DateTimeUtil().getRelativeTimeAgo(tweet.createdAt);
        holder.tvTimeAgo.setText(relativeTime);

        Glide.with(context).load(tweet.user.profileImageUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.ivProfileImage);
    }
    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvScreenName;
        public TextView tvBody;
        public TextView tvTimeAgo;

        public ViewHolder(View itemView) {
            super(itemView);

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTimeAgo = (TextView) itemView.findViewById(R.id.tvTimeAgo);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Tweet tweet = mTweets.get(position);
            mClickListener.onItemClicked(v, tweet);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }
}
