package com.example.qrlink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

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

        EditText body = (EditText)findViewById(R.id.editTextTextPersonName);
        body.setText(result.getContents(), TextView.BufferType.NORMAL);
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

    private void createQRcode() {

        Bitmap qr = null;
        try {
            qr = createQRCodeByZxing("http://google.co.jp", 400);
        } catch (WriterException e) {
            Log.d("createQRcode", "error: ", e);
        }

        try {
            File root = Environment.getExternalStorageDirectory();

            // 日付でファイル名を作成　
            Date mDate = new Date();
            SimpleDateFormat fileName = new SimpleDateFormat("yyyyMMdd_HHmmss");

            // 保存処理開始
            FileOutputStream fos = null;
            fos = new FileOutputStream(new File(root, fileName.format(mDate) + ".jpg"));

            // jpegで保存
            qr.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            // 保存処理終了
            fos.close();
        } catch (Exception e) {
            Log.e("Error", "" + e.toString());
        }
    }

    public Bitmap createQRCodeByZxing(String contents,int size) throws WriterException {
        //QRコードをエンコードするクラス
        QRCodeWriter writer = new QRCodeWriter();

        //異なる型の値を入れるためgenericは使えない
        Hashtable encodeHint = new Hashtable();

        //日本語を扱うためにシフトJISを指定
        encodeHint.put(EncodeHintType.CHARACTER_SET, "shiftjis");

        //エラー修復レベルを指定
        //L 7%が復元可能
        //M 15%が復元可能
        //Q 25%が復元可能
        //H 30%が復元可能
        encodeHint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        BitMatrix qrCodeData = writer.encode(contents, BarcodeFormat.QR_CODE, size, size, encodeHint);

        //QRコードのbitmap画像を作成
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.argb(255, 255, 255, 255)); //いらないかも
        for (int x = 0; x < qrCodeData.getWidth(); x++) {
            for (int y = 0; y < qrCodeData.getHeight(); y++) {
                if (qrCodeData.get(x, y) == true) {
                    //0はBlack
                    bitmap.setPixel(x, y, Color.argb(255, 0, 0, 0));
                } else {
                    //-1はWhite
                    bitmap.setPixel(x, y, Color.argb(255, 255, 255, 255));
                }
            }
        }

        return bitmap;
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
        EditText body = (EditText)findViewById(R.id.editTextTextPersonName);
        copyToClipBoard(body.getText().toString());
    }

    // ブラウザで開くボタン
    public void openUrlButtonClick(View view) {
        EditText body = (EditText)findViewById(R.id.editTextTextPersonName);
        openUrlByText(body.getText().toString());
    }
}