package http.adapters;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        out.value(Optional.ofNullable(value)
                .map(v -> v.format(DateTimeFormatter.ISO_DATE_TIME))
                .orElse(null));
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        String value = in.nextString();

        if (value.isBlank()) {
            return null;
        }

        return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
    }

}
