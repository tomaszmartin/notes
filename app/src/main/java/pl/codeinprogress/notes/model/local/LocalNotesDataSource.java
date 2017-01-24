package pl.codeinprogress.notes.model.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.Date;
import java.util.List;

import pl.codeinprogress.notes.model.Note;
import pl.codeinprogress.notes.model.NotesDataSource;
import pl.codeinprogress.notes.util.SchedulerProvider;
import rx.Observable;
import rx.functions.Action1;

import static com.google.common.base.Preconditions.checkNotNull;
import static pl.codeinprogress.notes.model.local.NotesContract.CREATED;
import static pl.codeinprogress.notes.model.local.NotesContract.DESCRIPTION;
import static pl.codeinprogress.notes.model.local.NotesContract.ENTRY_ID;
import static pl.codeinprogress.notes.model.local.NotesContract.MODIFIED;
import static pl.codeinprogress.notes.model.local.NotesContract.PATH;
import static pl.codeinprogress.notes.model.local.NotesContract.SECURED;
import static pl.codeinprogress.notes.model.local.NotesContract.TABLE_NAME;
import static pl.codeinprogress.notes.model.local.NotesContract.TITLE;

public class LocalNotesDataSource implements NotesDataSource {

    @Nullable
    private static LocalNotesDataSource instance;
    @NonNull
    private static BriteDatabase database;

    private LocalNotesDataSource(@NonNull Context context, @NonNull SchedulerProvider schedulerProvider) {
        LocalNotesDatabaseHelper databaseHelper = new LocalNotesDatabaseHelper(context);
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        database = sqlBrite.wrapDatabaseHelper(databaseHelper, schedulerProvider.io());
    }

    public static LocalNotesDataSource getInstance(@NonNull Context context, @NonNull SchedulerProvider schedulerProvider) {
        if (instance == null) {
            instance = new LocalNotesDataSource(context, schedulerProvider);
        }

        return instance;
    }

    @Override
    public Observable<List<Note>> getNotes() {
        String[] projection = {
                ENTRY_ID,
                TITLE,
                DESCRIPTION,
                PATH,
                CREATED,
                MODIFIED,
                SECURED
        };
        String sql = String.format("SELECT %s FROM %s", TextUtils.join(",", projection), TABLE_NAME);
        Log.d(this.getClass().getSimpleName(), "getNotes: " + sql);
        return database.createQuery(TABLE_NAME, sql).mapToList(this::getNote);
    }

    @Override
    public Observable<Note> getNote(@NonNull String noteId) {
        Log.d(this.getClass().getSimpleName(), "getNote: " + noteId);
        String[] projection = {
                ENTRY_ID,
                TITLE,
                DESCRIPTION,
                PATH,
                CREATED,
                MODIFIED,
                SECURED
        };
        String sql = String.format("SELECT %s FROM %s WHERE %s LIKE ?", TextUtils.join(",", projection), TABLE_NAME, ENTRY_ID, noteId);
        Log.d(this.getClass().getSimpleName(), "getNote: " + sql);
        return database.createQuery(TABLE_NAME, sql, noteId).mapToOne(this::getNote);
    }



    @Override
    public void saveNote(@NonNull Note note) {
        checkNotNull(note);
        ContentValues values = new ContentValues();
        values.put(ENTRY_ID, note.getId());
        values.put(TITLE, note.getTitle());
        values.put(DESCRIPTION, note.getDescription());
        values.put(PATH, note.getPath());
        values.put(CREATED, note.getCreated());
        values.put(MODIFIED, new Date().getTime());
        values.put(SECURED, note.isSecured());

        String selection = ENTRY_ID + " LIKE ?";
        database.update(TABLE_NAME, values, selection, note.getId());
    }

    @Override
    public void deleteNote(@NonNull String noteId) {
        String selection = ENTRY_ID + "LIKE ?";
        String[] selectionArgs = {noteId};

        database.delete(TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public void addNote(@NonNull String noteId) {
        checkNotNull(noteId);
        ContentValues values = new ContentValues();
        long timestamp = new Date().getTime();
        values.put(ENTRY_ID, noteId);
        values.put(CREATED, timestamp);
        values.put(MODIFIED, timestamp);

        String selection = ENTRY_ID + " LIKE ?";
        database.update(TABLE_NAME, values, selection, noteId);
    }


    @NonNull
    private Note getNote(@NonNull Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getString(cursor.getColumnIndexOrThrow(ENTRY_ID)));
        note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
        note.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
        note.setPath(cursor.getString(cursor.getColumnIndexOrThrow(PATH)));
        note.setCreated(cursor.getLong(cursor.getColumnIndexOrThrow(CREATED)));
        note.setModified(cursor.getLong(cursor.getColumnIndexOrThrow(MODIFIED)));
        note.setSecured(cursor.getInt(cursor.getColumnIndexOrThrow(SECURED)) == 1);
        Log.d(this.getClass().getSimpleName(), "getNote: " + note.toString());

        return note;
    }

}
