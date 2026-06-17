package com.example.quanlychitieu;

import android.app.AlertDialog;
import android.content.Intent;
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

        holder.tvNote.setText((t.getNote() == null || t.getNote().isEmpty()) ? t.getCategory() : t.getNote());
        holder.tvDate.setText(t.getDate() != null ? t.getDate() : "N/A");

        if (t.isIncome()) {
            holder.tvAmount.setText("+ " + formatter.format(t.getAmount()) + " VNĐ");
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.tvAmount.setText("- " + formatter.format(t.getAmount()) + " VNĐ");
            holder.tvAmount.setTextColor(Color.RED);
        }

        // Nhấn giữ: hiện dialog chọn Sửa hoặc Xóa
        holder.itemView.setOnLongClickListener(v -> {
            int actualPosition = holder.getAdapterPosition();
            String label = (t.getNote() == null || t.getNote().isEmpty()) ? t.getCategory() : t.getNote();

            new AlertDialog.Builder(v.getContext())
                    .setTitle(label)
                    .setItems(new String[]{"✏️ Sửa giao dịch", "🗑️ Xóa giao dịch"}, (dialog, which) -> {
                        if (which == 0) {
                            Intent intent = new Intent(v.getContext(), EditTransactionActivity.class);
                            intent.putExtra("TRANSACTION_ID", t.getId());
                            intent.putExtra("AMOUNT", String.valueOf((long) t.getAmount()));
                            intent.putExtra("NOTE", t.getNote());
                            intent.putExtra("CATEGORY", t.getCategory());
                            intent.putExtra("TYPE", t.getType());
                            v.getContext().startActivity(intent);
                        } else {
                            new AlertDialog.Builder(v.getContext())
                                    .setTitle("Xóa giao dịch?")
                                    .setMessage("Xóa '" + label + "' " + formatter.format(t.getAmount()) + " đ?")
                                    .setPositiveButton("Xóa", (d, w) -> {
                                        new DatabaseHelper(v.getContext()).deleteTransactionById(t.getId());
                                        if (actualPosition != RecyclerView.NO_POSITION) {
                                            Transaction item = list.get(actualPosition);
                                            list.remove(actualPosition);
                                            listFull.remove(item);
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
                        }
                    })
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