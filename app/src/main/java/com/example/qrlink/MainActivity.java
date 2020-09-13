package com.example.qrlink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    // エントリーポイント
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // QRコードのコールバック関数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if(result.getContents() == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

        TextView textView = (TextView) findViewById(R.id.text_view);
        textView.setText(result.getContents());

    }

    // クリップボードにコピー
    public void copyToClipBoard(String copy_txt) {
        // クリップボードに格納するItemを作成
        ClipData.Item item = new ClipData.Item(copy_txt);

        // MIMETYPEの作成
        String[] mimeType = new String[1];
        mimeType[0] = ClipDescription.MIMETYPE_TEXT_URILIST;

        // クリップボードに格納するClipDataオブジェクトの作成
        ClipData cd = new ClipData(new ClipDescription("copy_text", mimeType), item);

        // クリップボードにデータを格納
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.setPrimaryClip(cd);

        // Toast表示
        Toast.makeText(this, "Copy to Clip: " + copy_txt, Toast.LENGTH_LONG).show();
    }

    // URLを開く
    public void openUrlByText(String url_txt) {
        Uri uri = Uri.parse(url_txt);
        Intent i = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(i);
    }

    // 読み取りボタン
    public void scanBarcode(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureCodeActivity.class);
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.initiateScan();
    }

    // コピーボタン
    public void copyButtonClick(View view) {
        TextView textView = (TextView) findViewById(R.id.text_view);
        copyToClipBoard(textView.getText().toString());
    }

    public void openUrlButtonClick(View view) {
        TextView textView = (TextView) findViewById(R.id.text_view);
        openUrlByText(textView.getText().toString());
    }
}