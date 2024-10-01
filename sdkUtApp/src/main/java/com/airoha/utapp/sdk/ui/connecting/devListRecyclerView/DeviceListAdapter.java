package com.airoha.utapp.sdk.ui.connecting.devListRecyclerView;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airoha.utapp.sdk.databinding.ItemBoundedDeviceBinding;
import com.airoha.utapp.sdk.util.BluetoothDeviceUtil;

import java.util.List;


@SuppressLint("MissingPermission")
public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.MyViewHolder> {

    private List<String> items;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String device);
    }

    public DeviceListAdapter(List<String> items, OnItemClickListener listener) {
        this.items = items;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(ItemBoundedDeviceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.onBind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemBoundedDeviceBinding mBinding;

        public MyViewHolder(ItemBoundedDeviceBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.getRoot().setOnClickListener(view -> onItemClickListener.onItemClick(items.get(getAdapterPosition())));
        }

        public void onBind(String deviceName) {
          //  mBinding.tvDeviceName.setText(BluetoothDeviceUtil.getDisplayName(device));
            mBinding.tvDeviceName.setText(deviceName);
            mBinding.imgDeviceLogo.setImageResource(BluetoothDeviceUtil.getDisplayIcon());
        }
    }
}

