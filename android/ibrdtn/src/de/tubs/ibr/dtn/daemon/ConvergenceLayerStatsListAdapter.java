package de.tubs.ibr.dtn.daemon;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.tubs.ibr.dtn.R;
import de.tubs.ibr.dtn.stats.ConvergenceLayerStatsEntry;

public class ConvergenceLayerStatsListAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    private List<ConvergenceLayerStatsEntry> mData = null;
    private Context mContext = null;
    private ColorProvider mColorProvider = null;

    private class ViewHolder {
        public ImageView imageIcon;
        public TextView textName;
        public ConvergenceLayerStatsEntry data;
    }
    
    public interface ColorProvider {
        int getColor(String tag);
    }

    public ConvergenceLayerStatsListAdapter(Context context, ColorProvider provider) {
        this.mContext = context;
        this.mColorProvider = provider;
        this.mInflater = LayoutInflater.from(context);
    }
    
    public void swapList(List<ConvergenceLayerStatsEntry> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public Object getItem(int position) {
        if (mData == null) return null;
        return mData.get(position);
    }
    
    @Override
    public int getCount() {
        if (mData == null) return 0;
        return mData.size();
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.stats_item, null, true);
            holder = new ViewHolder();
            holder.imageIcon = (ImageView) convertView.findViewById(R.id.imageIcon);
            holder.textName = (TextView) convertView.findViewById(R.id.textName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.data = (ConvergenceLayerStatsEntry)getItem(position);
        
        // generate a data key for this series
        String key = holder.data.getConvergenceLayer() + "|" + holder.data.getDataTag();

        holder.imageIcon.setBackgroundColor(mContext.getResources().getColor(mColorProvider.getColor(key)));
        holder.imageIcon.setImageResource(R.drawable.ic_log);

        if ("in".equals(holder.data.getDataTag()) || "out".equals(holder.data.getDataTag()))
        {
            holder.textName.setText(holder.data.getConvergenceLayer() + " [" + holder.data.getDataTag() + "]: " + humanReadableByteCount(holder.data.getDataValue().longValue(), true));
        } else {
            holder.textName.setText(holder.data.getConvergenceLayer() + " [" + holder.data.getDataTag() + "]: " + holder.data.getDataValue());
        }

        return convertView;
    }
    
    @SuppressLint("DefaultLocale")
    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
