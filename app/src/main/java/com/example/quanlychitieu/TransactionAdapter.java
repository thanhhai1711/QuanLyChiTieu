package com.example.quanlychitieu;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<Transaction> list;

    public TransactionAdapter(List<Transaction> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Dùng lại layout item_transaction của mày
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction t = list.get(position);

        // 1. Hiển thị Ghi chú (Hoặc Danh mục nếu ghi chú trống)
        if (t.getNote() == null || t.getNote().isEmpty()) {
            holder.tvNote.setText(t.getCategory());
        } else {
            holder.tvNote.setText(t.getCategory() + " (" + t.getNote() + ")");
        }

        // 2. Hiển thị số tiền
        java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###");
        holder.tvAmount.setText("- " + formatter.format(t.getAmount()) + " VNĐ");

        // 3. XỬ LÝ NHẤN GIỮ ĐỂ XÓA MÓN LẺ
        holder.itemView.setOnLongClickListener(v -> {
            int actualPosition = holder.getAdapterPosition();

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Xóa khoản chi này?")
                    .setMessage("Mày muốn xóa '" + t.getNote() + "' giá " + t.getAmount() + "đ đúng không?")
                    .setPositiveButton("Xóa luôn", (dialog, which) -> {
                        // Gọi Database xóa theo ID duy nhất của món đó
                        DatabaseHelper db = new DatabaseHelper(v.getContext());
                        db.deleteTransactionById(t.getId());

                        // Xóa khỏi danh sách đang hiển thị trên màn hình
                        if (actualPosition != RecyclerView.NO_POSITION) {
                            list.remove(actualPosition);
                            notifyItemRemoved(actualPosition);
                            notifyItemRangeChanged(actualPosition, list.size());
                        }

                        Toast.makeText(v.getContext(), "Đã xóa món lẻ này!", Toast.LENGTH_SHORT).show();

                        // Nếu màn hình DetailActivity có hàm cập nhật tổng tiền, mày có thể gọi ở đây
                        if (v.getContext() instanceof DetailActivity) {
                            // ((DetailActivity) v.getContext()).updateTotal(); // Nếu mày có làm hàm này
                        }
                    })
                    .setNegativeButton("Thôi", null)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNote, tvAmount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Kiểm tra đúng ID trong file item_transaction.xml của mày
            tvNote = itemView.findViewById(R.id.tvNoteItem);
            tvAmount = itemView.findViewById(R.id.tvAmountItem);
        }
    }
}