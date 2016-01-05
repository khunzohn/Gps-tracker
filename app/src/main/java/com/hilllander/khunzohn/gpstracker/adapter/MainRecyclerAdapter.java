package com.hilllander.khunzohn.gpstracker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hilllander.khunzohn.gpstracker.R;
import com.hilllander.khunzohn.gpstracker.database.model.Device;
import com.hilllander.khunzohn.gpstracker.util.Logger;
import com.hilllander.khunzohn.gpstracker.util.USSD;

import java.util.ArrayList;
import java.util.List;

import mm.technomation.mmtext.MMTextView;

/**
 *Created by khunzohn on 1/2/16.
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

    public void setDevices(List<Device> devices) {
        this.devices = devices;
        for (Device device : devices) {
            Logger.logDevice(TAG, device);
        }
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
        ((MainRecyclerViewHolder) holder).deviceName.setMyanmarText(device.getDeviceName());
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

    }

    @Override
    public int getItemCount() {
        return null != devices ? devices.size() : 0;
    }

    public interface OnDeviceOnClickListener {
        void onClickEdit(Device device);

        void onClickLock(Device device);

        void onClickProfile(Device device);

        void onClickGoToMap(Device device);
    }

    class MainRecyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView deviceProfile;
        private MMTextView deviceName, authorization, tvGoToMap;
        private TextView simNumber, tvLatValue, tvLongValue, tvDateTime;
        private ImageButton ibLock, ibEdit;

        public MainRecyclerViewHolder(View v) {
            super(v);
            Logger.d(TAG, "ViewHolder constructor triggered ");
            deviceProfile = (ImageView) v.findViewById(R.id.deviceProfile);
            deviceName = (MMTextView) v.findViewById(R.id.deviceName);
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