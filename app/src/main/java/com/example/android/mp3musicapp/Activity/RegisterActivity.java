package com.example.android.mp3musicapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mp3musicapp.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    EditText inputName, inputPassword, inputemail;
    Button buttonRegister, btnChuyen;
    TextView linklogin;
    private UserDataManager userDataManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputName = findViewById(R.id.txtName);
        inputemail = findViewById(R.id.txtEmail);
        inputPassword = findViewById(R.id.txtPwd);
        linklogin = findViewById(R.id.lnkLogin);
        buttonRegister = findViewById(R.id.btnregister);

        userDataManager = new UserDataManager(this);
        userDataManager.open();

        linklogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = inputName.getText().toString().trim();
                String email = inputemail.getText().toString().trim();
                String password = inputPassword.getText().toString();

                Log.d("RegisterActivity", "Bắt đầu đăng ký với: Tên=" + name + ", Email=" + email);

                if (name.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Tên không được trống!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (email.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Email không được trống!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu không được trống!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!emailValidator(email)) {
                    Toast.makeText(RegisterActivity.this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu phải ít nhất 6 kí tự!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("RegisterActivity", "Dữ liệu hợp lệ, kiểm tra email tồn tại.");
                if (userDataManager.checkUserExists(email)) {
                    Toast.makeText(RegisterActivity.this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
                    Log.d("RegisterActivity", "Email " + email + " đã tồn tại.");
                } else {
                    Log.d("RegisterActivity", "Email " + email + " chưa tồn tại, tiến hành đăng ký.");
                    long result = userDataManager.registerUser(name, email, password);
                    Log.d("RegisterActivity", "Kết quả đăng ký: " + result);
                    if (result != -1) {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                        Log.e("RegisterActivity", "Lỗi khi chèn dữ liệu người dùng vào cơ sở dữ liệu.");
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userDataManager.close();
    }

    public boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}