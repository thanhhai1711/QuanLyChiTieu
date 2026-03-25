package com.example.quanlychitieu;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<Transaction> list;

    public TransactionAdapter(List<Transaction> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Layout item_transaction.xml mày đã sửa thêm tvDateItem rồi nhé
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction t = list.get(position);

        // 1. Hiển thị Ghi chú (Nếu trống thì hiện Danh mục)
        if (t.getNote() == null || t.getNote().isEmpty()) {
            holder.tvNote.setText(t.getCategory());
        } else {
            holder.tvNote.setText(t.getNote());
        }

        // 2. Hiển thị Ngày tháng (Xử lý nếu date bị null)
        if (t.getDate() != null) {
            holder.tvDate.setText(t.getDate());
        } else {
            holder.tvDate.setText("N/A");
        }

        // 3. Hiển thị số tiền (Xóa số .0 và thêm dấu phân cách)
        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.tvAmount.setText("- " + formatter.format(t.getAmount()) + " VNĐ");

        // 4. Nhấn giữ để XÓA món lẻ
        holder.itemView.setOnLongClickListener(v -> {
            int actualPosition = holder.getAdapterPosition();

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Xóa khoản chi này?")
                    .setMessage("Mày muốn xóa '" + (t.getNote().isEmpty() ? t.getCategory() : t.getNote()) +
                            "' giá " + formatter.format(t.getAmount()) + "đ đúng không?")
                    .setPositiveButton("Xóa luôn", (dialog, which) -> {
                        DatabaseHelper db = new DatabaseHelper(v.getContext());
                        db.deleteTransactionById(t.getId());

                        if (actualPosition != RecyclerView.NO_POSITION) {
                            list.remove(actualPosition);
                            notifyItemRemoved(actualPosition);
                            notifyItemRangeChanged(actualPosition, list.size());
                        }

                        Toast.makeText(v.getContext(), "Đã xóa xong!", Toast.LENGTH_SHORT).show();

                        // Cập nhật lại giao diện màn hình chi tiết nếu có hàm loadData
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
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNote, tvAmount, tvDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNote = itemView.findViewById(R.id.tvNoteItem);
            tvAmount = itemView.findViewById(R.id.tvAmountItem);
            tvDate = itemView.findViewById(R.id.tvDateItem); // Nhớ check ID này trong XML nhé
        }
    }

}