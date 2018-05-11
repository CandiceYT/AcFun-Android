/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package advrecyclerview.demo_s_button;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acty.component_advrecyclerview.R;
import com.acty.component_advrecyclerview.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.acty.component_advrecyclerview.advrecyclerview.swipeable.SwipeableItemConstants;
import com.acty.component_advrecyclerview.advrecyclerview.swipeable.action.SwipeResultAction;
import com.acty.component_advrecyclerview.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.acty.component_advrecyclerview.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.acty.component_advrecyclerview.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.acty.component_advrecyclerview.advrecyclerview.utils.RecyclerViewAdapterUtils;

import advrecyclerview.common.data.AbstractDataProvider;
import advrecyclerview.common.utils.ViewUtils;


class SwipeableWithButtonExampleAdapter
        extends RecyclerView.Adapter<SwipeableWithButtonExampleAdapter.MyViewHolder>
        implements SwipeableItemAdapter<SwipeableWithButtonExampleAdapter.MyViewHolder> {
    private static final String TAG = "MySwipeableItemAdapter";

    // NOTE: Make accessible with short name
    private interface Swipeable extends SwipeableItemConstants {
    }

    private AbstractDataProvider mProvider;
    private EventListener mEventListener;
    private View.OnClickListener mSwipeableViewContainerOnClickListener;
    private View.OnClickListener mUnderSwipeableViewButtonOnClickListener;

    public interface EventListener {
        void onItemPinned( int position );

        void onItemViewClicked( View v );

        void onUnderSwipeableViewButtonClicked( View v );
    }

    public static class MyViewHolder extends AbstractSwipeableItemViewHolder {
        public FrameLayout mContainer;
        public RelativeLayout mBehindViews;
        public TextView mTextView;
        public Button mButton;

        public MyViewHolder(View v) {
            super(v);
            mContainer = v.findViewById( R.id.container);
            mBehindViews = v.findViewById(R.id.behind_views);
            mTextView = v.findViewById(android.R.id.text1);
            mButton = v.findViewById(android.R.id.button1);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }

    }

    public SwipeableWithButtonExampleAdapter(AbstractDataProvider dataProvider) {
        mProvider = dataProvider;
        mSwipeableViewContainerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwipeableViewContainerClick(v);
            }
        };
        mUnderSwipeableViewButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUnderSwipeableViewButtonClick(v);
            }
        };

        // SwipeableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    private void onSwipeableViewContainerClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(
                    RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }
    }

    private void onUnderSwipeableViewButtonClick(View v) {
        if (mEventListener != null) {
            mEventListener.onUnderSwipeableViewButtonClicked(
                    RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }
    }

    @Override
    public long getItemId(int position) {
        return mProvider.getItem(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return mProvider.getItem(position).getViewType();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_item_with_leave_behind_button, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final AbstractDataProvider.Data item = mProvider.getItem(position);

        // set listeners
        // (if the item is *pinned*, click event comes to the mContainer)
        holder.mContainer.setOnClickListener(mSwipeableViewContainerOnClickListener);
        holder.mButton.setOnClickListener(mUnderSwipeableViewButtonOnClickListener);

        // set text
        holder.mTextView.setText(item.getText());

        // set background resource (target view ID: container)
        final int swipeState = holder.getSwipeStateFlags();

        if ((swipeState & Swipeable.STATE_FLAG_IS_UPDATED) != 0) {
            int bgResId;

            if ((swipeState & Swipeable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_swiping_active_state;
            } else if ((swipeState & Swipeable.STATE_FLAG_SWIPING) != 0) {
                bgResId = R.drawable.bg_item_swiping_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }

        // set swiping properties
        holder.setMaxLeftSwipeAmount(-0.5f);
        holder.setMaxRightSwipeAmount(0);
        holder.setSwipeItemHorizontalSlideAmount(item.isPinned() ? -0.5f : 0);

        // Or, it can be specified in pixels instead of proportional value.
        // float density = holder.itemView.getResources().getDisplayMetrics().density;
        // float pinnedDistance = (density * 100); // 100 dp

        // holder.setProportionalSwipeAmountModeEnabled(false);
        // holder.setMaxLeftSwipeAmount(-pinnedDistance);
        // holder.setMaxRightSwipeAmount(0);
        // holder.setSwipeItemHorizontalSlideAmount(item.isPinned() ? -pinnedDistance: 0);
    }

    @Override
    public int getItemCount() {
        return mProvider.getCount();
    }

    @Override
    public int onGetSwipeReactionType(MyViewHolder holder, int position, int x, int y) {
        if ( ViewUtils.hitTest(holder.getSwipeableContainerView(), x, y)) {
            return Swipeable.REACTION_CAN_SWIPE_BOTH_H;
        } else {
            return Swipeable.REACTION_CAN_NOT_SWIPE_BOTH_H;
        }
    }

    @Override
    public void onSwipeItemStarted(MyViewHolder holder, int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onSetSwipeBackground(MyViewHolder holder, int position, int type) {
        if (type == Swipeable.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND) {
            holder.mBehindViews.setVisibility(View.GONE);
        } else {
            holder.mBehindViews.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public SwipeResultAction onSwipeItem( MyViewHolder holder, int position, int result) {
        Log.d(TAG, "onSwipeItem(position = " + position + ", result = " + result + ")");

        switch (result) {
            // swipe left --- pin
            case Swipeable.RESULT_SWIPED_LEFT:
                return new SwipeLeftResultAction(this, position);
            // other --- do nothing
            case Swipeable.RESULT_SWIPED_RIGHT:
            case Swipeable.RESULT_CANCELED:
            default:
                if (position != RecyclerView.NO_POSITION) {
                    return new UnpinResultAction(this, position);
                } else {
                    return null;
                }
        }
    }

    public EventListener getEventListener() {
        return mEventListener;
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    private static class SwipeLeftResultAction extends SwipeResultActionMoveToSwipedDirection {
        private SwipeableWithButtonExampleAdapter mAdapter;
        private final int mPosition;
        private boolean mSetPinned;

        SwipeLeftResultAction(SwipeableWithButtonExampleAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            AbstractDataProvider.Data item = mAdapter.mProvider.getItem(mPosition);

            if (!item.isPinned()) {
                item.setPinned(true);
                mAdapter.notifyItemChanged(mPosition);
                mSetPinned = true;
            }
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mSetPinned && mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onItemPinned(mPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class UnpinResultAction extends SwipeResultActionDefault {
        private SwipeableWithButtonExampleAdapter mAdapter;
        private final int mPosition;

        UnpinResultAction(SwipeableWithButtonExampleAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            AbstractDataProvider.Data item = mAdapter.mProvider.getItem(mPosition);
            if (item.isPinned()) {
                item.setPinned(false);
                mAdapter.notifyItemChanged(mPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }
}