package ddwu.mobile.finalproject.ma01_20170580;

import static java.lang.Integer.parseInt;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateActivity extends AppCompatActivity {

    final String TAG = "UpdateActivity";
    final int MAP_CODE = 100;
    final int REQUEST_TAKE_PHOTO = 200;

    Record record;

    EditText etDate;
    EditText etCafe;
    EditText etAddress;
    EditText etMenu;
    EditText etMemo;

    DBManager dbManager;

    ImageView imageView;
    String mCurrentPhotoPath;
    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        imageView = (ImageView) findViewById(R.id.btnUpdateCapture);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    dispatchTakePictureIntent();
                    return true;
                }
                return false;
            }
        });
        dbManager = new DBManager(this);

        record = (Record) getIntent().getSerializableExtra("record");

        etDate = findViewById(R.id.etUpdateDate);
        etCafe = findViewById(R.id.etUpdateCafe);
        etAddress = findViewById(R.id.etUpdateAddress);
        etMenu = findViewById(R.id.etUpdateMenu);
        etMemo = findViewById(R.id.etUpdateMemo);

        etDate.setText(record.getDate());
        etCafe.setText(record.getCafe());
        etAddress.setText(record.getAddress());
        etMenu.setText(record.getMenu());
        etMemo.setText(record.getMemo());
        mCurrentPhotoPath = record.getPath();
        getPic();
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnUpdate:
                if (etDate.getText().toString().length() == 0) {
                    Toast.makeText(this,"날짜를 입력해주세요!", Toast.LENGTH_LONG).show();
                } else if (etCafe.getText().toString().length() == 0) {
                    Toast.makeText(this,"카페를 입력해주세요!", Toast.LENGTH_LONG).show();
                } else if (etAddress.getText().toString().length() == 0) {
                    Toast.makeText(this,"주소를 입력해주세요!", Toast.LENGTH_LONG).show();
                } else if (etMenu.getText().toString().length() == 0) {
                    Toast.makeText(this,"메뉴를 입력해주세요!", Toast.LENGTH_LONG).show();
                } else if (etMemo.getText().toString().length() == 0) {
                    Toast.makeText(this,"한줄평을 입력해주세요!", Toast.LENGTH_LONG).show();
                } else {
                    record.setDate(etDate.getText().toString());
                    record.setCafe(etCafe.getText().toString());
                    record.setAddress(etAddress.getText().toString());
                    record.setMenu(etMenu.getText().toString());
                    record.setMemo(etMemo.getText().toString());
                    record.setPath(mCurrentPhotoPath);

                    if (dbManager.updateRecord(record)) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("record", record);
                        setResult(RESULT_OK, resultIntent);
                    } else {
                        setResult(RESULT_CANCELED);
                    }
                    finish();
                }
                break;
            case R.id.btnUpdateCancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.icMapUpdate:
                Intent intent = new Intent(UpdateActivity.this, MapActivity.class);
                startActivityForResult(intent, MAP_CODE);
        }
    }

    private void getPic() {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        bmOptions.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        "ddwu.mobile.finalproject.ma01_20170580.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }

    }

    private void setPic() {
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAP_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    etCafe.setText(data.getStringExtra("cafe"));
                    etAddress.setText(data.getStringExtra("address"));
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(this, getString(R.string.notSelectMarker), Toast.LENGTH_SHORT).show();
                    break;
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case R.id.shareTwitter:
                shareTwitter();
                break;
        }
        return true;
    }

    private void shareTwitter() {
        try {
            String txt = "마이 커피 다이어리" + "\n날짜: " + etDate.getText().toString()
                    + "\n카페: " + etCafe.getText().toString() + "\n주소: " + etAddress.getText().toString()
                    + "\n메뉴: " + etMenu.getText().toString() + "\n메모: " + etMemo.getText().toString();
            String sharedText = String.format("http://twitter.com/intent/tweet?text=%s",
                    URLEncoder.encode(txt, "utf-8"));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharedText));
            startActivity(intent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}