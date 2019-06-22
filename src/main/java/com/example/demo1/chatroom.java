package com.example.demo1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;

public class chatroom extends AppCompatActivity {
    String[] name = {};
    Button fab;
    String usr_name;
    String friend_name;
    String ref = "";
    String[] ref_name = {"Amy", "Tonny", "Betty", "Panda", "Jack"};
    //String[] ref_name = {"Pupu", "Tonny", "Betty", "Panda", "Jack"};
    FirebaseListAdapter<ChatMessage> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);



        //get friend_name and user name
        Intent intent = getIntent();
        name = intent.getStringArrayExtra(DisplayMessageActivity.EXTRA_MESSAGE);
        usr_name = name[0];
        friend_name = name[1];

        //decide file_name
        for(int i = 0; i < 5; i++){
            if(ref_name[i].equals(friend_name))
                ref = Integer.toString(i);
        }

        //displaymessage
        displayChatMessages();

        //for button
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference(ref)
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                usr_name)
                        );
                // Clear the input
                input.setText("");
            }
        });

    }

    private void displayChatMessages() {
        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference(ref)) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);
                TextView messageTime1 = (TextView)v.findViewById(R.id.message_time1);
                TextView messagefriend = (TextView)v.findViewById(R.id.message_friend);

                // Set their text
                if(friend_name.equals(model.getMessageUser())) {
                    messageUser.setVisibility(View.VISIBLE);
                    messageText.setVisibility(View.VISIBLE);
                    messageTime1.setVisibility(View.VISIBLE);

                    messagefriend.setVisibility(View.INVISIBLE);
                    messageTime.setVisibility(View.INVISIBLE);

                    messageText.setText(model.getMessageText());
                    messageUser.setText(model.getMessageUser());

                    // Format the date before showing it
                    messageTime1.setText(DateFormat.format( "(HH:mm:ss)",
                            model.getMessageTime()));
                }
                else{
                    messageUser.setVisibility(View.INVISIBLE);
                    messageText.setVisibility(View.INVISIBLE);
                    messageTime1.setVisibility(View.INVISIBLE);

                    messageTime.setVisibility(View.VISIBLE);
                    messagefriend.setVisibility(View.VISIBLE);

                    messagefriend.setText(model.getMessageText());

                    // Format the date before showing it
                    messageTime.setText(DateFormat.format("(HH:mm:ss)",
                            model.getMessageTime()));

                }
            }
        };

        listOfMessages.setAdapter(adapter);
    }

}
