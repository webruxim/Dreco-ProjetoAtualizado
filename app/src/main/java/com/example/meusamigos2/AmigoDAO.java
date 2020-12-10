package com.example.meusamigos2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

public class AmigoDAO extends _Default {
    private final String TABLE_AMIGOS = "Amigos";
    private DbGateway gw;

    public AmigoDAO (Context context)
    {
        gw = DbGateway.getInstance(context);
    }

    public boolean atualizarContatoMysql(String pNome, String pCelular) {
        List<Amigo> amigos = new ArrayList<>();
        Cursor cursor = null;
        DB db = new DB();

        cursor = gw.getDatabase().rawQuery("SELECT * FROM Amigos WHERE Status <= 1", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("ID"));
                String nome = cursor.getString(cursor.getColumnIndex("Nome"));
                String celular = cursor.getString(cursor.getColumnIndex("Celular"));
                int status = cursor.getInt(cursor.getColumnIndex("Status"));
                int sincronizado = cursor.getInt(cursor.getColumnIndex("Sincronizado"));
                byte[] imagemEmString = cursor.getBlob(cursor.getColumnIndex("Foto"));

                if ((nome.equals(pNome)) && celular.equals(pCelular)) {
                    boolean ok = db.updateAmigoMysql(id, nome, celular, 1, imagemEmString);
                    if (ok) {
                        salvar(id, nome, celular, 1, 1, imagemEmString);
                    } else {
                        this._status = false;
                    }
                }

            }
            cursor.close();
        }
        return this._status;
    }

    public boolean salvar(String nome, String celular, Integer status, Integer sincronizado, byte[] pImagemEmString)
    {
        return salvar(0, nome, celular, status, sincronizado, pImagemEmString);
    }

    public boolean salvar(int id, String nome, String celular, Integer status, Integer sincronizado, byte[] pImagemEmString)
    {
        ContentValues cv = new ContentValues();
        cv.put("Nome", nome);
        cv.put("Celular", celular);
        cv.put("Status", status);
        cv.put("Sincronizado", sincronizado);
        if (pImagemEmString != null) {
            cv.put("Foto", pImagemEmString);
        }

        if (id > 0) {
            return gw.getDatabase().update(TABLE_AMIGOS, cv, "ID = ?", new String[]{id + ""}) > 0;
        } else {
            return gw.getDatabase().insert(TABLE_AMIGOS, null, cv) > 0;
        }
    }

    public boolean deletar(int id) {
        return gw.getDatabase().delete(TABLE_AMIGOS, "ID = ?", new String[] { id + ""}) > 0;
    }

    public boolean deletar(int id, int status) {
        ContentValues cv = new ContentValues();
        cv.put("Status", status);
        return gw.getDatabase().update(TABLE_AMIGOS, cv, "ID = ?", new String[] {id + "" }) > 0;
    }

    public List<Amigo> retornarAmigos(int num) {
        List<Amigo> amigos = new ArrayList<>();
        Cursor cursor = null;
        Cursor cursorAux = null;
        Amigo amigo = new Amigo();
        Boolean ok = true;

        DB db = new DB();

        switch (num) {
            case 0:
                /** lista normal */
                cursor = gw.getDatabase().rawQuery("SELECT * FROM Amigos WHERE Status <= 1", null);
                break;
            case 1:
                /** Lista Deletados pela primeira vez */
                cursor = gw.getDatabase().rawQuery("SELECT * FROM Amigos WHERE Status = 2", null);
                break;
            case 2:
                /** Lista Deletados pela segunda vez */
                cursor = gw.getDatabase().rawQuery("SELECT * FROM Amigos WHERE Status = 3", null);
                break;
            case 3:
                /** Lista Amigos Mysql */
                this._mysql = true;
                try {
                    amigos = db.retornaAmigoMysql("SELECT * FROM MeusAmigos WHERE RA = '5041480'");
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case 4:
                /** Sincronização Local para Mysql */
                this._mysql = true;
                cursorAux = gw.getDatabase().rawQuery("SELECT * FROM Amigos", null);
                ok = sincronizarLocalMysql(cursorAux);
                if (ok) {
                    cursor = gw.getDatabase().rawQuery("SELECT * FROM Amigos WHERE Sincronizado = 1", null);
                }
                break;
            case 5:
                /** Sincronização Mysql para Local */
                this._mysql = true;
                cursorAux = gw.getDatabase().rawQuery("SELECT * FROM Amigos", null);
                try {
                    amigos = sincronizarMysqlLocal(cursorAux);
                } catch (Exception e) {
                    this._mensagem = e.getMessage();
                    this._status = false;
                }

                break;
        }

        if (cursor != null) {
            while (cursor.moveToNext()) {
                amigo.setId(cursor.getInt(cursor.getColumnIndex("ID")));
                amigo.setNome(cursor.getString(cursor.getColumnIndex("Nome")));
                amigo.setCelular(cursor.getString(cursor.getColumnIndex("Celular")));
                amigo.setStatus(cursor.getInt(cursor.getColumnIndex("Status")));
                amigo.setSincronizado(cursor.getInt(cursor.getColumnIndex("Sincronizado")));
                amigo.setImagemEmbyte(cursor.getBlob(cursor.getColumnIndex("Foto")));

                if (amigo.getImagemEmbyte() != null) {
                    amigo.setImagemEmBitmap(Auxilio.getImagemBytes(amigo.getImagemEmbyte()));
                    amigos.add(new Amigo(amigo.getId(), amigo.getNome(), amigo.getCelular(), amigo.getStatus(), amigo.getSincronizado(), amigo.getImagemEmBitmap()));
                } else {
                    amigo.setImagemEmBitmap(null);
                    amigos.add(new Amigo(amigo.getId(), amigo.getNome(), amigo.getCelular(), amigo.getStatus(), amigo.getSincronizado(), amigo.getImagemEmBitmap()));
                }

            }
            cursor.close();
        }
        return amigos;
    }

    public Amigo retornarUltimoAmigo()
    {
        Amigo amigo = new Amigo();
        Cursor cursor = gw.getDatabase().rawQuery("SELECT * FROM Amigos order by id desc", null);

        if (cursor.moveToFirst())
        {
            amigo.setId(cursor.getInt(cursor.getColumnIndex("ID")));
            amigo.setNome(cursor.getString(cursor.getColumnIndex("Nome")));
            amigo.setCelular(cursor.getString(cursor.getColumnIndex("Celular")));
            amigo.setStatus(cursor.getInt(cursor.getColumnIndex("Status")));
            amigo.setImagemEmbyte(cursor.getBlob(cursor.getColumnIndex("Foto")));
            if (amigo.getImagemEmbyte() != null) {
                amigo.setImagemEmBitmap(Auxilio.getImagemBytes(amigo.getImagemEmbyte()));
            }
            cursor.close();
            return new Amigo(amigo.getId(), amigo.getNome(), amigo.getCelular(), amigo.getStatus(), amigo.getSincronizado(), amigo.getImagemEmBitmap());
        }

        return null;
    }

    public boolean deleteMysql(String nome, String telefone) {
        DB db = new DB();
        boolean ok = true;
        try {
            ok = db.deletar("DELETE FROM MeusAmigos WHERE RA = '5041480' AND Nome = '"+ nome +"' AND Celular = '"+ telefone +"'");
            return ok;
        } catch (Exception e){
            e.printStackTrace();
            return ok;
        }
    }

    public boolean sincronizarLocalMysql(Cursor cursor) {
        List<Amigo> amigos = new ArrayList<>();
        Amigo amigo = new Amigo();
        DB db = new DB();
        boolean amigoDeletado = false;
        boolean ok = true;

        if (cursor != null) {
            while (cursor.moveToNext()) {
                amigo.setId(cursor.getInt(cursor.getColumnIndex("ID")));
                amigo.setNome(cursor.getString(cursor.getColumnIndex("Nome")));
                amigo.setCelular(cursor.getString(cursor.getColumnIndex("Celular")));
                amigo.setStatus(cursor.getInt(cursor.getColumnIndex("Status")));
                amigo.setSincronizado(cursor.getInt(cursor.getColumnIndex("Sincronizado")));
                amigo.setImagemEmbyte(cursor.getBlob(cursor.getColumnIndex("Foto")));

                ok = db.sincronizacaoMysql(amigo.getId(), amigo.getNome(), amigo.getCelular(), amigo.getStatus(), amigo.getImagemEmbyte());

                if (ok) {
                    switch (amigo.getStatus()) {
                        case 0:
                            amigo.setStatus(1);
                            amigo.setSincronizado(1);
                            salvar(amigo.getId(), amigo.getNome(), amigo.getCelular(), amigo.getStatus(), amigo.getSincronizado(), amigo.getImagemEmbyte());
                            break;
                        case 3:
                            amigoDeletado = true;
                            deletar(amigo.getId());
                            break;
                        default:
                            amigo.setSincronizado(1);
                            salvar(amigo.getId(), amigo.getNome(), amigo.getCelular(), amigo.getStatus(), amigo.getSincronizado(), amigo.getImagemEmbyte());
                            break;
                    }
                    if (amigoDeletado) {
                        amigoDeletado = false;
                    } else {
                        amigos.add(new Amigo(amigo.getId(), amigo.getNome(), amigo.getCelular(), amigo.getStatus(), amigo.getSincronizado(), amigo.getImagemEmBitmap()));
                    }
                } else {
                    this._status = false;
                }
            }
            cursor.close();
        }
        return this._status;
    }

    public List<Amigo> sincronizarMysqlLocal(Cursor cursor) {
        List<Amigo> amigosMysql = new ArrayList<>();
        List<Amigo> amigos = new ArrayList<>();
        Amigo amigo = new Amigo();
        DB db = new DB();
        boolean existeRegistro = false;

        amigosMysql = db.sincronizacaoMysqlLocal();

        if (amigosMysql != null) {
            for(Amigo c : amigosMysql) {
                int id = c.getId();
                cursor.moveToFirst();
                while (cursor.moveToNext()) {
                    int id2 = cursor.getInt(cursor.getColumnIndex("ID"));
                    if (id2 == id) {
                        existeRegistro = true;
                    }
                }
                cursor.close();

                if (!(existeRegistro)) {
                    amigo.setId(c.getId());
                    amigo.setNome(c.getNome());
                    amigo.setCelular(c.getCelular());
                    amigo.setStatus(0);
                    amigo.setSincronizado(1);
                    amigo.setImagemEmbyte(c.getImagemEmbyte());

                    amigos.add(new Amigo(amigo.getId(), amigo.getNome(), amigo.getCelular(), amigo.getStatus(), amigo.getSincronizado(), amigo.getImagemEmBitmap()));

                    boolean ok = db.updateAmigoMysql(amigo.getId(), amigo.getNome(), amigo.getCelular(), 0, amigo.getImagemEmbyte());
                    if (ok) {
                        salvar(amigo.getNome(), amigo.getCelular(), amigo.getStatus(), amigo.getSincronizado(), amigo.getImagemEmbyte());
                    }

                }
            }

        }
        return amigos;
    }
}