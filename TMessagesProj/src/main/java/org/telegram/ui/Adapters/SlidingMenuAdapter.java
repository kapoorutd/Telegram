package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.telegram.messenger.R;
import org.telegram.ui.MenuItems;

import java.util.ArrayList;

/**
 * Created by craterzone on 28/11/14.
 */
public class SlidingMenuAdapter extends ArrayAdapter<MenuItems> {

int shareCount = -1;

    public SlidingMenuAdapter(Context context, ArrayList<MenuItems> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.sliding_menu_row, null);
        }
        MenuItems item = getItem(position);
        TextView title = (TextView) convertView.findViewById(R.id.row_title);
        if(item.isTextShow()){
            title.setVisibility(View.VISIBLE);
            title.setText(item.getName());
        }else{
            title.setVisibility(View.GONE);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.menu_image);
        image.setImageResource(item.getIcon());

        TextView count = (TextView) convertView.findViewById(R.id.menu_count);
        if(item.getCount() == ""){
            count.setText("");
        }


        /*else if (Integer.parseInt(item.getCount()) == -1) {
            count.setText("");
        }*/ else {
            count.setText(String.format("%s", item.getCount()));
        }
        if(count.getText() == "Loading...") {
            if (shareCount != -1) {
                count.setText(String.format("%d", shareCount));
            }
        }


         return convertView;
    }

  public void  setTotalCount(int count){
        this.shareCount = count;
    }



}