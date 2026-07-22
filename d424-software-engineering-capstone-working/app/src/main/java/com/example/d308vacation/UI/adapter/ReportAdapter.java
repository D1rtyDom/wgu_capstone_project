package com.example.d308vacation.UI.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308vacation.R;
import com.example.d308vacation.model.Vacation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<Vacation> vacations;
    private final Context context;
    private final LayoutInflater inflater;

    public ReportAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameItemView;
        private final TextView startDateItemView;
        private final TextView endDateItemView;
        private final TextView accommodationsItemView;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            nameItemView = itemView.findViewById(R.id.report_name_text);
            startDateItemView = itemView.findViewById(R.id.report_start_date_text);
            endDateItemView = itemView.findViewById(R.id.report_end_date_text);
            accommodationsItemView = itemView.findViewById(R.id.report_accommodations_text);

        }
    }

    @NonNull
    @Override
    public ReportAdapter.ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = inflater.inflate(R.layout.report_list_item, parent, false);

        return new ReportViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.ReportViewHolder holder, int position) {
        if (vacations != null) {
            Vacation current = vacations.get(position);
            String name = current.getName();
            String startDate = current.getStartDate();
            String endDate = current.getEndDate();
            String accommodations = current.getAccommodationName();

            // set every other row to light blue
            int light_blue = ContextCompat.getColor(context.getApplicationContext(), R.color.light_blue);
            if (position % 2 == 0) {
                holder.nameItemView.setBackgroundColor(light_blue);
                holder.startDateItemView.setBackgroundColor(light_blue);
                holder.endDateItemView.setBackgroundColor(light_blue);
                holder.accommodationsItemView.setBackgroundColor(light_blue);
            }

            holder.nameItemView.setText(name);
            holder.startDateItemView.setText(startDate);
            holder.endDateItemView.setText(endDate);
            holder.accommodationsItemView.setText(accommodations);
        }
    }

    @Override
    public int getItemCount() {
        if (vacations != null) {
            return vacations.size();
        }
        else {
            return 0;
        }
    }

    public void setVacations(List<Vacation> vacations) {
        this.vacations = vacations;
        notifyDataSetChanged();
    }
}
