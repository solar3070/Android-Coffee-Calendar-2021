package ddwu.mobile.finalproject.ma01_20170580;

import static android.os.Environment.getExternalStoragePublicDirectory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddActivity extends AppCompatActivity {

    final String TAG = "AddActivity";
    final int MAP_CODE = 100;
    final int REQUEST_TAKE_PHOTO = 200;

    EditText etDate;
    EditText etCafe;
    EditText etAddress;
    EditText etMenu;
    EditText etMemo;

    DBManager dbManager;

    ImageView imageView;
    String currentPhotoPath;
    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etDate = findViewById(R.id.etAddDate);
        etCafe = findViewById(R.id.etAddCafe);
        etAddress = findViewById(R.id.etAddAddress);
        etMenu = findViewById(R.id.etAddMenu);
        etMemo = findViewById(R.id.etAddMemo);

        Intent intent = getIntent();
        String date = intent.getStringExtra("date");

        if (date != null) {
            etDate.setText(intent.getStringExtra("date"));
        } else {
            long now = System.currentTimeMillis();
            etDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(new Date(now)));
        }

        dbManager = new DBManager(this);

        imageView = (ImageView) findViewById(R.id.btnCapture);
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

        Log.i(TAG, getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        Log.i(TAG, getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath());
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
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
                    boolean result = dbManager.addNewRecord(new Record(
                            null,
                            etDate.getText().toString(),
                            etCafe.getText().toString(),
                            etAddress.getText().toString(),
                            etMenu.getText().toString(),
                            etMemo.getText().toString(),
                            currentPhotoPath
                    ));

                    if (result) {
                        Log.d(TAG, "DB에 잘 추가되었음");

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("date", etDate.getText().toString());
                        resultIntent.putExtra("cafe", etCafe.getText().toString());
                        resultIntent.putExtra("address", etAddress.getText().toString());
                        resultIntent.putExtra("menu", etMenu.getText().toString());
                        resultIntent.putExtra("memo", etMemo.getText().toString());
                        resultIntent.putExtra("path", currentPhotoPath);
                        setResult(RESULT_OK, resultIntent);
                    } else {
                        Toast.makeText(this, "새로운 기록 추가 실패!", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
                break;
            case R.id.btnAddCancel:
                if (photoFile != null) {
                    photoFile.delete();
                }
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.icMap:
                Intent intent = new Intent(AddActivity.this, MapActivity.class);
                startActivityForResult(intent, MAP_CODE);
        }
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
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();
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
}