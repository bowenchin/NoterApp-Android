package com.bowenchin.android.noter;


import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bowenchin.android.noter.interfaces.OnEditTask;
import com.bowenchin.android.noter.provider.TaskProvider;
import com.bowenchin.android.noter.util.ReminderManager;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;

import java.text.SimpleDateFormat;

/**
 * Created by bowenchin on 27/12/15.
 */
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {
    private ImageView mColorImageView;

    Cursor cursor;
    int titleColumnIndex;
    int notesColumnIndex;
    int idColumnIndex;
    int dateTimeColumnIndex;

    public void swapCursor(Cursor c){
        cursor = c;
        if(cursor != null){
            cursor.moveToFirst();
            titleColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_TITLE);
            notesColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_NOTES);
            idColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_TASKID);
            dateTimeColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_DATE_TIME);
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i){
        //Create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_task,parent,false);

        //wrap it in a ViewHolder
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final Context context = viewHolder.titleView.getContext();
        final long id = getItemId(position);

        final TaskListActivity taskListActivity = new TaskListActivity();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd hh:mm");
        String dateString = sdf.format(cursor.getLong(dateTimeColumnIndex));

        //set the text
        cursor.moveToPosition(position);
        viewHolder.titleView.setText(cursor.getString(titleColumnIndex));
        viewHolder.notesView.setText(cursor.getString(notesColumnIndex));
        viewHolder.dateTimeView.setText(dateString);

        //Set the thumbnail image
        ColorGenerator generator = ColorGenerator.MATERIAL;
        final int color = generator.getRandomColor();

        String title = viewHolder.titleView.getText().toString();

        if(title==null || title.equals("") || title.equals(" "))
        {
            //EditText is empty
            title="##";
        }
        else {
            title = viewHolder.titleView.getText().toString();
        }

        TextDrawable myDrawable = TextDrawable.builder().beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .toUpperCase()
                .endConfig()
                .buildRound(title.substring(0, 1), color);

        viewHolder.imageView.setImageDrawable(myDrawable);

        //Set the click action
        viewHolder.cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ((OnEditTask)context).editTask(id);
            }
        });

        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view){
                new AlertDialog.Builder(context).setTitle(R.string.delete_q)
                        .setMessage(viewHolder.titleView.getText()).setCancelable(true)
                        .setNegativeButton(android.R.string.cancel,null)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i){
                                deleteTask(context, id);
                            }
                        }).show();

                return true;
            }
        });
    }

    @Override
    public long getItemId(int position){
        cursor.moveToPosition(position);
        return cursor.getLong(idColumnIndex);
    }

    public void deleteTask(Context context, long id){
        context.getContentResolver().delete(ContentUris.withAppendedId(
                TaskProvider.CONTENT_URI, id), null, null);
        ReminderManager.deleteReminder(context, id);
    }

    @Override
    public int getItemCount() {
        return cursor!=null ? cursor.getCount() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView titleView;
        TextView notesView;
        ImageView imageView;
        TextView dateTimeView;

        public ViewHolder(CardView card){
            super(card);
            cardView = card;
            titleView = (TextView)card.findViewById(R.id.text1);
            imageView = (ImageView)card.findViewById(R.id.toDoListItemColorImageView);
            notesView = (TextView)card.findViewById(R.id.text2);
            dateTimeView = (TextView)card.findViewById(R.id.reminderDate);
        }
    }
}
