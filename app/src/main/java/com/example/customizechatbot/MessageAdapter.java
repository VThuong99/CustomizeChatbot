package com.example.customizechatbot;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import androidx.core.content.ContextCompat;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private ArrayList<Message> messages;

    public MessageAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessageContent;
        LinearLayout messageLayoutContainer;

        MessageViewHolder(View itemView) {
            super(itemView);
            tvMessageContent = itemView.findViewById(R.id.tvMessageContent);
            messageLayoutContainer = itemView.findViewById(R.id.messageLayoutContainer);
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.tvMessageContent.setText(message.getContent());

        if (message.isUser()) {
            holder.messageLayoutContainer.setGravity(Gravity.END);
            holder.tvMessageContent.setBackgroundResource(R.drawable.message_background_user);
            holder.tvMessageContent.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.black));

        } else {
            holder.messageLayoutContainer.setGravity(Gravity.START);
            holder.tvMessageContent.setBackgroundResource(R.drawable.message_background_bot);
            holder.tvMessageContent.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}