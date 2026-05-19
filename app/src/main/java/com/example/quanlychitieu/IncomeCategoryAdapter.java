package com.example.quanlychitieu;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class IncomeCategoryAdapter extends RecyclerView.Adapter<IncomeCategoryAdapter.ViewHolder> {
    private List<CategorySummary> list;
    private List<CategorySummary> listFull;
    private int currentMonth, currentYear;
    private String username;

    public IncomeCategoryAdapter(List<CategorySummary> list, int month, int year, String username) {
        this.list = list;
        this.listFull = new ArrayList<>(list);
        this.currentMonth = month;
        this.currentYear = year;
        this.username = username;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_income_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategorySummary item = list.get(position);
        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.tvCategoryName.setText(item.getCategory());
        holder.tvTotalAmount.setText("+" + formatter.format(item.getTotalAmount()) + "đ");
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailActivity.class);
            intent.putExtra("CATEGORY_NAME", item.getCategory());
            intent.putExtra("MONTH", currentMonth);
            intent.putExtra("YEAR", currentYear);
            intent.putExtra("USERNAME", username);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return list != null ? list.size() : 0; }

    public void filter(String text) {
        list.clear();
        if (text.isEmpty()) { list.addAll(listFull); }
        else {
            text = text.toLowerCase().trim();
            for (CategorySummary item : listFull)
                if (item.getCategory().toLowerCase().contains(text)) list.add(item);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvTotalAmount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvTotalAmount  = itemView.findViewById(R.id.tvTotalAmount);
        }
    }
}