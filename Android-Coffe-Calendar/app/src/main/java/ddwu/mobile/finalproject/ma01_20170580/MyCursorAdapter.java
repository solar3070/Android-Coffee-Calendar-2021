package ddwu.mobile.finalproject.ma01_20170580;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyCursorAdapter extends android.widget.CursorAdapter {

    LayoutInflater inflater;
    int layout;

    public MyCursorAdapter(Context context, int layout, Cursor c) {
        super(context, c, android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(layout, parent, false);

        ViewHolder holder = new ViewHolder();
        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = (ViewHolder) view.getTag();

        if (holder.tvDate == null) {
            holder.tvDate = view.findViewById(R.id.tvDate);
            holder.tvCafe = view.findViewById(R.id.tvCafe);
            holder.tvMenu = view.findViewById(R.id.tvMenu);
        }

        int dateIdx = cursor.getColumnIndex(DBHelper.COL_DATE);
        int cafeIdx = cursor.getColumnIndex(DBHelper.COL_CAFE);
        int menuIdx = cursor.getColumnIndex(DBHelper.COL_MENU);

        holder.tvDate.setText(cursor.getString(dateIdx));
        holder.tvCafe.setText(cursor.getString(cafeIdx));
        holder.tvMenu.setText(cursor.getString(menuIdx));
    }

    static class ViewHolder {
        TextView tvDate;
        TextView tvCafe;
        TextView tvMenu;

        public ViewHolder() {
            tvDate = null;
            tvCafe = null;
            tvMenu = null;
        }
    }
}