package com.example.admin.eventappb.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.admin.eventappb.DTO.IconoDTO;
import com.example.admin.eventappb.R;

/**
 * Created by JC on 20/9/2017.
 */

public class DSelectIconPrueba extends DialogFragment{

    private View v = null;

    private ImageView Selection;

    public DSelectIconPrueba() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Restore last state for checked position.
        }
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.grid_prueba, null);
        return createDSelectIcon(v);
    }

    private AlertDialog createDSelectIcon(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        GridView grid = v.findViewById(R.id.grid_dos);
        // grid.setAdapter(new ArrayAdapter<Integer>(this, R.layout.cell,
        // items));

        grid.setAdapter(new AdaptadorDeCoches(getContext()));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                // TODO Auto-generated method stub

                //Selection.setImageResource(items[arg2]);
                IconoDTO item = (IconoDTO) adapterView.getItemAtPosition(i);
                int item_image = item.getIdDrawable();
                ((DCreateEvent)(DSelectIconPrueba.this.getParentFragment())).onIconSelected(item_image);
                dismiss();
            }
        });


        builder.setView(v);

        return builder.create();
    }

    public class AdaptadorDeCoches extends BaseAdapter {
        private Context context;

        public AdaptadorDeCoches(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return IconoDTO.ITEMS.length;
        }

        @Override
        public IconoDTO getItem(int position) {
            return IconoDTO.ITEMS[position];
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.grid_prueba_item, viewGroup, false);
            }

            ImageView imagenCoche = (ImageView) view.findViewById(R.id.imagen_coche);

            final IconoDTO item = getItem(position);
            imagenCoche.setImageResource(item.getIdDrawable());

            return view;
        }
    }
}

