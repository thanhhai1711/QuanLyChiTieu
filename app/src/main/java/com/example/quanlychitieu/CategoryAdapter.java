package com.example.quanlychitieu;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<CategorySummary> list;

    public CategoryAdapter(List<CategorySummary> list) { this.list = list; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạm thời dùng lại giao diện item_transaction cho nhanh, lát rảnh mày tự css sau
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 1. Phải moi được cái item ở vị trí hiện tại ra (Mày đang thiếu dòng này)
        CategorySummary item = list.get(position);

        // 2. Hiện tên danh mục và tổng tiền ra màn hình (Mày cũng lỡ xóa mất)
        holder.tvNote.setText(item.getCategory());
        holder.tvAmount.setText(item.getTotalAmount() + " VNĐ");

        // 3. Sự kiện bấm vào dòng (Code mày làm đúng đoạn này rồi)
        holder.itemView.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                // Tạo lệnh bay sang màn hình DetailActivity
                android.content.Intent intent = new android.content.Intent(v.getContext(), DetailActivity.class);

                // Gói cái tên danh mục mang theo
                intent.putExtra("CATEGORY_NAME", item.getCategory());

                // Khởi hành
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNote, tvAmount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNote = itemView.findViewById(R.id.tvNoteItem);
            tvAmount = itemView.findViewById(R.id.tvAmountItem);
        }
    }
}