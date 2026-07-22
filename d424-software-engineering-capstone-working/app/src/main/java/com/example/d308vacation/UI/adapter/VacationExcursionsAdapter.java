package com.example.d308vacation.UI.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308vacation.R;
import com.example.d308vacation.UI.ExcursionDetailActivity;
import com.example.d308vacation.model.Excursion;

import java.util.List;

public class VacationExcursionsAdapter extends RecyclerView.Adapter<VacationExcursionsAdapter.VacationExcursionsViewHolder> {

    private List<Excursion> excursions;
    private final Context context;
    private final LayoutInflater inflater;

    public VacationExcursionsAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public class VacationExcursionsViewHolder extends RecyclerView.ViewHolder {

        private final TextView vacationExcursionsItemView;

        public VacationExcursionsViewHolder(@NonNull View itemView) {
            super(itemView);

            vacationExcursionsItemView = itemView.findViewById(R.id.vacation_list_item_text);
            itemView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                final Excursion current = excursions.get(position);
                if (current != null) {
                    Intent intent = new Intent(context, ExcursionDetailActivity.class)
                            .putExtra("id", current.getId())
                            .putExtra("name", current.getName())
                            .putExtra("date", current.getDate())
                            .putExtra("description", current.getDescription());
                    context.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public VacationExcursionsAdapter.VacationExcursionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = inflater.inflate(R.layout.vacation_list_item, parent, false);

        return new VacationExcursionsAdapter.VacationExcursionsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationExcursionsAdapter.VacationExcursionsViewHolder holder, int position) {
        if (excursions != null) {
            Excursion current = excursions.get(position);
            String name = current.getName();
            holder.vacationExcursionsItemView.setText(name);
        }
    }

    @Override
    public int getItemCount() {
        if (excursions != null) {
            return excursions.size();
        }
        else {
            return 0;
        }
    }

    public void setExcursions(List<Excursion> excursions) {
        this.excursions = excursions;
        notifyDataSetChanged();
    }
}
