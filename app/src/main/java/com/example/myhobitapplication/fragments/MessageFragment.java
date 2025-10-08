package com.example.myhobitapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.adapters.MessagesAdapter;
import com.example.myhobitapplication.models.Message;
import com.example.myhobitapplication.viewModels.MessageViewModel;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    private MessagesAdapter adapter;
    private MessageViewModel viewModel;
    private RecyclerView recyclerView;
    private EditText messageInput;
    private ImageView sendButton;
    private String allianceId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_message, container, false);

        recyclerView = view.findViewById(R.id.recyclerChat);
        messageInput = view.findViewById(R.id.messageInput);
        sendButton = view.findViewById(R.id.Sendbutton);

        allianceId = getArguments().getString("ALLIANCE_ID");

       
        adapter = new MessagesAdapter(new ArrayList<>(), getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        viewModel = new ViewModelProvider(this).get(MessageViewModel.class);


        viewModel.listenForMessages(allianceId);


        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.updateMessages(messages); // samo update postojeći adapter
            recyclerView.scrollToPosition(messages.size() - 1);
        });



        sendButton.setOnClickListener(v -> {
            String text = messageInput.getText().toString().trim();
            if (!text.isEmpty()) {
                viewModel.sendMessage(allianceId, text);
                messageInput.setText("");
            }
        });

        return view;
    }
}
