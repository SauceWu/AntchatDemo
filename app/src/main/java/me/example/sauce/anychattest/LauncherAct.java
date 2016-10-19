package me.example.sauce.anychattest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Version ${versionName}
 * Created by sauce on 16/10/17.
 */

public class LauncherAct extends AppCompatActivity {

    @BindView(R.id.editText_userId)
    EditText editTextUserId;
    @BindView(R.id.editText_room)
    EditText editTextRoom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_launcher);
        ButterKnife.bind(this);
    }



    @OnClick({R.id.button, R.id.button2,R.id.button3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                String userId = editTextUserId.getText().toString();
                String room = editTextRoom.getText().toString();
                if (TextUtils.isEmpty(userId)) {
                    editTextUserId.setError("用户名不能为空");
                    return;
                }
                if (TextUtils.isEmpty(room)) {
                    editTextRoom.setError("房间号不能为空");
                    return;
                }
                MainActivity.start(this, userId, Integer.valueOf(room));
                break;
            case R.id.button2:
                startActivity(new Intent(this,ScanActivity.class));
                break;
            case R.id.button3:
                startActivity(new Intent(this,IntSigActivity.class));
                break;
        }
    }
}
