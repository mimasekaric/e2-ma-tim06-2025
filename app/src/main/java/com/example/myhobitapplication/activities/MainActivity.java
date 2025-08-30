package com.example.myhobitapplication.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.adapters.UserListAdapter;
import com.example.myhobitapplication.models.UserTest;
import com.example.myhobitapplication.viewModels.UserViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private EditText etName, etSurname;
    private Button btnAddUser;
    private ListView lvUsers;
    private UserListAdapter userListAdapter;
    private UserViewModel userViewModel; // Dodajemo ViewModel

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicijalizacija UI elemenata
        etName = findViewById(R.id.et_name);
        etSurname = findViewById(R.id.et_surname);
        btnAddUser = findViewById(R.id.btn_add_user);
        lvUsers = findViewById(R.id.lv_users);

        // Inicijalizacija ViewModel-a
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Inicijalizacija adaptera i postavljanje na ListView
        // Adapter se inicijalizuje sa praznom listom
        userListAdapter = new UserListAdapter(this, new ArrayList<UserTest>());
        lvUsers.setAdapter(userListAdapter);

        // Posmatranje LiveData-e
        // Ovaj kod se automatski izvršava svaki put kada se LiveData promeni
        userViewModel.getUsers().observe(this, userList -> {
            userListAdapter.clear(); // Brišemo staru listu iz adaptera
            userListAdapter.addAll(userList); // Dodajemo novu
            userListAdapter.notifyDataSetChanged(); // Obaveštavamo adapter o promeni
        });

        // Listener za dugme 'Dodaj korisnika'
        btnAddUser.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String surname = etSurname.getText().toString().trim();

            if (name.isEmpty() || surname.isEmpty()) {
                Toast.makeText(this, "Ime i prezime ne mogu biti prazni!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Pozivamo metodu u ViewModel-u, a ne direktno u bazi
            userViewModel.addUser(name, surname);

            etName.setText("");
            etSurname.setText("");
        });
    }
}
