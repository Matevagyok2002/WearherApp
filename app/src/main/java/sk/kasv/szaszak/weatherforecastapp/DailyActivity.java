package sk.kasv.szaszak.weatherforecastapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import sk.kasv.szaszak.weatherforecastapp.weather.DailyWeather;

public class DailyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        // Receive data passed from MainActivity
        List<DailyWeather> dailyWeatherList = (List<DailyWeather>) getIntent().getSerializableExtra("dailyWeatherList");

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.daily_list);
        DailyWeatherAdapter adapter = new DailyWeatherAdapter(this, dailyWeatherList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to navigate back to the main activity
                Intent intent = new Intent(DailyActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });
    }
}

class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherAdapter.ViewHolder> {

    private final List<DailyWeather> list;

    private final Context context;
    public DailyWeatherAdapter(Context context, List<DailyWeather> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyWeather dailyWeather = list.get(position);

        holder.dateTextView.setText(dailyWeather.getDate());
        holder.temperatureTextView.setText(String.valueOf(dailyWeather.getTemperature()));
        holder.summaryTextView.setText(dailyWeather.getSummary());
        int resourceId = context.getResources().getIdentifier(dailyWeather.getIcon(), "drawable", context.getPackageName());
        Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + resourceId);
        Picasso.get().load(uri).into(holder.iconImageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView temperatureTextView;

        TextView summaryTextView;
        ImageView iconImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTextView = itemView.findViewById(R.id.daily_date);
            temperatureTextView = itemView.findViewById(R.id.daily_temperature);
            summaryTextView = itemView.findViewById(R.id.summary);
            iconImageView = itemView.findViewById(R.id.daily_icon);
        }
    }
}