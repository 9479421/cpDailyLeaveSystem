package vip.wqby.cpdailyleave;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AlertDialog.Builder(this)
                .setTitle("更新内容")
                .setMessage("本次3.0更新内容：\n1、固定姓名\n2、修复审核时间超前的BUG\n3、采取新模式授权，目前暂时免费对所有人开放使用")
                .setIcon(R.mipmap.ic_logo)
                .create().show();



    }
}