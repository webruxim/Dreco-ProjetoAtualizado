package com.example.meusamigos2;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private int status = 0;
    private int sincronizado = 0;
    private boolean listarDeletatos, listarMysql, sincronizarMysqlLocal, sincronizarLocalMysql = false;
    public List<Amigo> amigos = new ArrayList<>();
    private byte[] fotoEmBytes;
    private boolean ListarDeletatosFisicos = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().hide();

        // Checagem se o usuario deu permissao para SMS
        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.SEND_SMS
        }, 1);

        configurarRecyclerView();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.includemain).setVisibility(View.INVISIBLE);
                findViewById(R.id.includecadastro).setVisibility(View.VISIBLE);
                findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                findViewById(R.id.include_amigos_listagem).setVisibility(View.INVISIBLE);
                findViewById(R.id.include_mensagens).setVisibility(View.INVISIBLE);

            }
        });

        CircleImageView fotoAvatar = (CircleImageView)findViewById(R.id.foto_avatar);
        fotoAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);
            }
        });

        Button btnSalvar = (Button)findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /** ligação dos objetos que tem na tela */
                EditText edtNome = (EditText)findViewById(R.id.edtNome);
                EditText edtCelular = (EditText)findViewById(R.id.edtCelular);
                CircleImageView fotoAvatar = (CircleImageView)findViewById(R.id.foto_avatar);

                /** Pegando dados */
                String nome = edtNome.getText().toString();
                String celular = edtCelular.getText().toString();

                /** Instanciando AmigoDAO */
                AmigoDAO dao = new AmigoDAO(getBaseContext());
                boolean ok;
                boolean ok2 = true;

                /** Se estiver edianto, entra no if */
                if (amigoEditado != null) {
                    if (fotoEmBytes != null) {
                        amigoEditado.setImagemEmbyte(fotoEmBytes);
                    }
                    ok = dao.salvar (amigoEditado.getId(), nome, celular, status, amigoEditado.getSincronizado(), amigoEditado.getImagemEmbyte());
                } else {
                    /** Se for um novo cadastro entra aqui */
                    ok = dao.salvar(nome, celular, status, sincronizado, fotoEmBytes);
                    if (ok) {
                        ok2 = dao.atualizarContatoMysql(nome, celular);
                    }
                }

                if (ok) {
                    Amigo amigo = dao.retornarUltimoAmigo();

                    if (amigoEditado != null) {
                        adapter.atualizarAmigo(amigo);
                        amigoEditado = null;
                    } else {
                        adapter.adicionarAmigo(amigo);
                    }

                    edtNome.setText("");
                    edtCelular.setText("");
                    fotoAvatar.setImageResource(R.drawable.ic_photo_camera_24);

                    Snackbar.make(view, "Amigo salvo com sucesso!", Snackbar.LENGTH_LONG)
                            .setAction("Inclusão", null).show();

                    findViewById(R.id.includemain).setVisibility(View.VISIBLE);
                    findViewById(R.id.includecadastro).setVisibility(View.INVISIBLE);
                    findViewById(R.id.fab).setVisibility(View.VISIBLE);
                    findViewById(R.id.include_amigos_listagem).setVisibility(View.VISIBLE);
                    findViewById(R.id.include_mensagens).setVisibility(View.INVISIBLE);
                } else {
                    Snackbar.make(view, "Erro ao gravar os dados do Amigo ["+nome+"]", Snackbar.LENGTH_LONG)
                            .setAction("Inclusão", null).show();
                }
            }
        });

        Button btnCancelar = (Button)findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.includemain).setVisibility(View.VISIBLE);
                findViewById(R.id.includecadastro).setVisibility(View.INVISIBLE);
                findViewById(R.id.fab).setVisibility(View.VISIBLE);
                findViewById(R.id.include_amigos_listagem).setVisibility(View.VISIBLE);
                findViewById(R.id.include_mensagens).setVisibility(View.INVISIBLE);
            }
        });

        Button btnEnviarSms = (Button)findViewById(R.id.btnEnviarSms);
        btnEnviarSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txtNome = (TextView)findViewById(R.id.txtNomeMensagem);
                TextView txtNumero = (TextView)findViewById(R.id.txtNumeroMensagem);
                TextView txtMensagem = (TextView)findViewById(R.id.txtMensagem);

                String numero = txtNumero.getText().toString();
                String mensagem = txtMensagem.getText().toString();

                SmsManager smsManager = SmsManager.getDefault();

                smsManager.sendTextMessage(numero, null, mensagem, null, null);

                Toast.makeText(MainActivity.this, "Mensagem Enviada", Toast.LENGTH_SHORT).show();

                txtNome.setText(null);
                txtNumero.setText(null);
                txtMensagem.setText(null);

                findViewById(R.id.includemain).setVisibility(View.INVISIBLE);
                findViewById(R.id.includecadastro).setVisibility(View.INVISIBLE);
                findViewById(R.id.fab).setVisibility(View.VISIBLE);
                findViewById(R.id.include_amigos_listagem).setVisibility(View.VISIBLE);
                findViewById(R.id.include_mensagens).setVisibility(View.INVISIBLE);
                amigoEditado = null;
            }
        });

        Button btnEnviarWhatsApp = (Button)findViewById(R.id.btnEnviarWhats);
        btnEnviarWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView txtNumero = (TextView)findViewById(R.id.txtNumeroMensagem);
                TextView txtMensagem = (TextView)findViewById(R.id.txtMensagem);

                String numero = txtNumero.getText().toString();
                String mensagem = txtMensagem.getText().toString();

                String url = "https://api.whatsapp.com/send?phone=" + "+55" + numero + "&text=" + "_" + mensagem + "_";

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                amigoEditado = null;
            }
        });

        Button btnCancelarMensagem = (Button)findViewById(R.id.btnCancelarMensagem);
        btnCancelarMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.includemain).setVisibility(View.VISIBLE);
                findViewById(R.id.includecadastro).setVisibility(View.INVISIBLE);
                findViewById(R.id.fab).setVisibility(View.VISIBLE);
                findViewById(R.id.include_amigos_listagem).setVisibility(View.VISIBLE);
                findViewById(R.id.include_mensagens).setVisibility(View.INVISIBLE);
                amigoEditado = null;
            }
        });

        Intent intent = getIntent();

        if (intent.hasExtra("amigoEditar"))
        {
            findViewById(R.id.includemain).setVisibility(View.INVISIBLE);
            findViewById(R.id.includecadastro).setVisibility(View.VISIBLE);
            findViewById(R.id.fab).setVisibility(View.INVISIBLE);
            findViewById(R.id.include_amigos_listagem).setVisibility(View.INVISIBLE);
            findViewById(R.id.include_mensagens).setVisibility(View.INVISIBLE);

            amigoEditado = (Amigo) intent.getSerializableExtra("amigoEditar");
            EditText edtNome = (EditText)findViewById(R.id.edtNome);
            EditText edtCelular = (EditText)findViewById(R.id.edtCelular);
            CircleImageView avatar = (CircleImageView)findViewById(R.id.foto_avatar);

            edtNome.setText(amigoEditado.getNome());
            edtCelular.setText(amigoEditado.getCelular());
            if (amigoEditado.getImagemEmbyte() != null) {
                avatar.setImageBitmap(Auxilio.getImagemBytes(amigoEditado.getImagemEmbyte()));
            }
        }

        if ((intent.hasExtra("mensagens")))
        {
            findViewById(R.id.includemain).setVisibility(View.INVISIBLE);
            findViewById(R.id.includecadastro).setVisibility(View.INVISIBLE);
            findViewById(R.id.fab).setVisibility(View.INVISIBLE);
            findViewById(R.id.include_amigos_listagem).setVisibility(View.INVISIBLE);
            findViewById(R.id.include_mensagens).setVisibility(View.VISIBLE);

            amigoEditado = (Amigo) intent.getSerializableExtra("mensagens");
            EditText txtNome = (EditText)findViewById(R.id.txtNomeMensagem);
            EditText txtNumero = (EditText)findViewById(R.id.txtNumeroMensagem);
            EditText txtMensagem = (EditText)findViewById(R.id.txtMensagem);

            SimpleMaskFormatter smF = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
            MaskTextWatcher mtW = new MaskTextWatcher(txtNumero, smF);
            txtNumero.addTextChangedListener(mtW);

            txtNome.setText(amigoEditado.getNome());
            txtNumero.setText((amigoEditado.getCelular()));
            txtMensagem.requestFocus();
            txtNome.setFocusable(false);
            txtNumero.setFocusable(false);
            amigoEditado = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.listar_amigos:
                listarDeletatos = false;
                listarMysql = false;
                ListarDeletatosFisicos = false;
                sincronizarMysqlLocal = false;
                sincronizarLocalMysql = false;
                findViewById(R.id.fab).setVisibility(View.VISIBLE);
                configurarRecyclerView();
                Toast.makeText(MainActivity.this, "Listar Amigos", Toast.LENGTH_LONG).show();
                break;
            case R.id.listar_amigos_deletados:
                listarDeletatos = true;
                listarMysql = false;
                ListarDeletatosFisicos = false;
                sincronizarMysqlLocal = false;
                sincronizarLocalMysql = false;
                findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                configurarRecyclerView();
                Toast.makeText(MainActivity.this, "Listar Amigos Deletados", Toast.LENGTH_LONG).show();
                break;
            case R.id.listar_amigos_deletados_fisico:
                /** Esse menu pode deletar depois */
                listarDeletatos = false;
                listarMysql = false;
                ListarDeletatosFisicos = true;
                sincronizarMysqlLocal = false;
                sincronizarLocalMysql = false;
                findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                configurarRecyclerView();
                Toast.makeText(MainActivity.this, "Listar Amigos Deletados Fisicos", Toast.LENGTH_LONG).show();
                break;
            case R.id.listar_amigos_mysql:
                listarDeletatos = false;
                listarMysql = true;
                ListarDeletatosFisicos = false;
                sincronizarMysqlLocal = false;
                sincronizarLocalMysql = false;
                findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                configurarRecyclerView();
                break;
            case R.id.sincronizar_local_para_mysql:
                listarDeletatos = false;
                listarMysql = false;
                ListarDeletatosFisicos = false;
                sincronizarMysqlLocal = false;
                sincronizarLocalMysql = true;
                findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                configurarRecyclerView();
                break;
            case R.id.sincronizar_mysql_para_local:
                listarDeletatos = false;
                listarMysql = false;
                ListarDeletatosFisicos = false;
                sincronizarMysqlLocal = true;
                sincronizarLocalMysql = false;
                configurarRecyclerView();
                break;
            case R.id.finalizar_app:
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    RecyclerView recyclerView;
    AmigoAdapter adapter;

    private void configurarRecyclerView ()
    {
        AmigoDAO dao = new AmigoDAO(this);

        recyclerView = (RecyclerView)findViewById(R.id.rcvAmigos);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        TextView tituloLista = (TextView)findViewById(R.id.txt_titulo_lista);

        if (listarDeletatos) {
            tituloLista.setText("Lista de Amigos Deletados");
            adapter = new AmigoAdapter(dao.retornarAmigos(1), 4, false);
        } else if (ListarDeletatosFisicos) {
            tituloLista.setText("Lista de Deletados Fisico");
            adapter = new AmigoAdapter(dao.retornarAmigos(2), 6, false);
        } else if (listarMysql) {
            adapter = new AmigoAdapter(dao.retornarAmigos(3), 5, true);
        } else if (sincronizarLocalMysql) {
            tituloLista.setText("Amigos Sincronizados com Mysql");
            adapter = new AmigoAdapter(dao.retornarAmigos(4), 5, true);
        } else if (sincronizarMysqlLocal) {
            tituloLista.setText("Lista Recuperar Amigos");
            adapter = new AmigoAdapter(dao.retornarAmigos(5), 4, true);
        } else {
            tituloLista.setText("Lista de Amigos");
            adapter = new AmigoAdapter(dao.retornarAmigos(0), 0, false);
        }

        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    Amigo amigoEditado = null;

    private int getIndex(Spinner spinner, String meuAmigo)
    {
        int indice = 0;
        int i = 0;

        for (i = 0; (i < spinner.getCount()) && (!spinner.getItemAtPosition(i).toString().equalsIgnoreCase(meuAmigo)); i++ );

        if (i == spinner.getCount())
        {
            indice = i;
        }

        return indice;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent dados) {

        super.onActivityResult(requestCode, resultCode, dados);
        if(requestCode == 1) {
            try {

                Bitmap fotoRegistrada = (Bitmap) dados.getExtras().get("data");
                CircleImageView fotoAvatar = (CircleImageView)findViewById(R.id.foto_avatar);
                fotoAvatar.setImageBitmap(fotoRegistrada);


                ByteArrayOutputStream streamDaFotoEmBytes = new ByteArrayOutputStream();
                fotoRegistrada.compress(Bitmap.CompressFormat.PNG, 50, streamDaFotoEmBytes);
                fotoEmBytes = streamDaFotoEmBytes.toByteArray();
                //fotoEmBytes = Auxilio.convertBitmapToString(fotoRegistrada);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}