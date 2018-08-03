package com.bowenchin.android.noter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.ImageView;
import android.widget.TextView;

import com.bowenchin.android.noter.provider.TaskProvider;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;

/**
 * A simple {@link Fragment} subclass.
 */

public class TaskListFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    RecyclerView recyclerView;
    TaskListAdapter adapter;
    private TextView emptyView;
    private ImageView empty_view_illustration;


    public TaskListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        adapter = new TaskListAdapter();

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int ignored, Bundle args){
        return new CursorLoader(getActivity(), TaskProvider.CONTENT_URI,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor){
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        adapter.swapCursor(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_task_list, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        emptyView = (TextView) v.findViewById(R.id.empty_view);
        empty_view_illustration = (ImageView)v.findViewById(R.id.empty_view_illustration);

        Log.e("adapter", "Count: " + adapter.getItemCount());
        if (adapter.getItemCount() > -1 ) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            empty_view_illustration.setVisibility(View.GONE);
        }
        else {
            recyclerView.setVisibility(View.INVISIBLE);
            emptyView.setVisibility(View.VISIBLE);
            empty_view_illustration.setVisibility(View.VISIBLE);
        }

        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(recyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    adapter.deleteTask(getContext(),adapter.getItemId(position));
                                    adapter.notifyItemRemoved(getId());
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    adapter.deleteTask(getContext(),adapter.getItemId(position));
                                    adapter.notifyItemRemoved(position);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);

        return v;
    }

}
