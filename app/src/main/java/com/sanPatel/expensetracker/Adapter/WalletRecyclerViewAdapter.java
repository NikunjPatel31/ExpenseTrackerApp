package com.sanPatel.expensetracker.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sanPatel.expensetracker.Datas.Wallet;
import com.sanPatel.expensetracker.R;
import com.sanPatel.expensetracker.WalletActivity;

import java.util.ArrayList;

public class WalletRecyclerViewAdapter extends RecyclerView.Adapter<WalletRecyclerViewAdapter.WalletViewHolder> {

    private static final String TAG = "WalletRecyclerView";
    private ArrayList<Wallet> walletList;
    private Context context;
    public WalletRecyclerViewAdapter(ArrayList<Wallet> walletList, Context context) {
        this.walletList = walletList;
        this.context = context;
    }

    @NonNull
    @Override
    public WalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_layout,parent,false);
        return new WalletViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletViewHolder holder, final int position) {
        holder.tvWalletName.setText(walletList.get(position).getWalletName());
        holder.cvParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WalletActivity.class);
                intent.putExtra("Wallet_id",walletList.get(position).getWalletID());
                intent.putExtra("Wallet_name", walletList.get(position).getWalletName());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: SIZE: "+walletList.size());
        return walletList.size();
    }

    public class WalletViewHolder extends RecyclerView.ViewHolder {

        TextView tvWalletName;
        CardView cvParentLayout;
        public WalletViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWalletName = itemView.findViewById(R.id.text_view_wallet_name);
            cvParentLayout = itemView.findViewById(R.id.card_view_parent_layout);
        }
    }
}
