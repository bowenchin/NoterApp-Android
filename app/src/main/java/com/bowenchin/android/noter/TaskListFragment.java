package com.bowenchin.android.noter;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.ImageView;
import android.widget.TextView;

import com.bowenchin.android.noter.provider.TaskProvider;
import com.bowenchin.android.noter.util.ReminderManager;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;

/**
 * A simple {@link Fragment} subclass.
 */

public class TaskListFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    RecyclerView recyclerView;
    TaskListAdapter adapter;
    private TextView emptyView;
    private ImageView empty_view_illustration;
    private Boolean doSort = false;


    public TaskListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        Preferences.applyTheme(getActivity());
        super.onCreate(savedInstanceState);
        adapter = new TaskListAdapter();
        setHasOptionsMenu(true);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_date) {
            doSort = true;
            Bundle args = new Bundle();
            args.putString("sortOrder", "task_date_time DESC");
            getLoaderManager().restartLoader(id, args, this);
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_alpha) {
            doSort = true;
            Bundle args = new Bundle();
            args.putString("sortOrder", "title");
            getLoaderManager().restartLoader(id, args, this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int ignored, Bundle args){
        return new CursorLoader(getActivity(), TaskProvider.CONTENT_URI,null,null,null, doSort ? args.getString("sortOrder") : "task_date_time");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor){
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        adapter.swapCursor(null);
        loader.reset();
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
                                final int[] rsp = reverseSortedPositions;

                                new AlertDialog.Builder(getContext()).setTitle(R.string.delete_q)
                                        .setMessage(R.string.delete_ask).setCancelable(true)
                                        .setNegativeButton(android.R.string.cancel,null)
                                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i){
                                                for (int position : rsp) {
                                                    adapter.deleteTask(getContext(),adapter.getItemId(position));
                                                    adapter.notifyItemRemoved(position);
                                                }
                                                adapter.notifyDataSetChanged();
                                                setEmptyView();
                                            }
                                        }).show();

                                adapter.notifyDataSetChanged();
                                setEmptyView();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                final int[] rsp = reverseSortedPositions;

                                new AlertDialog.Builder(getContext()).setTitle(R.string.delete_q)
                                        .setMessage(R.string.delete_ask).setCancelable(true)
                                        .setNegativeButton(android.R.string.cancel,null)
                                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i){
                                                for (int position : rsp) {
                                                    adapter.deleteTask(getContext(),adapter.getItemId(position));
                                                    adapter.notifyItemRemoved(position);
                                                }
                                                adapter.notifyDataSetChanged();
                                                setEmptyView();
                                            }
                                        }).show();

                                adapter.notifyDataSetChanged();
                                setEmptyView();
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        Preferences.applyTheme(getActivity());
        setEmptyView();
    }

    private void setEmptyView(){
        Cursor cursor = getContext().getContentResolver().query(
                TaskProvider.CONTENT_URI, null, null, null, null);
        if(cursor.moveToFirst()){
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            empty_view_illustration.setVisibility(View.GONE);
        }
        else {
            recyclerView.setVisibility(View.INVISIBLE);
            emptyView.setVisibility(View.VISIBLE);
            empty_view_illustration.setVisibility(View.VISIBLE);
        }
    }

}
