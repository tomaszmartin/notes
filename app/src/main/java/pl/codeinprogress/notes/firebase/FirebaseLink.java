package pl.codeinprogress.notes.firebase;

import pl.codeinprogress.notes.model.Note;
import pl.codeinprogress.notes.model.User;

/**
 * Created by tomaszmartin on 23.06.2016.
 */

public class FirebaseLink {

    public static String forUsers() {
        return "/users";
    }

    public static String forUser(User user) {
        return forUsers() + "/" + user.getId();
    }

    public static String forNotes() {
        return "/notes";
    }

    public static String forNote(Note note) {
        return forNotes() + "/" + note.getId();
    }

    public static String forNote(String noteId) {
        return forNotes() + "/" + noteId;
    }

}