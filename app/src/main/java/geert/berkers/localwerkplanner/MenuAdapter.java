package geert.berkers.localwerkplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by Geert on 11-9-2015
 */

class MenuAdapter extends BaseAdapter {
    private final Context context;

    private final String[] sort;
    private final Integer[] images = {R.drawable.ic_schedule_black_36dp, R.drawable.ic_restore_black_36dp, R.drawable.ic_settings_black_36dp, R.drawable.ic_info_outline_black_36dp};

    public MenuAdapter(Context context){
        this.context = context;
        sort = context.getResources().getStringArray(R.array.menu);
    }

    @Override
    public int getCount() {
        return sort.length;
    }

    @Override
    public String getItem(int position) {
        return sort[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.drawer_layout, parent, false);
        } else {
            row = convertView;
        }
        TextView titleMenuItem = (TextView) row.findViewById(R.id.menuItem);
        ImageView titleImageView = (ImageView) row.findViewById(R.id.menuPicture);

        titleMenuItem.setText(sort[position]);
        titleImageView.setImageResource(images[position]);

        return row;
    }
}