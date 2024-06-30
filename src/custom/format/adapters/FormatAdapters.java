package custom.format.adapters;

import http.handlers.BaseHttpHandler;
import tasks.epic.Epic;
import tasks.subtask.SubTask;
import task.enums.TaskStatus;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatAdapters extends BaseHttpHandler {
    public static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            jsonWriter.value(localDateTime.format(formatter));
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), formatter);
        }
    }

    public static class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            if (duration == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(duration.toString());
            }
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            return Duration.parse(jsonReader.nextString());
        }
    }

    public static class EpicTypeAdapter extends TypeAdapter<Epic> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(JsonWriter out, Epic epic) throws IOException {
            out.beginObject();
            out.name("subTasks");
            out.beginArray();
            for (SubTask subTask : epic.getSubTasks()) {
                out.beginObject();
                out.name("subTaskName").value(subTask.toString());
                out.endObject();
            }
            out.endArray();

            out.name("endTime").value(epic.getEndTime().format(formatter));
            out.name("taskName").value(epic.getTaskName());
            out.name("description").value(epic.getDescription());
            out.name("id").value(epic.getTaskId());
            out.name("taskStatus").value(epic.getTaskStatus().toString());
            out.name("duration").value(epic.getDuration().toString());
            out.name("startTime").value(epic.getStartTime().format(formatter));
            out.endObject();
        }

        @Override
        public Epic read(final JsonReader jsonReader) throws IOException {
            return new Epic(jsonReader.nextString(), jsonReader.nextString());
        }
    }

    public static class SubTaskTypeAdapter extends TypeAdapter<SubTask> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        private final TypeAdapter<Epic> epicTypeAdapter;

        public SubTaskTypeAdapter(TypeAdapter<Epic> epicTypeAdapter) {
            this.epicTypeAdapter = epicTypeAdapter;
        }

        @Override
        public void write(JsonWriter out, SubTask subTask) throws IOException {
            out.beginObject();

            out.name("epic");
            epicTypeAdapter.write(out, subTask.getEpic());

            out.name("taskName").value(subTask.getTaskName());
            out.name("description").value(subTask.getDescription());
            out.name("id").value(subTask.getTaskId());
            out.name("taskStatus").value(subTask.getTaskStatus().toString());
            out.name("duration").value(subTask.getDuration().toString());
            out.name("startTime").value(subTask.getStartTime().format(formatter));

            out.endObject();
        }

        @Override
        public SubTask read(final JsonReader jsonReader) throws IOException {
            return new SubTask(jsonReader.nextString(), jsonReader.nextString(),
                    TaskStatus.valueOf(jsonReader.nextString()),
                    Duration.parse(jsonReader.nextString()), LocalDateTime.parse(jsonReader.nextString()));
        }
    }
}
