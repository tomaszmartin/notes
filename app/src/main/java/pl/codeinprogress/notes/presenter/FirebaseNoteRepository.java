package pl.codeinprogress.notes.presenter;

import android.os.Environment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import pl.codeinprogress.notes.model.Note;
import pl.codeinprogress.notes.model.data.firebase.FirebaseLink;
import pl.codeinprogress.notes.secret.Secrets;

/**
 * Implementation of NoteRepository.
 * Uses FirebaseDatabase and FirebaseStorage backed by local copies.
 */

public class FirebaseNoteRepository implements NoteRepository {

    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private ArrayList<Note> notes;

    public void FirebaseNoteRepository() {
        this.database = FirebaseDatabase.getInstance();
        this.storage = FirebaseStorage.getInstance();
        this.notes = getNotes();
    }

    @Override
    public Note getNote(String noteId) {
        if (notes != null && !notes.isEmpty()) {
            for (Note note : notes) {
                if (note.getId().equals(noteId)) {
                    return note;
                }
            }
        }

        return null;
    }

    // TODO: async...
    @Override
    public ArrayList<Note> getNotes() {
        if (this.notes == null) {
            this.notes = new ArrayList<>();
        }
        database.getReference(FirebaseLink.forNotes()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notes.clear();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    if (iterator.next() instanceof Note) {
                        Note current = (Note) iterator.next();
                        notes.add(current);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return null;
    }

    @Override
    public void updateNote(Note note) {
        DatabaseReference current = database.getReference(FirebaseLink.forNote(note));
        current.setValue(note);
    }

    @Override
    public void deleteNote(Note note) {
        deleteFromDatabase(note);
        deleteFromStorage(note);
        deleteFromFile(note);
    }

    @Override
    public void addNote() {
        DatabaseReference noteReference = database.getReference(FirebaseLink.forNotes()).push();
        String noteId = noteReference.getKey();
        Note note = new Note(noteId);
        noteReference.setValue(note);
    }

    private void deleteFromDatabase(Note note) {
        DatabaseReference noteReference = database.getReference(FirebaseLink.forNote(note));
        noteReference.removeValue();
    }

    private void deleteFromFile(Note note) {
        final Note current = note;
        Runnable task = new Runnable() {
            @Override
            public void run() {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + current.getFileName());
                file.delete();
            }
        };
        Thread thread = new Thread(task);
        thread.run();
    }

    private void deleteFromStorage(Note note) {
        StorageReference current = storage.getReference(Secrets.FIREBASE_STORAGE).child(note.getFileName());
        current.delete();
    }

}
