package com.example.roomdatabaseandroidtutorial;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.roomdatabaseandroidtutorial.database.UserDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_RESULT_CODE = 10;
    private EditText edtUsername;
    private EditText edtAddress;
    private Button btnAddUser;
    private RecyclerView rcvUser;

    private UserAdapter userAdapter;
    private List<User> mListUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        userAdapter = new UserAdapter(new UserAdapter.IClickItemUser() {
            @Override
            public void updateUser(User user) {
                clickUpdateUser(user);
            }
        });
        mListUser = new ArrayList<>();
        userAdapter.setData(mListUser);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvUser.setLayoutManager(linearLayoutManager);

        rcvUser.setAdapter(userAdapter);

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });

        loadData();
    }

    private void initUI() {
        edtUsername = findViewById(R.id.edt_username);
        edtAddress = findViewById(R.id.edt_address);
        btnAddUser = findViewById(R.id.btn_add_user);
        rcvUser = findViewById(R.id.rcv_user);
    }

    private void addUser() {
        String strUsername = edtUsername.getText().toString().trim();
        String strAddress = edtAddress.getText().toString().trim();

        if ((TextUtils.isEmpty(strUsername)) || TextUtils.isEmpty(strAddress)) {
            Toast.makeText(this, "Add user unsuccessfully", Toast.LENGTH_SHORT).show();
            return;
        } else {
            User user = new User(strUsername, strAddress);

            if (isUSerExist(user)) {
                Toast.makeText(this, "User exist", Toast.LENGTH_SHORT).show();
                return;
            }

            UserDatabase.getInstance(this).userDAO().insertUser(user);
            Toast.makeText(this, "Add user successfully", Toast.LENGTH_SHORT).show();

            edtUsername.setText("");
            edtAddress.setText("");

            hideSoftKeyBoard();

            loadData();
        }
    }

    public void hideSoftKeyBoard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException exception) {
            exception.printStackTrace();
        }
    }

    private void loadData() {
        mListUser = UserDatabase.getInstance(this).userDAO().getListUser();
        userAdapter.setData(mListUser);
    }

    public boolean isUSerExist(User user) {
        List<User> list = UserDatabase.getInstance(this).userDAO().checkUser(user.getName());
        return list != null && !list.isEmpty();
    }

    private void clickUpdateUser(User user) {
        Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_user", user);
        intent.putExtras(bundle);
        startActivityForResult(intent, MY_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == MY_RESULT_CODE) && (resultCode == Activity.RESULT_OK)){
            loadData();
        }
    }
}