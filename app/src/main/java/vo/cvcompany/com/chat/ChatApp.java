package vo.cvcompany.com.chat;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import vo.cvcompany.com.chat.View.RecyclerView.MyAdapterChat;
import vo.cvcompany.com.chat.View.RecyclerView.MyAdapterUser;

public class ChatApp extends AppCompatActivity {
    private static final String TAG =ChatApp.class.getSimpleName() ;
    private Socket mSocket;
    @BindView(R.id.etText)
    EditText etText;
    @BindView(R.id.recyclerViewUser)
    RecyclerView recyclerViewUser;
    @BindView(R.id.recyclerViewChat)
    RecyclerView recyclerViewChat;
    private ArrayList<String> arrayList ;
    private ArrayList<String> arrayListMessage ;
    private MyAdapterUser adapterUser;
    private MyAdapterChat adapterChat;
    Emitter.Listener serversendchatListener =new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
           runOnUiThread(new Runnable() {
               @Override
               public void run() {
                    JSONObject jsonObject= (JSONObject) args[0];
                   try {
                       arrayListMessage.add(jsonObject.getString("chat"));
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }finally {
                        adapterChat.notifyDataSetChanged();
                   }
               }
           });
        }
    };
    private Emitter.Listener lis = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                boolean check;
                @Override
                public void run() {
                    JSONObject onJsonObject = (JSONObject) args[0];
                    try {
                         check  =onJsonObject.getBoolean("send");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                   if(check){
                       Toast.makeText(ChatApp.this, "The user is existed", Toast.LENGTH_SHORT).show();

                   }else{
                       Toast.makeText(ChatApp.this, "success", Toast.LENGTH_SHORT).show();
                   }
                }
            });
        }
    };

    private Emitter.Listener resultListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    arrayList.clear();
                    try {
                        JSONArray array = object.getJSONArray("list");
                        for(int i =  0 ; i< array.length(); i++){
                            arrayList.add(array.getString(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }finally {
                        adapterUser.notifyDataSetChanged();
                    }
                }
            });


        }
    };
    private String text="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_app);
        ButterKnife.bind(this);


        try {
            mSocket = IO.socket("http://192.168.1.11:3000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.connect();

        if(savedInstanceState==null){
            arrayList  =new ArrayList<String>();
            arrayListMessage  =new ArrayList<String>();
        }else{
            arrayList = savedInstanceState.getStringArrayList("stringArrayListUser");
            arrayListMessage = savedInstanceState.getStringArrayList("stringArrayListMessage");
            text = savedInstanceState.getString("user");
            if (text!="")
            mSocket.emit("receive", text);
        }

        mSocket.on("send", lis);
        mSocket.on("ServerSendUser", resultListener);
        mSocket.on("ServerSendChat",serversendchatListener);

        adapterUser = new MyAdapterUser(this, arrayList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewUser.setLayoutManager(manager);
        recyclerViewUser.setAdapter(adapterUser);



        adapterChat = new MyAdapterChat(this, arrayListMessage);
        LinearLayoutManager manager1 = new LinearLayoutManager(this);
        manager1.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewChat.setLayoutManager(manager1);
        recyclerViewChat.setAdapter(adapterChat);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("stringArrayListUser", arrayList);
        outState.putStringArrayList("stringArrayListMessage", arrayListMessage);
        if (text!="")
        outState.putString("user", text);


    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @OnClick(R.id.imgSend)
    public void imgSend(){

        String text = etText.getText().toString().trim();
        if(text.length()>0) {
            mSocket.emit("chat", text);
        }

    }



    @OnClick(R.id.imgNewUser)
    public void imgNewUser(){
        text = etText.getText().toString().trim();
        if(text.length()>0) {
            mSocket.emit("receive", text);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        if (text!=null)
        mSocket.emit("deleteUser",text );
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(TAG, "onDetachedFromWindow: ");
    }
}
