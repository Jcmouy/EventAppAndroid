package com.example.admin.eventappb.Dialog;

import android.app.Application;
import android.app.Dialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.eventappb.Adapter.ContactoAdapter;
import com.example.admin.eventappb.DTO.ContactoDTO;
import com.example.admin.eventappb.DTO.IconoDTO;
import com.example.admin.eventappb.MainActivity;
import com.example.admin.eventappb.R;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

/**
 * Created by JC on 16/1/2018.
 */

public class DSelectContact extends DialogFragment {

    // ArrayList
    ArrayList<ContactoDTO> contactos_seleccionados;
    ArrayList<ContactoDTO> contactos;
    List<ContactoDTO> temp;
    // Contact List
    ListView listView;
    // Cursor to load contacts list
    Cursor telefonos, email;

    //Layout
    LinearLayout buttom_layout;

    // Pop up
    ContentResolver resolver;
    SearchView search;
    ContactoAdapter adapter;

    private View v = null;

    TextView count_elements;
    private int checkedCount = 0 ;
    final ArrayList<String> telefonos_seleccionados = new ArrayList<>();

    public DSelectContact(){

    }

    /*

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String title = "Prueba";
        View v = inflater.inflate(R.layout.dialog_select_contact, container, false);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar_contact);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the menu item
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.main);
        toolbar.setTitle(title);
        return v;
    }
    */

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /*
        if (savedInstanceState != null) {
            // Restore last state for checked position.
        }
        setHasOptionsMenu(true);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.activity_select_contact, null);
        return createDSelectContact(v);
        */

        /*
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
        */

        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.activity_select_contact, null);

        count_elements = (TextView) v.findViewById(R.id.elementsHint);
        buttom_layout = (LinearLayout) v.findViewById(R.id.contact_button_layout);

        Button button_accept = (Button) v.findViewById(R.id.button_accept_contact);
        Button button_cancel = (Button) v.findViewById(R.id.button_cancel_contact);

        contactos = new ArrayList<ContactoDTO>();
        resolver = getContext().getContentResolver();
        listView = (ListView) v.findViewById(R.id.contacts_list);

        telefonos = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        LoadContact loadContact = new LoadContact();
        loadContact.execute();

        search = (SearchView) v.findViewById(R.id.searchView);

        //*** setOnQueryTextListener ***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                adapter.filter(newText);
                return false;
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        button_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DCreateEvent)(DSelectContact.this.getParentFragment())).onContactSelected(telefonos_seleccionados);

                dismiss();

                Toast.makeText(getActivity(), "Contacts selected",
                        Toast.LENGTH_LONG).show();

            }
        });

        return createDSelectContact(v);

    }

    private Dialog createDSelectContact(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(v);

        return builder.create();
    }

    /*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        super.onCreateOptionsMenu(menu, inflater);
    }
    */

    // Load data on background
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(getContext());

            String last_phoneNumber = "0";

            if (telefonos != null) {
                Log.e("count", "" + telefonos.getCount());
                if (telefonos.getCount() == 0) {
                    Toast.makeText(getContext(), "No contacts in your contact list.", Toast.LENGTH_LONG).show();
                }

                while (telefonos.moveToNext()) {
                    Bitmap bit_thumb = null;
                    String id = telefonos.getString(telefonos.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = telefonos.getString(telefonos.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = telefonos.getString(telefonos.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String EmailAddr = telefonos.getString(telefonos.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA2));
                    String image_thumb = telefonos.getString(telefonos.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                    try {
                        if (image_thumb != null) {
                            bit_thumb = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(image_thumb));
                        } else {
                            Log.e("No Image Thumb", "--------------");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    try {
                        Phonenumber.PhoneNumber phone = phoneUtil.parse(phoneNumber, "UY");
                        PhoneNumberUtil.PhoneNumberType tipo = phoneUtil.getNumberType(phone);
                        Log.e("PhoneUtil", tipo.toString());

                        if (last_phoneNumber.equals(phoneNumber)) {
                            Log.e("Load Contact", "Same phone number");
                        }else if(!tipo.toString().equals("MOBILE")){
                            Log.e("Load Contact", "Not mobile number");
                        }else {
                            ContactoDTO contacto = new ContactoDTO();
                            contacto.setThumb(bit_thumb);
                            contacto.setNombre(name);
                            contacto.setTelefono(phoneNumber);
                            contacto.setEmail(id);
                            contacto.setCheckedBox(false);
                            contactos.add(contacto);
                        }

                        last_phoneNumber = phoneNumber;
                        last_phoneNumber = last_phoneNumber.replaceAll("\\s+","");

                    } catch (NumberParseException e) {
                        e.printStackTrace();
                    }

                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }
            //phones.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            adapter = new ContactoAdapter(contactos, getContext());
            listView.setAdapter(adapter);
            contactos_seleccionados = new ArrayList<>();

            // Select item on listclick
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    ContactoDTO data = contactos.get(i);

                    if (!data.getCheckedBox()){
                        data.setCheckedBox(true);
                        checkedCount ++;
                        contactos_seleccionados.add(data);
                    }else {
                        data.setCheckedBox(false);
                        checkedCount = checkedCount - 1;
                        contactos_seleccionados.remove(data);
                        telefonos_seleccionados.remove(data.getTelefono());
                    }

                    count_elements.setText(getResources().getString(R.string.selected) + " " + checkedCount);

                    if (checkedCount == 0){
                        count_elements.setText("");
                        buttom_layout.setVisibility(View.INVISIBLE);
                    }else{
                        buttom_layout.setVisibility(View.VISIBLE);
                    }

                    Log.e("search", String.valueOf(checkedCount));

                    //Get nº phones
                    for (int z=0; z < contactos_seleccionados.size(); z++){
                        String t = contactos_seleccionados.get(z).getTelefono();
                        if (telefonos_seleccionados.size() == 0){
                            telefonos_seleccionados.add(t);
                        } else if (!telefonos_seleccionados.contains(t)){
                            telefonos_seleccionados.add(t);
                        }
                    }

                    Log.e("Selected contacts", contactos_seleccionados.toString());

                    // Drawable for tick doesnt work yet
                    /*
                    Bitmap bm = BitmapFactory.decodeResource(view.getResources(), R.drawable.checked_blue); // Load default image
                    ImageView ch = view.findViewById(R.id.cheked);

                    if (data.getThumb_checked() == null){
                        data.setThumb_checked(bm);
                        ch.setImageBitmap(bm);
                    }else{
                        data.setThumb_checked(null);
                        ch.setImageBitmap(null);
                    }
                    */

                    adapter.notifyDataSetChanged();
                }
            });

            listView.setFastScrollEnabled(true);        }
    }

    @Override
    public void onStop() {
        super.onStop();
        telefonos.close();
    }

}
