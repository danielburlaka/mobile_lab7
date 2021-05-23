package ua.kpi.comsys.io8303.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import ua.kpi.comsys.io8303.model.BookEntity;

import java.util.List;

@Dao
public interface BookDao {
    @Query("SELECT * FROM books")
    List<BookEntity> getAll();

    @Query("SELECT * FROM books WHERE search LIKE :s LIMIT 3")
    BookEntity findBySearch(String s);

    @Query("SELECT * FROM books WHERE search LIKE :s")
    List<BookEntity> getAllBySearch(String s);

    @Insert
    void insertAll(BookEntity... movies);

    @Delete
    void delete(BookEntity movies);

}
