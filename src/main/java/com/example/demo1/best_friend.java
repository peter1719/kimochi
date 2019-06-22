package com.example.demo1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import static com.example.demo1.DisplayMessageActivity.EXTRA_MESSAGE;

public class best_friend extends AppCompatActivity {

    String[] bf = {"Amy", "Tonny", "Betty", "Panda", "Jack"};
    //String[] bf = {"Pupu", "Tonny", "Betty", "Panda", "Jack"};
    //String user_name = "Amy";
    String user_name = "Pupu";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_friend);

        ListView list = (ListView) findViewById(R.id.list_of_friends);
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,bf);
        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView list = (ListView) parent;

                String friend_name = list.getItemAtPosition(position).toString();
                go_chatroom(friend_name);



            }
        });
    }

    public void go_chatroom(String friend_name){
        Intent intent = new Intent(this, chatroom.class);
        String[] name = {user_name, friend_name};
        intent.putExtra(EXTRA_MESSAGE, name);
        startActivity(intent);
    }

    public void mode2(View view){

//        Intent intent = new Intent(this, DisplayMessageActivity.class);
//        // EditText editText = (EditText) findViewById(R.id.editText);
//        //  String message = editText.getText().toString();
//        // intent.putExtra(EXTRA_MESSAGE, message);
//        startActivity(intent);
        this.finish();
    }

}
