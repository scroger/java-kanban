package http.adapters;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter out, Duration value) throws IOException {
        out.value(Optional.ofNullable(value).map(Duration::toMinutes).orElse(null));
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        long value = in.nextLong();

        if (value <= 0) {
            return null;
        }

        return Duration.ofMinutes(value);
    }
}
