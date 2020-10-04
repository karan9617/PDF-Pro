package com.example.myapplication.ui.notifications;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;

import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.example.myapplication.ContactsAll;
import com.example.myapplication.CustomAdapter;
import com.example.myapplication.CustomAdapter3;
import com.example.myapplication.Database.DBHandler3;
import com.example.myapplication.Main2Activity;
import com.example.myapplication.R;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment{
    ListView listView;
    CustomAdapter customAdapter;
    ArrayList<String> l = new ArrayList<>();
    ListView trashlistview;
    ProgressBar progressbar;

    private SpeechRecognizer speech = null;
    ImageView pdfimage;

    List<ContactsAll> allfiles = new ArrayList<>();
    TextView searching, notfound;
    EditText searchedit;
    ImageView cancel;
    ArrayList<ContactsAll> arrayList2 = new ArrayList<>();
    private NotificationsViewModel notificationsViewModel;
    public String searchstring = "";
    private static final int PERMISSION_REQUEST_CODE = 1;


    int count = 0;
    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            Toast.makeText(getActivity(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        searchedit = root.findViewById(R.id.searchedit);
        listView = root.findViewById(R.id.listview);
        searching = root.findViewById(R.id.searching);
        cancel  = root.findViewById(R.id.cancel);
        pdfimage = root.findViewById(R.id.pdfimage);
        pdfimage.setVisibility(View.INVISIBLE);
        trashlistview = root.findViewById(R.id.trashlistview);
        progressbar = root.findViewById(R.id.progressbar);
        notfound = root.findViewById(R.id.notfound);
        notfound.setVisibility(View.INVISIBLE);
        trashlistview.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
        searching.setVisibility(View.INVISIBLE);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            requestPermission();
        }
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                MobileAds.initialize(getActivity(),"ca-app-pub-3940256099942544~3347511713");

                final DBHandler3 dbHandler3  = new DBHandler3(getActivity().getApplicationContext());
                allfiles = dbHandler3.getAllContacts();
                showSoftKeyboard(searchedit);
                final CustomAdapter3 customAdapter3 = new CustomAdapter3(getActivity(),allfiles);
                listView.setAdapter(customAdapter3);
                listView.setVisibility(View.VISIBLE);
                trashlistview.setVisibility(View.INVISIBLE);
                listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                    @Override
                    public void onItemCheckedStateChanged(ActionMode actionMode, int position, long l, boolean b) {
                        count = count+1;
                        actionMode.setTitle(count + " items selected");
                        arrayList2.add(allfiles.get(position));
                    }
                    @Override
                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        MenuInflater inflater = actionMode.getMenuInflater();
                        inflater.inflate(R.menu.pop_upmenu_star,menu);
                        return true;
                    }
                    @Override
                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }
                    @Override
                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                        switch (menuItem.getItemId())
                        {
                            case R.id.remove:
                                for(ContactsAll msg : arrayList2) {
                                    customAdapter3.remove(msg);
                                }
                                Toast.makeText(getActivity(),"deleted",Toast.LENGTH_SHORT).show();
                                count=0;
                                actionMode.finish();
                                return true;
                            default:
                                Toast.makeText(getActivity(),"Nothing selected",Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode actionMode) {

                    }
                });

               cancel.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                    searchedit.setText("");
                   }
               });
                searchedit.setImeOptions(EditorInfo.IME_ACTION_DONE);
                searchedit.setImeActionLabel("My Text", EditorInfo.IME_ACTION_DONE);
                searchedit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) ||
                                (event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) || (event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_NEXT)) {
                            searching.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.INVISIBLE);

                            hideKeyboard(getActivity());
                            progressbar.setVisibility(View.VISIBLE);
                            trashlistview.setVisibility(View.VISIBLE);
                            searching.setVisibility(View.INVISIBLE);
                            ArrayList<ContactsAll> type_name_filter = new ArrayList<>();
                            String text = searchstring;
                            for (int i = 0; i < allfiles.size(); i++) {
                                if ((allfiles.get(i).getFilename().toLowerCase()).contains(text.toLowerCase())) {
                                    type_name_filter.add(allfiles.get(i));
                                }
                            }
                            if(type_name_filter.size() == 0){
                                notfound.setVisibility(View.VISIBLE);
                                pdfimage.setVisibility(View.VISIBLE);
                                trashlistview.setVisibility(View.INVISIBLE);
                            }
                            else{
                                pdfimage.setVisibility(View.INVISIBLE);
                                notfound.setVisibility(View.INVISIBLE);
                                trashlistview.setVisibility(View.VISIBLE);
                            }
                            progressbar.setVisibility(View.INVISIBLE);
                            listUpdate(type_name_filter);
                        }
                        return false;
                    }
                });
                searchedit.addTextChangedListener(new TextWatcher()
                {                                         //MAIN EDIT EDITTEXT
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }
                    @Override
                    public void afterTextChanged(Editable editable) {
                        searchedit.setImeActionLabel("Search",EditorInfo.IME_ACTION_UNSPECIFIED);
                        cancel.setVisibility(View.VISIBLE);
                        progressbar.setVisibility(View.INVISIBLE);
                        searchstring = editable.toString();
                    }
                });
            }
        });
        return root;
    }
    public void listUpdate(ArrayList<ContactsAll> data)
    {
        CustomAdapter3 customAdapter3 = new CustomAdapter3(getActivity(), data);
        trashlistview.setAdapter(customAdapter3);
        // listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, data));
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void showSoftKeyboard(View view){
        if(view.requestFocus()){
            InputMethodManager imm =(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
