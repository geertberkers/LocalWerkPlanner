package geert.berkers.localwerkplanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Geert on 11-9-2015
 */

class WorkAdapter extends BaseAdapter {
    private Context context;

    private Activity activity;

    private ListView listView;
    private TextView emptyTextView;

    private WorkAdapter adapter;

    private ArrayList<Work> workList =  new ArrayList<>();

    public WorkAdapter(Context context, ArrayList<Work> workList, Activity activity, ListView listView, TextView emptyTextView) {
        this.context = context;
        this.activity = activity;
        this.workList = workList;
        this.listView = listView;
        this.emptyTextView = emptyTextView;
        this.adapter= this;
    }

    @Override
    public int getCount() {
        return workList.size();
    }

    @Override
    public Work getItem(int position) {
        return workList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.work_layout, parent, false);
        } else {
            row = convertView;
        }

        TextView workItem = (TextView) row.findViewById(R.id.workItem);
        TextView dayOfWeek = (TextView) row.findViewById(R.id.dayOfWeek);
        ImageView editImageView = (ImageView) row.findViewById(R.id.editPicture);
        ImageView deleteImageView = (ImageView) row.findViewById(R.id.deletePicture);

        final SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());

        String dateFormat = sharedPref.getString("dateFormat", "dd-MM-yyy");
        String dayOfWeekString = workList.get(position).getDayOfWeek();
        workItem.setText(workList.get(position).getWorkString(dateFormat));
        dayOfWeek.setText(dayOfWeekString);
        editImageView.setImageResource(R.drawable.ic_mode_edit_black_24dp);
        deleteImageView.setImageResource(R.drawable.ic_delete_black_24dp);


        String[] weekendDays = context.getResources().getStringArray(R.array.weekendDays);

        for(String s : weekendDays){
            if(s.equals(dayOfWeekString)){
                row.setBackgroundResource(R.color.transBlauw);
            }
        }

        editImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent workEditorIntent = new Intent(v.getContext(), WorkEditor.class);
                workEditorIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                workEditorIntent.putExtra("workParcelable", workList.get(position).getWork());
                v.getContext().startActivity(workEditorIntent);
            }
        });

        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                alertDialog.setTitle(R.string.delete_work);
                alertDialog.setMessage(R.string.confirmation);
                alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {

                        Dialog dialog = (Dialog) dialogInterface;
                        Context context = dialog.getContext();

                        MySQLiteHelper db = new MySQLiteHelper(dialog.getContext());
                        db.deleteWork(workList.get(position).getWork());

                        workList.remove(workList.get(position).getWork());

                        if(workList.isEmpty()){
                            listView.setVisibility(View.INVISIBLE);
                            emptyTextView.setVisibility(View.VISIBLE);
                        }
                        else {
                            listView.setVisibility(View.VISIBLE);
                            emptyTextView.setVisibility(View.INVISIBLE);
                            listView.setAdapter(adapter);
                        }

                        UpdateAppWidget updateAppWidget = new UpdateAppWidget(context);
                        updateAppWidget.updateAppWidget();
                    }
                });
                alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                alertDialog.setIcon(R.drawable.ic_delete_black_36dp);
                alertDialog.show();
            }
        });
        return row;
    }
}

