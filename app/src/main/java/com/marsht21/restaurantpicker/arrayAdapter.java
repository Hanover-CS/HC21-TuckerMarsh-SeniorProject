package com.marsht21.restaurantpicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
/*
 * arrayAdapter
 * Custom array adapter to populate the cards to be used in the swipeActivity
 */
public class arrayAdapter extends ArrayAdapter<cards>{ //populates cards

    Context context;

    public arrayAdapter(Context context, int resourceId, List<cards> items){
        super(context, resourceId, items);
    }
    public View getView(int position, View convertView, ViewGroup parent){ //adds name to textview, image to imageview ect.
        cards card_item = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.name);
        name.setText(card_item.getName());
        return convertView;
    }
}

