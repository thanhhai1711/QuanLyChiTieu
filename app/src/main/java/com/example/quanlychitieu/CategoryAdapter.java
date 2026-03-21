package com.example.quanlychitieu;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<CategorySummary> list;

    public CategoryAdapter(List<CategorySummary> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategorySummary item = list.get(position);

        holder.tvNote.setText(item.getCategory());
        holder.tvAmount.setText(String.valueOf(item.getTotalAmount()) + " VNĐ");

        // SỰ KIỆN NHẤN GIỮ ĐỂ XÓA
        holder.itemView.setOnLongClickListener(v -> {
            // DÙNG HÀM NÀY CHO BẢN CŨ NÈ HẢI
            int actualPosition = holder.getAdapterPosition();

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Xác nhận xóa")
                    .setMessage("Mày muốn xóa sạch tiền của mục '" + item.getCategory() + "' không?")
                    .setPositiveButton("Xóa luôn", (dialog, which) -> {
                        // 1. Thực hiện xóa trong Database
                        DatabaseHelper db = new DatabaseHelper(v.getContext());
                        db.deleteTransactionsByCategory(item.getCategory());

                        // 2. Xóa trong List và báo Adapter cập nhật ngay
                        if (actualPosition != RecyclerView.NO_POSITION) {
                            list.remove(actualPosition);
                            notifyItemRemoved(actualPosition);
                            notifyItemRangeChanged(actualPosition, list.size());
                        }

                        Toast.makeText(v.getContext(), "Đã dọn dẹp xong!", Toast.LENGTH_SHORT).show();

                        // 3. Cập nhật lại biểu đồ ở MainActivity
                        if (v.getContext() instanceof MainActivity) {
                            ((MainActivity) v.getContext()).updateUI();
                        }
                    })
                    .setNegativeButton("Thôi", null)
                    .show();
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailActivity.class);
            intent.putExtra("CATEGORY_NAME", item.getCategory());
            v.getContext().startActivity(intent);
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
            tvNote = itemView.findViewById(R.id.tvNoteItem);
            tvAmount = itemView.findViewById(R.id.tvAmountItem);
        }
    }
}