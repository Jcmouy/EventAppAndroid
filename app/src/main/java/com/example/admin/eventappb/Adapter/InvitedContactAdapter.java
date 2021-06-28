package com.example.admin.eventappb.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.eventappb.R;
import com.example.admin.eventappb.Utils.MyInvitedContacts;

import java.util.List;

/**
 * Created by JC on 28/2/2018.
 */

public class InvitedContactAdapter extends ArrayAdapter<MyInvitedContacts> {

    public InvitedContactAdapter(Context context, List<MyInvitedContacts> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener inflater.
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // ¿Existe el view actual?
        if (null == convertView) {
            convertView = inflater.inflate(
                    R.layout.slidepane_detail_contact,
                    parent,
                    false);
        }

        // Referencias UI.
        ImageView avatar = (ImageView) convertView.findViewById(R.id.details_pic);
        TextView name = (TextView) convertView.findViewById(R.id.details_text_name);
        TextView response = (TextView) convertView.findViewById(R.id.details_text_response);

        MyInvitedContacts invitedContact = getItem(position);

        name.setText(invitedContact.getName());

        String confirmation = invitedContact.getConfirmation();

        switch (confirmation){
            case "Y":
                response.setText(R.string.confirmation_yes);
                break;
            case "N":
                response.setText(R.string.confirmation_no);
                break;
            case "":
                response.setText(R.string.confirmation_wait);
                break;
        }

        return convertView;
    }

}
