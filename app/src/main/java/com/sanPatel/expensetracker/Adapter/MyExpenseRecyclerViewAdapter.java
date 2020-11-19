package com.sanPatel.expensetracker.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sanPatel.expensetracker.AddExpenseActivity;
import com.sanPatel.expensetracker.Datas.Expense;
import com.sanPatel.expensetracker.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MyExpenseRecyclerViewAdapter extends RecyclerView.Adapter<MyExpenseRecyclerViewAdapter.MyExpenseRecyclerViewHolder>{

    ArrayList<Expense> expenseList;
    Context context;

    public MyExpenseRecyclerViewAdapter(ArrayList<Expense> expenseList, Context context) {
        this.expenseList = expenseList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyExpenseRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item_layout,parent,false);
        return new MyExpenseRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyExpenseRecyclerViewHolder holder, final int position) {
        holder.setExpenseTitle(expenseList.get(position).getExpense_title());
        holder.setExpenseDesc(expenseList.get(position).getExpense_description());
        holder.setExpenseDate(new SimpleDateFormat("dd-MM-yyyy").format(expenseList.get(position).getExpense_date()));

        if (expenseList.get(position).getExpense_type() == 0) {
            // entry is for income.
            holder.setExpenseAmount("+ ₹"+Double.toString(expenseList.get(position).getExpense_amount()));
            holder.tvAmount.setTextColor(context.getResources().getColor(R.color.colorIncomeAmount));
        } else {
            // entry is for expense.
            holder.setExpenseAmount("- ₹"+Double.toString(expenseList.get(position).getExpense_amount()));
            holder.tvAmount.setTextColor(Color.RED);
        }
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // this method will take user to edit expense screen.
                Intent intent = new Intent(context, AddExpenseActivity.class);
                intent.putExtra("Activity","Edit_expense");
                intent.putExtra("Expense_id",expenseList.get(position).getExpense_id());
                intent.putExtra("Amount",expenseList.get(position).getExpense_amount());
                intent.putExtra("Title",expenseList.get(position).getExpense_title());
                intent.putExtra("Desc",expenseList.get(position).getExpense_description());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public class MyExpenseRecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvAmount, tvDesc, tvDate;
        private CardView parentLayout;
        public MyExpenseRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.text_view_title);
            tvAmount = itemView.findViewById(R.id.text_view_amount);
            tvDate = itemView.findViewById(R.id.text_view_date);
            tvDesc = itemView.findViewById(R.id.text_view_desc);
            parentLayout = itemView.findViewById(R.id.expense_item_parent_layout);
        }

        public void setExpenseTitle(String title) {
            tvTitle.setText(title);
        }

        public void setExpenseAmount(String amount) {
            tvAmount.setText(amount);
        }

        public void setExpenseDesc(String desc) {
            tvDesc.setText(desc);
        }

        public void setExpenseDate(String date) {
            tvDate.setText(date);
        }

    }
}
