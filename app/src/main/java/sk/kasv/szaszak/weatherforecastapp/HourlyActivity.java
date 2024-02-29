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

import sk.kasv.szaszak.weatherforecastapp.weather.HourlyWeather;

public class HourlyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly);

        // Receive data passed from MainActivity
        List<HourlyWeather> hourlyWeatherList = (List<HourlyWeather>) getIntent().getSerializableExtra("hourlyWeatherList");

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.hourly_list);
        HourlyWeatherAdapter adapter = new HourlyWeatherAdapter(this, hourlyWeatherList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to navigate back to the main activity
                Intent intent = new Intent(HourlyActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });
    }
}
class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.ViewHolder> {

    private final List<HourlyWeather> list;

    private final Context context;
    public HourlyWeatherAdapter(Context context, List<HourlyWeather> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HourlyWeather hourlyWeather = list.get(position);

        holder.timeTextView.setText(hourlyWeather.getTime());
        holder.temperatureTextView.setText(String.valueOf(hourlyWeather.getTemperature()));
        int resourceId = context.getResources().getIdentifier(hourlyWeather.getIcon(), "drawable", context.getPackageName());
        Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + resourceId);
        Picasso.get().load(uri).into(holder.iconImageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        TextView temperatureTextView;
        ImageView iconImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            timeTextView = itemView.findViewById(R.id.hourly_time);
            temperatureTextView = itemView.findViewById(R.id.hourly_temperature);
            iconImageView = itemView.findViewById(R.id.hourly_icon);
        }
    }
}
