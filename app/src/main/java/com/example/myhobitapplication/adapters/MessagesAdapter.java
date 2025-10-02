package com.example.myhobitapplication.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.models.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int TYPE_MY_MESSAGE = 1;
    private static final int TYPE_OTHER_MESSAGE = 2;

    private List<Message> messageList;
    private Context context;

    public MessagesAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("MessagesAdapter", "message senderId: " + message.getSenderId() + ", currentUid: " + currentUid);
        if(message.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            return TYPE_MY_MESSAGE;
        } else {
            return TYPE_OTHER_MESSAGE;
        }
    }

    public void updateMessages(List<Message> messages) {
        messageList.clear();
        messageList.addAll(messages);
        notifyDataSetChanged(); // ovo će pozvati getItemViewType za sve stavke
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_MY_MESSAGE){
            View view = LayoutInflater.from(context).inflate(R.layout.message_right_item, parent, false);
            return new MyMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.message_left_item, parent, false);
            return new OtherMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if(holder instanceof MyMessageViewHolder){
            ((MyMessageViewHolder) holder).bind(message);
        } else if(holder instanceof OtherMessageViewHolder){
            ((OtherMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MyMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        public MyMessageViewHolder( View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textMessage);
            itemView.findViewById(R.id.textMessage);
        }
        void bind(Message message){
            messageText.setText(message.getText());
        }
    }

    static class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView senderName;
        public OtherMessageViewHolder( View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textMessage);
            senderName = itemView.findViewById(R.id.textSender);
        }
        void bind(Message message){
            messageText.setText(message.getText()); senderName.setText(message.getSenderName() + ":");
        }
    }

    public void addMessage(Message message){
        messageList.add(message);
        notifyItemInserted(messageList.size()-1);
    }
}
