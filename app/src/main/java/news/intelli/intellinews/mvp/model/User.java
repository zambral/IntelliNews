package news.intelli.intellinews.mvp.model;

import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;

import java.util.UUID;

/**
 * Created by llefoulon on 25/10/2016.
 */

public class User {

    //public static final String USER = "user";

    private String id;
    private String email;

    /*public static User createFakeUser(String email) {
        User user = new User();
        user.email = email;
        user.id = UUID.randomUUID().toString();

        return user;
    }*/

    public String getEmail() {
        return email;
    }

    public static User create(@Nullable FirebaseUser firebaseUser) {
        if(firebaseUser == null) return null;

        User user = new User();
        user.email = firebaseUser.getEmail();
        user.id = firebaseUser.getUid();
    }
}
