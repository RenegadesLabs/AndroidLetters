package com.renegades.labs.androidletters;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import me.pinxter.letters.Letters;

public class MainActivity extends AppCompatActivity {

    List<MyContact> contactList;
    MyRecyclerViewAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS},
                1);

        int permissionReadContacts = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);

        if (permissionReadContacts != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    1);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    contactList = new ArrayList<>();
                    final RecyclerView contactRecyclerView = (RecyclerView) findViewById(R.id.contact_list);
                    LinearLayoutManager llm = new LinearLayoutManager(this);
                    contactRecyclerView.setLayoutManager(llm);

                    ContentResolver cr = getContentResolver();
                    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                            null, null, null, null);

                    if (cur.getCount() > 0) {

                        while (cur.moveToNext()) {
                            MyContact myContact = new MyContact();
                            String id = cur.getString(
                                    cur.getColumnIndex(ContactsContract.Contacts._ID));
                            String name = cur.getString(cur.getColumnIndex(
                                    ContactsContract.Contacts.DISPLAY_NAME));

                            if (cur.getInt(cur.getColumnIndex(
                                    ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                                Cursor pCur = cr.query(
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                        new String[]{id}, null);

                                pCur.moveToNext();
                                String phoneNo = pCur.getString(pCur.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER));

                                myContact.setName(name);
                                myContact.setPhone(phoneNo);
                                contactList.add(myContact);
                                pCur.close();
                            }
                        }
                    }
                    cur.close();


                    Collections.sort(contactList, new Comparator<MyContact>() {
                        @Override
                        public int compare(MyContact o1, MyContact o2) {
                            Collator collator = Collator.getInstance(Locale.getDefault());
                            collator.setStrength(Collator.PRIMARY);
                            return collator.compare(o1.getName(), o2.getName());
                        }
                    });

                    Letters letters = new Letters(this, "name", (List) new ArrayList<>(contactList));
                    letters.setOnSelect(new Letters.OnSelect() {
                        @Override
                        public void onSelect(int index, String letter) {
                            contactRecyclerView.getLayoutManager().scrollToPosition(index);
                        }
                    });
                    ((FrameLayout) findViewById(R.id.letters)).removeAllViews();
                    ((FrameLayout) findViewById(R.id.letters)).addView(letters.getLetterLayout());

                    myAdapter = new MyRecyclerViewAdapter(contactList, letters);
                    contactRecyclerView.setAdapter(myAdapter);

                    myAdapter.notifyDataSetChanged();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

}
