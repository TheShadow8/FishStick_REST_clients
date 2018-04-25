/* File: StuffArrayAdapter.java
 * Author: Chao Gu, Qiuju Zhu, Weikai Li, Di Yuan, Binh Minh Do
 * Date: April 2018
 * Reference:
 * Paul Deitel, Harvey Deitel, Alexander Wald. (2015). Android™ 6 for Programmers: An App-Driven Approach, Third Edition
 * Prentice Hall, ISBN: 0-13-428936-6. Chapter 7 WeatherViewer App pp256-285
 */


package com.algonquin.fishstickandroidclient_rest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;



public class FishStickArrayAdaper extends ArrayAdapter<FishStick> {

    private static class ViewHolder {
        TextView fsIDTextView;
        TextView fsRecordNumberTextView;
        TextView fsOmegaTextView;
        TextView fsLambdaTextView;
        TextView fsUUIDTextView;
    }

    // constructor to initialize superclass inherited members
    public FishStickArrayAdaper(Context context, List<FishStick> fishSticks) {
        super(context, -1, fishSticks);
    }

    // creates the custom views for the ListView's items
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        try {
            // get FishStick object for this specified ListView position
            FishStick fsItem = getItem(position);

            ViewHolder viewHolder; // object that reference's list item's views

            // check for reusable ViewHolder from a ListView item that scrolled
            // offscreen; otherwise, create a new ViewHolder
            if (convertView == null) { // no reusable ViewHolder, so create one
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView =
                        inflater.inflate(R.layout.list_item, parent, false);
                viewHolder.fsIDTextView =
                        (TextView) convertView.findViewById(R.id.fsIDTextView);
                viewHolder.fsRecordNumberTextView =
                        (TextView) convertView.findViewById(R.id.fsRecordNumberTextView);
                viewHolder.fsOmegaTextView =
                        (TextView) convertView.findViewById(R.id.fsOmegaTextView);
                viewHolder.fsLambdaTextView =
                        (TextView) convertView.findViewById(R.id.fsLambdaTextView);
                viewHolder.fsUUIDTextView =
                        (TextView) convertView.findViewById(R.id.fsUUIDTextView);

                convertView.setTag(viewHolder);
            } else { // reuse existing ViewHolder stored as the list item's tag
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // get other data from FishStick object and place into views
            Context context = getContext(); // for loading String resources
            viewHolder.fsIDTextView.setText(context.getString(
                    R.string.fishStick_id, fsItem.id));
            viewHolder.fsRecordNumberTextView.setText(
                    context.getString(R.string.fishStick_recordNum, fsItem.recordNumber));
            viewHolder.fsOmegaTextView.setText(
                    context.getString(R.string.fishStick_omega, fsItem.omega));
            viewHolder.fsLambdaTextView.setText(
                    context.getString(R.string.fishStick_lambda, fsItem.lambda));
            viewHolder.fsUUIDTextView.setText(
                    context.getString(R.string.fishStick_uuid, fsItem.uuid));
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        return convertView; // return completed list item to display
    }
}
