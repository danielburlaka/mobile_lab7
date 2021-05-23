package ua.kpi.comsys.io8303.ui_fragments.book_list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.lab1.R;
import ua.kpi.comsys.io8303.activities.AddBookActivity;
import ua.kpi.comsys.io8303.activities.DisplayBookActivity;
import ua.kpi.comsys.io8303.adapters.BookListAdapter;
import ua.kpi.comsys.io8303.database.BooksDatabase;
import ua.kpi.comsys.io8303.model.BookEntity;
import ua.kpi.comsys.io8303.model.BookItem;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import ua.kpi.comsys.io8303.model.SearchItem;
import ua.kpi.comsys.io8303.threads.BookBGThread;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BookListFragment extends Fragment {
    private String TAG = "MovieList";
    private static final String BOOK = "movie";
    private static final String RESULT = "result";
    private static String booksJSON = "";

    ArrayList<BookItem> books = new ArrayList<>();
    ArrayList<BookEntity> bookEntity = new ArrayList<>();
    SearchItem searchItem = new SearchItem();
    ArrayList<BookItem> searchedList = new ArrayList<>();
    ListView list;
    BookListAdapter adapter;
    ArrayList<String> textViewResourceId = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_book_list, container, false);
        TextView nothingFound = root.findViewById(R.id.nothingFound);
        TextView noInternet = root.findViewById(R.id.noInternetText);
        nothingFound.setVisibility(View.INVISIBLE);
        noInternet.setVisibility(View.INVISIBLE);
        list = root.findViewById(R.id.BooksListView);
        SearchView searchView = root.findViewById(R.id.searchView2);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null)
                    adapter.clear();
                Log.i(TAG, String.valueOf(searchedList.size()));
                if (newText.length() >= 3){
                    nothingFound.setVisibility(View.INVISIBLE);
                    noInternet.setVisibility(View.INVISIBLE);
                    try {
                        if (isNetworkAvailable()) {
                            fillListFromInternet(newText);
                        } else {
                            fillFromDatabase(newText, noInternet);
                        }
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                        nothingFound.setVisibility(View.VISIBLE);
                    }
                }
                else
                    nothingFound.setVisibility(View.VISIBLE);
                return false;
            }
        });

        Button addItemButton = (Button) root.findViewById(R.id.addItem);

        addItemButton.setOnClickListener(v -> {
            try {
                openAddBookActivity();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return root;
    }

    private void fillListFromInternet(String search) throws InterruptedException {
        Gson gson = new Gson();
        BooksDatabase db = BooksDatabase.getDbInstance(getContext());
        Type listOfBooksItemsType = new TypeToken<SearchItem>() {}.getType();
        BookBGThread g = new BookBGThread(search);
        Thread t = new Thread(g, "Background Thread");
        t.start();//we start the thread
        t.join();
        System.out.println(booksJSON);
        try {
            System.out.println("Query: " + db.bookDao().findBySearch(search).toString());
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        if (booksJSON.contains("\"Response\":\"False\""))
            throw new InterruptedException();
        searchItem = gson.fromJson(booksJSON, listOfBooksItemsType);
        searchedList = searchItem.getBooks();
        updateResourseId(searchedList);

        //adding to db missing searches
        if (db.bookDao() != null && db.bookDao().findBySearch(search) == null){
            for (BookItem book : searchedList) {
                System.out.println(book.toEntity(search));
                db.bookDao().insertAll(book.toEntity(search));
            }
        }

        adapter = new BookListAdapter(this, searchedList, textViewResourceId);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = list.getItemAtPosition(position);
                openDisplayBookActivity(id);
            }
        });
    }

    public void fillFromDatabase(String search, TextView tv) throws InterruptedException{
        System.out.println("Connection is not available");
        BooksDatabase db = BooksDatabase.getDbInstance(getContext());
        if (db.bookDao().findBySearch(search) != null) {
            bookEntity = new ArrayList<BookEntity>(db.bookDao().getAllBySearch(search));
            searchedList.clear();
            for (BookEntity entity : bookEntity) {
                searchedList.add(entity.toItem());
                System.out.println("Converting to item: "+entity.toItem().toString());
            }
            System.out.println("Converted to item: "+searchedList);
            adapter = new BookListAdapter(this, searchedList, textViewResourceId);
            list.setAdapter(adapter);
            updateResourseId(searchedList);
            list.setOnItemClickListener((parent, view, position, id) -> {
                Object listItem = list.getItemAtPosition(position);
                openDisplayBookActivity(id);
            });
        }
        else {
            tv.setVisibility(View.VISIBLE);
            throw new InterruptedException();
        }
    }

    public void refresh() {
        updateResourseId(searchedList);
        adapter = new BookListAdapter(this, searchedList, textViewResourceId);
        list.setAdapter(adapter);
    }

    public void openDisplayBookActivity(long id) {
        Intent intent = new Intent(this.getActivity(), DisplayBookActivity.class);
        intent.putExtra(BOOK, searchItem.getBooks().get((int) (id)).getIsbn13());
        startActivity(intent);
    }

    private void updateResourseId (ArrayList<BookItem> list) {
        textViewResourceId.clear();
        for (BookItem book: list) {
            textViewResourceId.add(book.getTitle());
        }
    }

    public void openAddBookActivity() throws IOException {
        Intent intent = new Intent(this.getActivity(), AddBookActivity.class);
        intent.putExtra(BOOK, booksToString());
        startActivityForResult(intent, 1);
    }

    private void updateJSON(String newData) {
        Gson gson = new Gson();
        Type listOfBooksItemsType = new TypeToken<ArrayList<BookItem>>() {}.getType();
        Log.i(TAG, booksToString());
        books = gson.fromJson(newData, listOfBooksItemsType);
        searchedList = books;
        refresh();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String returnValue = data.getStringExtra(RESULT);
                updateJSON(returnValue);
            }
        }
    }

    private String booksToString() {
        StringBuilder result = new StringBuilder("[ ");
        for (int i = 0; i < searchItem.getBooks().size(); i++) {
            if (i < searchItem.getBooks().size() - 1) {
                result.append(searchItem.getBooks().get(i).toString());
                result.append(", ");
            }
            else result.append(searchItem.getBooks().get(i).toString());

        }
        return result.append(" ]").toString();
    }

    public static void getUrlResponse(String search) throws IOException {
        booksJSON = search;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}