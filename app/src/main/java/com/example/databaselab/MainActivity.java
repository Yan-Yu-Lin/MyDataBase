package com.example.databaselab;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText ed_book, ed_price;
    private Button btn_query, btn_insert, btn_update, btn_delete;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> items = new ArrayList<>();
    private SQLiteDatabase dbrw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 連結畫面元件
        ed_book = findViewById(R.id.ed_book);
        ed_price = findViewById(R.id.ed_price);
        btn_query = findViewById(R.id.btn_query);
        btn_insert = findViewById(R.id.btn_insert);
        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);
        listView = findViewById(R.id.listView);

        // 宣告 Adapter，使用 simple_list_item_1 並連結 listView
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

        // 取得資料庫實體
        dbrw = new MyDBHelper(this).getWritableDatabase();

        // 新增按鈕事件
        btn_insert.setOnClickListener(view -> {
            if (ed_book.length() < 1 || ed_price.length() < 1)
                Toast.makeText(MainActivity.this, "欄位請勿空白", Toast.LENGTH_SHORT).show();
            else {
                try {
                    dbrw.execSQL("INSERT INTO myTable(book,price) values(?,?)",
                            new Object[]{ed_book.getText().toString(),
                                    ed_price.getText().toString()});
                    Toast.makeText(MainActivity.this,
                            "新增書名" + ed_book.getText().toString() +
                                    "價格" + ed_price.getText().toString(),
                            Toast.LENGTH_SHORT).show();
                    ed_book.setText("");
                    ed_price.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,
                            "新增失敗:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 修改按鈕事件
        btn_update.setOnClickListener(view -> {
            if (ed_book.length() < 1 || ed_price.length() < 1)
                Toast.makeText(MainActivity.this, "欄位請勿空白", Toast.LENGTH_SHORT).show();
            else {
                try {
                    dbrw.execSQL("UPDATE myTable SET price = " +
                            ed_price.getText().toString() +
                            " WHERE book LIKE '" +
                            ed_book.getText().toString() + "'");
                    Toast.makeText(MainActivity.this,
                            "更新書名" + ed_book.getText().toString() +
                                    "價格" + ed_price.getText().toString(),
                            Toast.LENGTH_SHORT).show();
                    ed_book.setText("");
                    ed_price.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,
                            "更新失敗:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 刪除按鈕事件
        btn_delete.setOnClickListener(view -> {
            if (ed_book.length() < 1)
                Toast.makeText(MainActivity.this, "書名請勿空白", Toast.LENGTH_SHORT).show();
            else {
                try {
                    dbrw.execSQL("DELETE FROM myTable WHERE book LIKE '" +
                            ed_book.getText().toString() + "'");
                    Toast.makeText(MainActivity.this,
                            "刪除書名" + ed_book.getText().toString(),
                            Toast.LENGTH_SHORT).show();
                    ed_book.setText("");
                    ed_price.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,
                            "刪除失敗:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 查詢按鈕事件
        btn_query.setOnClickListener(view -> {
            Cursor c;
            if (ed_book.length() < 1)
                c = dbrw.rawQuery("SELECT * FROM myTable", null);
            else
                c = dbrw.rawQuery("SELECT * FROM myTable WHERE book LIKE '" +
                        ed_book.getText().toString() + "'", null);

            c.moveToFirst();
            items.clear();
            Toast.makeText(MainActivity.this,
                    "共有" + c.getCount() + "筆", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < c.getCount(); i++) {
                items.add("書籍:" + c.getString(0) + "\t\t\t\t價格:" + c.getString(1));
                c.moveToNext();
            }
            adapter.notifyDataSetChanged();
            c.close();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 關閉資料庫
        dbrw.close();
    }
}