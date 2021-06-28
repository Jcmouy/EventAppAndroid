package com.example.admin.eventappb.Adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.eventappb.DTO.ContactoDTO;
import com.example.admin.eventappb.Dialog.DSelectContact;
import com.example.admin.eventappb.R;
import com.example.admin.eventappb.Utils.RoundImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by JC on 25/1/2018.
 */

public class ContactoAdapter extends BaseAdapter{

    public List<ContactoDTO> _data;
    private ArrayList<ContactoDTO> arraylist;
    Context _c;
    ViewHolder v;
    RoundImage roundedImage;

    public ContactoAdapter(List<ContactoDTO> contactos, Context context) {
        _data = contactos;
        _c = context;
        this.arraylist = new ArrayList<ContactoDTO>();
        this.arraylist.addAll(_data);
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public Object getItem(int i) {
        return _data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.dialog_select_contact, null);
            Log.e("Inside", "here--------------------------- In view1");
        } else {
            view = convertView;
            Log.e("Inside", "here--------------------------- In view2");
        }

        v = new ViewHolder();

        v.title = (TextView) view.findViewById(R.id.name);
        v.check = (CheckBox) view.findViewById(R.id.check);
        v.phone = (TextView) view.findViewById(R.id.no);
        v.imageView = (ImageView) view.findViewById(R.id.pic);
        v.checked = (ImageView) view.findViewById(R.id.cheked);

        final ContactoDTO data = (ContactoDTO) _data.get(i);
        v.title.setText(data.getNombre());
        v.check.setChecked(data.getCheckedBox());
        v.phone.setText(data.getTelefono());

         /*// Set check box listener android
        v.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    data.setCheckedBox(true);
                  } else {
                    data.setCheckedBox(false);
                }
            }
        });*/

        /*
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v.checked.setImageResource(R.drawable.checked);
            }
        });
        */


        // Set image if exists
        try {

            if (data.getThumb() != null) {
                v.imageView.setImageBitmap(data.getThumb());
                roundedImage = new RoundImage(data.getThumb());
            } else {
                v.imageView.setImageResource(R.drawable.profile);
                Bitmap bm = BitmapFactory.decodeResource(view.getResources(), R.drawable.profile); // Load default image
                roundedImage = new RoundImage(bm);
            }

            // Seting round image
            v.imageView.setImageDrawable(roundedImage);
        } catch (OutOfMemoryError e) {
            // Add default picture
            v.imageView.setImageDrawable(this._c.getDrawable(R.drawable.profile));
            e.printStackTrace();
        }

        Log.e("Image Thumb", "--------------" + data.getThumb());

        view.setTag(data);
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        _data.clear();
        if (charText.length() == 0) {
            _data.addAll(arraylist);
        } else {
            for (ContactoDTO wp : arraylist) {
                if (wp.getNombre().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    _data.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
    static class ViewHolder {
        ImageView imageView;
        TextView title, phone;
        CheckBox check;
        ImageView checked;
    }


}
