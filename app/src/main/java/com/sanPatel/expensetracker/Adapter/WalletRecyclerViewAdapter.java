package com.sanPatel.expensetracker.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sanPatel.expensetracker.Datas.Wallet;
import com.sanPatel.expensetracker.R;

import java.util.ArrayList;

public class WalletRecyclerViewAdapter extends RecyclerView.Adapter<WalletRecyclerViewAdapter.WalletViewHolder> {

    private ArrayList<Wallet> walletList;
    public WalletRecyclerViewAdapter(ArrayList<Wallet> walletList) {
        this.walletList = walletList;
    }

    @NonNull
    @Override
    public WalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_layout,parent,false);
        return new WalletViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletViewHolder holder, int position) {
        holder.tvWalletName.setText(walletList.get(position).getWalletName());
    }

    @Override
    public int getItemCount() {
        return walletList.size();
    }

    public class WalletViewHolder extends RecyclerView.ViewHolder {

        TextView tvWalletName;
        public WalletViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWalletName = itemView.findViewById(R.id.text_view_wallet_name);
        }
    }
}
