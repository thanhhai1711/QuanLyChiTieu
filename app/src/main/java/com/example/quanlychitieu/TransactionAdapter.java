package com.example.quanlychitieu;

import android.app.AlertDialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<Transaction> list;
    private List<Transaction> listFull;

    public TransactionAdapter(List<Transaction> list) {
        this.list = list;
        this.listFull = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction t = list.get(position);
        DecimalFormat formatter = new DecimalFormat("#,###");

        if (t.getNote() == null || t.getNote().isEmpty()) {
            holder.tvNote.setText(t.getCategory());
        } else {
            holder.tvNote.setText(t.getNote());
        }

        holder.tvDate.setText(t.getDate() != null ? t.getDate() : "N/A");

        if (t.isIncome()) {
            holder.tvAmount.setText("+ " + formatter.format(t.getAmount()) + " VNĐ");
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.tvAmount.setText("- " + formatter.format(t.getAmount()) + " VNĐ");
            holder.tvAmount.setTextColor(Color.RED);
        }

        holder.itemView.setOnLongClickListener(v -> {
            int actualPosition = holder.getAdapterPosition();
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Xóa giao dịch này?")
                    .setMessage("Xóa '" + (t.getNote().isEmpty() ? t.getCategory() : t.getNote()) +
                            "' " + formatter.format(t.getAmount()) + " đ?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        DatabaseHelper db = new DatabaseHelper(v.getContext());
                        db.deleteTransactionById(t.getId());
                        if (actualPosition != RecyclerView.NO_POSITION) {
                            Transaction itemToRemove = list.get(actualPosition);
                            list.remove(actualPosition);
                            listFull.remove(itemToRemove);
                            notifyItemRemoved(actualPosition);
                            notifyItemRangeChanged(actualPosition, list.size());
                        }
                        Toast.makeText(v.getContext(), "Đã xóa!", Toast.LENGTH_SHORT).show();
                        if (v.getContext() instanceof DetailActivity) {
                            ((DetailActivity) v.getContext()).loadData();
                        }
                    })
                    .setNegativeButton("Thôi", null)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() { return (list != null) ? list.size() : 0; }

    public void filter(String text) {
        list.clear();
        if (text.isEmpty()) {
            list.addAll(listFull);
        } else {
            text = text.toLowerCase();
            for (Transaction item : listFull) {
                String note = item.getNote() != null ? item.getNote().toLowerCase() : "";
                String category = item.getCategory() != null ? item.getCategory().toLowerCase() : "";
                if (note.contains(text) || category.contains(text)) list.add(item);
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNote, tvAmount, tvDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNote = itemView.findViewById(R.id.tvNoteItem);
            tvAmount = itemView.findViewById(R.id.tvAmountItem);
            tvDate = itemView.findViewById(R.id.tvDateItem);
        }
    }
}