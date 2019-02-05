package onipractice.mahmoud.com.fitnessapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import onipractice.mahmoud.com.fitnessapp.ChooseUserTypeActivity;
import onipractice.mahmoud.com.fitnessapp.R;

public class GalleryAdapter extends BaseAdapter {

    private Context ctx;
    private int pos;
    private LayoutInflater inflater;
    private ImageView ivGallery;
    TextView date;
    ArrayList<Uri> mArrayUri;

    //Shared Preference
    SharedPreferences.Editor editor;
    SharedPreferences cacheData;

    public GalleryAdapter(Context ctx, ArrayList<Uri> mArrayUri) {

        this.ctx = ctx;
        this.mArrayUri = mArrayUri;

        cacheData = ctx.getSharedPreferences("ProgressGallery", ChooseUserTypeActivity.MODE_PRIVATE);
        editor = cacheData.edit();
    }

    @Override
    public int getCount() {


        return mArrayUri.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayUri.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);

        pos = position;
        inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.progress_gallery_item, parent, false);
        date = (TextView) itemView.findViewById(R.id.dateTv);
        date.setText(formattedDate);
        ivGallery = (ImageView) itemView.findViewById(R.id.ivGallery);
        for (int i = 0; i < mArrayUri.size(); i++) {

            Picasso.get().load(mArrayUri.get(position)).into(ivGallery);
        }

        return itemView;
    }

}
