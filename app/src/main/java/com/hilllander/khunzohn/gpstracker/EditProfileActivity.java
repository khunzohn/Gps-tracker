package com.hilllander.khunzohn.gpstracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hilllander.khunzohn.gpstracker.database.dao.DeviceDao;
import com.hilllander.khunzohn.gpstracker.database.model.Device;
import com.hilllander.khunzohn.gpstracker.database.table.DeviceTable;
import com.hilllander.khunzohn.gpstracker.util.BitmapUtil;
import com.hilllander.khunzohn.gpstracker.util.Logger;
import com.hilllander.khunzohn.gpstracker.util.ViewUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import de.hdodenhof.circleimageview.CircleImageView;
import mm.technomation.mmtext.MMButtonView;
import mm.technomation.mmtext.MMTextView;

/**
 *Created by khunzohn on 1/6/16.
 */
public class EditProfileActivity extends AppCompatActivity {
    public static final String KEY_TITLE = "key for title";
    public static final String TITLE_CHANGE_NAME = "Change name";
    public static final String TITLE_CHANGE_TYPE = "Change type";
    public static final String KEY_DEVICE = "key for device";
    public static final String KEY_INFO_EDITED = "key for info edited";
    public static final String KEY_RETURNED_DEVICE = "key_returned_from_edit_sim_nuber";
    private static final String TAG = Logger.generateTag(EditProfileActivity.class);
    private static final int REQUEST_EDIT_NAME = 456;
    private static final int REQUEST_EDIT_TYPE = 421;
    private static final int REQUEST_CAMERA = 789;
    private static final int REQUEST_GALLERY = 968;
    private static final int REQUEST_EDIT_SIM = 4521;
    private static final int REQUEST_EDIT_AUTHORIZATION = 333;
    private TextView tvNameValue;
    private ImageButton ibEditName;
    private TextView tvTypeValue;
    private ImageButton ibEditType;
    private MMTextView tvSimnumberValue;
    private ImageButton ibEditSimNumber;
    private MMTextView tvAuthorizationValue;
    private ImageButton ibEditAuthorization;
    private ImageButton deleteDevice;
    private CircleImageView ibProfile;
    private Device device;
    private boolean infoEdited = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ViewUtils.setStatusBarTint(this, R.color.indigo_700);
        tvNameValue = (TextView) findViewById(R.id.tvNameValue);
        tvTypeValue = (TextView) findViewById(R.id.tvTypeValue);
        tvSimnumberValue = (MMTextView) findViewById(R.id.tvSimnumberValue);
        tvAuthorizationValue = (MMTextView) findViewById(R.id.tvAuthorizationValue);
        device = new Device();
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            device = (Device) bundle.getSerializable(MainActivity.KEY_DEVICE);
            if (null != device) {
                setValue(device);
            }
        } else {
            Logger.e(TAG, "device is null!");
        }

        ibEditName = (ImageButton) findViewById(R.id.ibEditName);
        ibEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editName = new Intent(EditProfileActivity.this, EditBasicInfoActivity.class);
                editName.putExtra(KEY_DEVICE, device);
                editName.putExtra(KEY_TITLE, TITLE_CHANGE_NAME);
                startActivityForResult(editName, REQUEST_EDIT_NAME);
            }
        });

        ibEditType = (ImageButton) findViewById(R.id.ibEditType);
        ibEditType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editName = new Intent(EditProfileActivity.this, EditBasicInfoActivity.class);
                editName.putExtra(KEY_DEVICE, device);
                editName.putExtra(KEY_TITLE, TITLE_CHANGE_TYPE);
                startActivityForResult(editName, REQUEST_EDIT_TYPE);
            }
        });
        ibEditSimNumber = (ImageButton) findViewById(R.id.ibEditSimNumber);
        ibEditSimNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sim = new Intent(EditProfileActivity.this, EditSimNumActivity.class);
                sim.putExtra(KEY_DEVICE, device);
                startActivityForResult(sim, REQUEST_EDIT_SIM);
            }
        });
        ibEditAuthorization = (ImageButton) findViewById(R.id.ibEditAuthorization);
        ibEditAuthorization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authorization = new Intent(EditProfileActivity.this, EditAuthorizationActivity.class);
                authorization.putExtra(KEY_DEVICE, device);
                startActivityForResult(authorization, REQUEST_EDIT_AUTHORIZATION);
            }
        });

        ibProfile = (CircleImageView) findViewById(R.id.ibProfile);
        if (null == device.getPhotoUrl() || device.getPhotoUrl().equals(DeviceTable.DEFAULT_PHOTO_URL)) {
            ibProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));

        } else {
            String photoUrl = device.getPhotoUrl();
            Bitmap bm = BitmapUtil.scaledBitmap(photoUrl);
            if (null != bm) {
                ibProfile.setImageBitmap(bm);
            } else {
                Logger.e(TAG, "Can't decode bitmap from file path");
                ibProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
            }
        }
        ibProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseProfile();
            }
        });
        final MMButtonView fine = (MMButtonView) findViewById(R.id.fine);
        fine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        deleteDevice = (ImageButton) findViewById(R.id.deleteDevice);
        deleteDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDeviceFromDb(device);
            }
        });
    }

    private void chooseProfile() {
        final RadioGroup group = new RadioGroup(this);
        final RadioButton camera = new RadioButton(this);
        camera.setText("Camera");
        RadioButton gallary = new RadioButton(this);
        gallary.setText("Gallery");
        group.addView(camera);
        group.addView(gallary);
        group.check(camera.getId());
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Choose photo")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int checkedId = group.getCheckedRadioButtonId();
                        if (camera.getId() == checkedId) {
                            openCamera();
                        } else {
                            openGallery();
                        }
                    }
                }).setView(group, 16, 16, 16, 16)
                .create();
        dialog.show();
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        startActivityForResult(Intent.createChooser(gallery, "Choose photo"), REQUEST_GALLERY);
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, REQUEST_CAMERA);
    }

    private void deleteDeviceFromDb(final Device deviceTobeDeleted) {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Confirm to delete")
                .setMessage("Are your sure you want to permanently delete this device?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteDeviceFromDb delete = new DeleteDeviceFromDb(EditProfileActivity.this);
                        delete.execute(deviceTobeDeleted);
                    }
                })
                .create();
        dialog.show();

    }

    private void setValue(Device device) {
        tvNameValue.setText(device.getDeviceName());
        tvTypeValue.setText(device.getDeviceType());
        if (device.getAuthorization().equals(Device.UN_AUTHORIZED)) {
            tvAuthorizationValue.setMyanmarText(this.getString(R.string.label_toggle_action_un_authorized));
        } else {
            tvAuthorizationValue.setMyanmarText(this.getString(R.string.label_toggle_action_authorized));
        }
        tvSimnumberValue.setMyanmarText(device.getSimNumber());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            Bundle bundle = data.getExtras();
            if (REQUEST_EDIT_NAME == requestCode || REQUEST_EDIT_TYPE == requestCode ||
                    REQUEST_EDIT_SIM == requestCode || REQUEST_EDIT_AUTHORIZATION == requestCode) {

                if (null != bundle) {
                    device = (Device) bundle.getSerializable(KEY_RETURNED_DEVICE);
                    if (null != device)
                        setValue(device);
                    infoEdited = true;
                }
            } else if (REQUEST_CAMERA == requestCode) {
                if (null != bundle) {
                    final Bitmap raw = (Bitmap) bundle.get("data");
                    if (null != raw) {
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        raw.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                System.currentTimeMillis() + ".jpg");
                        FileOutputStream out;
                        try {
                            out = new FileOutputStream(path);
                            out.write(bytes.toByteArray());
                            out.close();
                        } catch (IOException e) {
                            Logger.e(TAG, e.getLocalizedMessage());
                        }
                        ibProfile.setImageBitmap(raw);
                        saveUrlToDb(path.getPath());
                    }
                }
            } else if (REQUEST_GALLERY == requestCode) {
                if (null != bundle) {
                    Uri uri = data.getData();
                    String projections[] = {MediaStore.MediaColumns.DATA};
                    CursorLoader loader = new CursorLoader(this, uri, projections, null, null, null);
                    Cursor cursor = loader.loadInBackground();
                    int dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    String urlPath = cursor.getString(dataColumnIndex);

                    Bitmap bm = BitmapUtil.scaledBitmap(urlPath);

                    ibProfile.setImageBitmap(bm);
                    saveUrlToDb(urlPath);
                }
            }
        }
    }

    private void saveUrlToDb(String url) {
        device.setPhotoUrl(url);
        SavePhotoUrlToDb saveUrl = new SavePhotoUrlToDb(this);
        saveUrl.execute(device);
    }

    @Override
    public void onBackPressed() {
        if (infoEdited) {
            Intent returnedIntent = new Intent();
            returnedIntent.putExtra(KEY_INFO_EDITED, infoEdited);
            setResult(RESULT_OK, returnedIntent);
        }
        super.onBackPressed();
    }

    private class DeleteDeviceFromDb extends AsyncTask<Device, Void, Boolean> {
        private DeviceDao dao;

        public DeleteDeviceFromDb(Context context) {
            dao = new DeviceDao(context);
        }

        @Override
        protected void onPreExecute() {
            try {
                dao.open();
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
        }

        @Override
        protected Boolean doInBackground(Device... params) {
            Device device = params[0];
            Boolean deleted = false;
            try {
                deleted = dao.deleteDevice(String.valueOf(device.getId()));
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
            return deleted;
        }

        @Override
        protected void onPostExecute(Boolean deleted) {
            try {
                dao.close();
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
            if (deleted) {
                Toast.makeText(EditProfileActivity.this, "Deleted successfully!", Toast.LENGTH_SHORT).show();
                infoEdited = true;
                onBackPressed();
            } else {
                Toast.makeText(EditProfileActivity.this, "Something went wrong deleting this device!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class SavePhotoUrlToDb extends AsyncTask<Device, Void, Device> {
        private DeviceDao dao;

        public SavePhotoUrlToDb(Context context) {
            dao = new DeviceDao(context);
        }

        @Override
        protected void onPreExecute() {
            try {
                dao.open();
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
        }

        @Override
        protected Device doInBackground(Device... params) {
            Device device = params[0];
            Device deviceUpdated = new Device();
            try {
                deviceUpdated = dao.updateDevice(device);
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
            return deviceUpdated;
        }

        @Override
        protected void onPostExecute(Device deviceUpdated) {
            try {
                dao.close();
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
            if (null != deviceUpdated) {
                Logger.d(TAG, "Photo url changed successfully!");
                infoEdited = true;
            }
        }
    }

}
