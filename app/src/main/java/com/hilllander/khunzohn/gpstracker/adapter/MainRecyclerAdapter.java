package com.hilllander.khunzohn.gpstracker.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hilllander.khunzohn.gpstracker.R;
import com.hilllander.khunzohn.gpstracker.database.model.Device;
import com.hilllander.khunzohn.gpstracker.database.table.DeviceTable;
import com.hilllander.khunzohn.gpstracker.util.BitmapUtil;
import com.hilllander.khunzohn.gpstracker.util.Logger;
import com.hilllander.khunzohn.gpstracker.util.USSD;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import mm.technomation.mmtext.MMTextView;

/**
 *Created by khunzohn on 1/2/16.
 * modified by khunzohn on 1/6/16.(add profile Bitmap)
 */
public class MainRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = Logger.generateTag(MainRecyclerAdapter.class);
    private List<Device> devices;
    private Context context;
    private OnDeviceOnClickListener onClickListener;

    public MainRecyclerAdapter(Context context) {
        this.context = context;
        devices = new ArrayList<>();
        Logger.d(TAG, "contructor triggered");
    }

    public void setDevices(List<Device> mDevices) {
        this.devices = mDevices;
        notifyDataSetChanged();
    }

    public void setOnClickListener(OnDeviceOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gps_device, parent, false);
        Logger.d(TAG, "onCreateViewHolder triggered");
        return new MainRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Device device = devices.get(position);
        Logger.logDevice(TAG, device);
        ((MainRecyclerViewHolder) holder).deviceName.setText(device.getDeviceName());
        ((MainRecyclerViewHolder) holder).simNumber.setText(device.getSimNumber());
        if (device.getPassword().equals(USSD.DEAFULT_PASSWORD)) {
            ((MainRecyclerViewHolder) holder).ibLock.setBackgroundResource(R.drawable.ic_lock_open_white_24dp);
        } else {
            ((MainRecyclerViewHolder) holder).ibLock.setBackgroundResource(R.drawable.ic_lock_white_24dp);
        }
        if (device.getAuthorization().equals(Device.AUTHORIZED)) {
            ((MainRecyclerViewHolder) holder).authorization.setMyanmarText(context.getString(R.string.label_toggle_action_authorized));
        } else {
            ((MainRecyclerViewHolder) holder).authorization.setMyanmarText(context.getString(R.string.label_toggle_action_un_authorized));
        }
        ((MainRecyclerViewHolder) holder).tvLatValue.setText(String.valueOf(device.getLatitude()));
        ((MainRecyclerViewHolder) holder).tvLongValue.setText(String.valueOf(device.getLongitude()));
        String dateTime = device.getTrackedDate() + " " + device.getTrackedTime();
        ((MainRecyclerViewHolder) holder).tvDateTime.setText(dateTime);
            /*edit onclick*/
        ((MainRecyclerViewHolder) holder).ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClickEdit(device);
            }
        });
            /*lock onclick*/
        ((MainRecyclerViewHolder) holder).ibLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClickLock(device);
            }
        });
            /*go to map onclick*/
        ((MainRecyclerViewHolder) holder).tvGoToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClickGoToMap(device);
            }
        });
            /*profile pic onclick*/
        ((MainRecyclerViewHolder) holder).deviceProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClickProfile(device);
            }
        });
        if (device.getPhotoUrl().equals(DeviceTable.DEFAULT_PHOTO_URL)) {
            ((MainRecyclerViewHolder) holder).deviceProfile.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_white_24dp));
        } else {
            String url = device.getPhotoUrl();
            Bitmap bm = BitmapUtil.scaledBitmap(url);
            if (null != bm) {
                ((MainRecyclerViewHolder) holder).deviceProfile.setImageBitmap(bm);
            } else {
                Logger.e(TAG, "Can't decode bitmap from file path!");
                ((MainRecyclerViewHolder) holder).deviceProfile.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_white_24dp));
            }
        }

    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public interface OnDeviceOnClickListener {
        void onClickEdit(Device device);

        void onClickLock(Device device);

        void onClickProfile(Device device);

        void onClickGoToMap(Device device);
    }

    class MainRecyclerViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView deviceProfile;
        private MMTextView authorization, tvGoToMap;
        private TextView deviceName, simNumber, tvLatValue, tvLongValue, tvDateTime;
        private ImageButton ibLock, ibEdit;

        public MainRecyclerViewHolder(View v) {
            super(v);
            Logger.d(TAG, "ViewHolder constructor triggered ");
            deviceProfile = (CircleImageView) v.findViewById(R.id.deviceProfile);
            deviceName = (TextView) v.findViewById(R.id.deviceName);
            authorization = (MMTextView) v.findViewById(R.id.authorization);
            tvGoToMap = (MMTextView) v.findViewById(R.id.tvGoToMap);
            simNumber = (TextView) v.findViewById(R.id.simNumber);
            tvLatValue = (TextView) v.findViewById(R.id.tvLatValue);
            tvLongValue = (TextView) v.findViewById(R.id.tvLongValue);
            tvDateTime = (TextView) v.findViewById(R.id.tvDateTime);
            ibLock = (ImageButton) v.findViewById(R.id.ibLock);
            ibEdit = (ImageButton) v.findViewById(R.id.ibEdit);
        }
    }
}
