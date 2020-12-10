package com.example.meusamigos2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DbGateway  {
    private static DbGateway ligacao;
    private SQLiteDatabase db;

    private DbGateway (Context context)
    {
        DbHelper helper = new DbHelper(context);
        db = helper.getWritableDatabase();
    }

    public static DbGateway getInstance(Context context)
    {
        if (ligacao == null)
        {
            ligacao = new DbGateway(context);
        }

        return ligacao;
    }

    public SQLiteDatabase getDatabase()
    {
        return this.db;
    }

}
