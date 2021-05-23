package ua.kpi.comsys.io8303.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ua.kpi.comsys.io8303.dao.BookDao;
import ua.kpi.comsys.io8303.model.BookEntity;

@Database(entities = {BookEntity.class}, version = 1)
public abstract class BooksDatabase extends RoomDatabase {
    public abstract BookDao bookDao();

    private static BooksDatabase INSTANCE;

    public static BooksDatabase getDbInstance(Context context) {

        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), BooksDatabase.class, "books")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}