package com.example.meusamigos2;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;

import androidx.core.graphics.BitmapCompat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Bitmap.CompressFormat.PNG;
import static java.nio.charset.StandardCharsets.UTF_8;

public class DB extends _Default implements Runnable {

    private Connection conn;
    private String host = "108.179.253.78";
    private String db = "dreco836_Amigos";
    private int port = 3306;
    private String user = "dreco836_amigos";
    private String pass = "amigos2020";
    private String url = "jdbc:mysql://%s:%d/%s?characterEncoding=latin1";
    DbGateway gw;

    public DB() {
        super();
        this.url = String.format(this.url, this.host, this.port, this.db);
    }

    public DB(Context context) {
        gw = DbGateway.getInstance(context);
    }

    @Override
    public void run() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            this.conn = DriverManager.getConnection(this.url, this.user, this.pass);

        } catch (Exception e) {
            this._mensagem = e.getMessage();
            this._status = false;
        }
    }

    public void conectaMysql() {
        Thread thread = new Thread(this);
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            this._mensagem = e.getMessage();
            this._status = false;
        }
    }

    public void disconectaMysql() {
        if (this.conn != null) {
            try {
                this.conn.close();
            } catch (Exception e) {

            } finally {
                this.conn = null;
            }
        }
    }

    public List<Amigo> sincronizacaoMysqlLocal() {
        List<Amigo> amigos = new ArrayList<>();
        String query;
        String queryTipo;
        ResultSet resultSet = null;
        Statement statement = null;

        this.conectaMysql();

        try {
            statement = conn.createStatement();

            query = "SELECT * FROM MeusAmigos WHERE RA = '5041480'";
            queryTipo = "SELECT";

            resultSet = new DBExecute(this.conn, query, queryTipo).execute().get();

            while (resultSet.next()) {
                int id = resultSet.getInt("Id");
                String nome = resultSet.getString("Nome");
                String celular = resultSet.getString("Celular");
                int status = resultSet.getInt("Status");
                byte[] foto = resultSet.getBytes("Foto");
                Bitmap bitmap = Auxilio.getImagemBytes(foto);

                amigos.add(new Amigo(id, nome, celular, status, 1, bitmap));
            }
            resultSet = null;
        } catch (Exception e) {
            this._mensagem = e.getMessage();
            this._status = false;
            e.printStackTrace();
        } finally {
            this.disconectaMysql();
        }
        return amigos;
    }

    public List<Amigo> retornaAmigoMysql(String query) {
        List<Amigo> amigos = new ArrayList<>();
        Amigo amigo = new Amigo();
        Statement statement = null;
        String queryTipo = "SELECT";
        this.conectaMysql();
        ResultSet resultSet = null;
        try {
            statement = conn.createStatement();
            /** DBExecute recebe o ultimo parametro tipo de query, se for select é true,
             * false é para o restante (insert, delete, update)
             */
            resultSet = new DBExecute(this.conn, query, queryTipo).execute().get();
            while (resultSet.next()) {
                amigo.setId(resultSet.getInt("Id"));
                amigo.setNome(resultSet.getString("Nome"));
                amigo.setCelular(resultSet.getString("Celular"));
                amigo.setStatus(resultSet.getInt("Status"));
                amigo.setImagemEmbyte(resultSet.getBytes("Foto"));

                if (resultSet.getBytes("Foto") != null) {
                    amigo.setImagemEmBitmap(Auxilio.getImagemBytes(amigo.getImagemEmbyte()));
                }


                amigos.add(new Amigo(amigo.getId(), amigo.getNome(), amigo.getCelular(), amigo.getStatus(), 1, amigo.getImagemEmBitmap()));
            }
        } catch (Exception e) {
            this._mensagem = e.getMessage();
            this._status = false;
            e.printStackTrace();
        } finally {
            this.disconectaMysql();
        }
        return amigos;
    }

    public boolean insertAmigoMysql(int pId, String pNome, String pCelular, int pStatus, byte[] pImagemEmByte) {
        String query;
        String queryTipo;
        int statusAtualizado = 0;
        ResultSet resultSet = null;
        ResultSet resultSet2 = null;
        boolean existeRegistro = false;
        Statement statement = null;

        this.conectaMysql();

        try {
            statement = conn.createStatement();

            query = "SELECT * FROM MeusAmigos WHERE RA = '5041480'";
            queryTipo = "SELECT";

            resultSet = new DBExecute(this.conn, query, queryTipo).execute().get();

            while (resultSet.next()) {
                int id = resultSet.getInt("Id");
                String nome = resultSet.getString("Nome");

                if (id == pId) {
                    existeRegistro = true;
                    break;
                }
            }

            if (existeRegistro) {
                resultSet = null;
                query = "UPDATE MeusAmigos SET Nome = '" + pNome + "', Foto = '" + pImagemEmByte + "', " +
                        "Celular = '" + pCelular + "', Status = '" + pStatus + "' " +
                        "WHERE RA = '5041480' AND Id = '" + pId + "'";
                queryTipo = "UPDATE";
                resultSet = new DBExecute(this.conn, query, queryTipo).execute().get();
            } else {
                resultSet = null;
                query = "INSERT INTO MeusAmigos (RA, Id, Nome, Apelido, Foto, " +
                        "Dt_Nascimento, Sexo, Email, Celular, WhatsAPP, Telegram, Facebook, " +
                        "Instagram, Twitter, Conexao, Excluido, Status) " +
                        "VALUES ('5041480', '" + pId + "', '" + pNome + "', 'x','" + pImagemEmByte + "', '0', '0', 'x', " +
                        "'" + pCelular + "', '0', '0', 'x', 'x', 'x', '0', '0', '" + pStatus + "')";
                queryTipo = "INSERT";
                resultSet = new DBExecute(this.conn, query, queryTipo).execute().get();
            }
            resultSet = null;
            resultSet2 = null;
        } catch (Exception e) {
            this._mensagem = e.getMessage();
            this._status = false;
        } finally {
            this.disconectaMysql();
        }
        return this._status;
    }

    public boolean updateAmigoMysql(int pId, String pNome, String pCelular, int pStatus, byte[] pImagemEmString) {
        this.conectaMysql();
        String queryTipo = "";
        String query;
        ResultSet resultSet = null;
        Statement statement = null;

        try {
            statement = conn.createStatement();
            query = "UPDATE MeusAmigos SET Status = '" + pStatus + "' WHERE RA = '5041480' AND Id = '" + pId + "'";
            queryTipo = "UPDATE";
            resultSet = new DBExecute(this.conn, query, queryTipo).execute().get();
        } catch (Exception e) {
            this._mensagem = e.getMessage();
            this._status = false;
        } finally {
            this.disconectaMysql();
        }
        return this._status;
    }

    public boolean deletar(String query) {
        this.conectaMysql();
        String queryTipo = "DELETE";
        ResultSet resultSet = null;
        try {
            resultSet = new DBExecute(this.conn, query, queryTipo).execute().get();
            return true;
        } catch (Exception e) {
            this._mensagem = e.getMessage();
            this._status = false;
            return this._status;
        }finally {
            this.disconectaMysql();
        }

    }

    public boolean sincronizacaoMysql(int pId, String pNome, String pCelular, int pStatus, byte[] imagemEmByte) {
        String query;
        String queryTipo;
        int statusAtualizado = 0;
        ResultSet resultSet = null;
        ResultSet resultSet2 = null;
        boolean existeRegistro = false;
        Statement statement = null;

        this.conectaMysql();

        try {
            statement = conn.createStatement();

            query = "SELECT * FROM MeusAmigos WHERE RA = '5041480'";
            queryTipo = "SELECT";

            resultSet = new DBExecute(this.conn, query, queryTipo).execute().get();

            while (resultSet.next()) {
                int id = resultSet.getInt("Id");
                String nome = resultSet.getString("Nome");

                if (id == pId) {
                    existeRegistro = true;
                    break;
                }
            }
            if (!existeRegistro) {
                if (pStatus == 0) {
                    pStatus = 1;
                }
                query = "INSERT INTO MeusAmigos (RA, Id, Nome, Apelido, Foto, " +
                        "Dt_Nascimento, Sexo, Email, Celular, WhatsAPP, Telegram, Facebook, " +
                        "Instagram, Twitter, Conexao, Excluido, Status) " +
                        "VALUES ('5041480', '" + pId + "', '" + pNome + "', 'x','" + imagemEmByte + "', '0', '0', 'x', " +
                        "'" + pCelular + "', '0', '0', 'x', 'x', 'x', '0', '0', '" + pStatus + "')";

                queryTipo = "INSERT";
                resultSet2 = new DBExecute(this.conn, query, queryTipo).execute().get();
            } else {
                query = "UPDATE MeusAmigos SET Nome = '" + pNome + "', Foto = '" + imagemEmByte + "', " +
                        "Celular = '" +  pCelular + "', Status = '" + pStatus + "' " +
                        "WHERE RA = '5041480' AND Id = '" + pId + "'";
                queryTipo = "UPDATE";
                resultSet2 = new DBExecute(this.conn, query, queryTipo).execute().get();
            }
            resultSet = null;
            resultSet2 = null;
        } catch (Exception e) {
            this._mensagem = e.getMessage();
            this._status = false;
            e.printStackTrace();
        } finally {
            this.disconectaMysql();
        }
        return this._status;
    }
}