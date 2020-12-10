package com.example.meusamigos2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.util.List;


public class AmigoAdapter extends RecyclerView.Adapter<AmigoHolder> {

    private final List<Amigo> amigos;
    private int escondeBotoes;
    public boolean mysql = false;

    public AmigoAdapter(List<Amigo> amigos, int botoes, boolean mysql) {
        this.amigos = amigos;
        this.escondeBotoes = botoes;
        this.mysql = mysql;
    }

    @Override
    public AmigoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AmigoHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.amigos_dados, parent, false));
    }

    private Activity getActivity(View v) {
        Context c = v.getContext();

        while (c instanceof ContextWrapper) {
            if (c instanceof ContextWrapper) {
                return (Activity) c;
            }
            c = ((ContextWrapper) c).getBaseContext();
        }
        return null;
    }


    @Override
    public void onBindViewHolder(final AmigoHolder holder, int posicao) {
        holder.txvAmigo.setText(amigos.get(posicao).getNome());
        holder.txvAmigoCelular.setText(amigos.get(posicao).getCelular());

        if (amigos.get(posicao).getImagemEmBitmap() == null) {
            holder.avatar.setImageResource(R.drawable.ic_photo_camera_24);
        } else {
            holder.avatar.setImageBitmap(amigos.get(posicao).getImagemEmBitmap());
        }

        switch (escondeBotoes) {
            case 0:
                holder.btnTelegram.setVisibility(View.VISIBLE);
                holder.btnSms.setVisibility(View.VISIBLE);
                holder.btnZap.setVisibility(View.VISIBLE);
                holder.btnLigar.setVisibility(View.VISIBLE);
                holder.btnEditar.setVisibility(View.VISIBLE);
                holder.btnRemover.setVisibility(View.VISIBLE);
                holder.btnEditar.setImageResource(R.drawable.ic_restore_24);
                break;
            case 1:
                holder.btnTelegram.setVisibility(View.INVISIBLE);
                holder.btnEditar.setImageResource(R.drawable.ic_restore_24);
                break;
            case 2:
                holder.btnTelegram.setVisibility(View.INVISIBLE);
                holder.btnSms.setVisibility(View.INVISIBLE);
                holder.btnEditar.setImageResource(R.drawable.ic_restore_24);
                break;
            case 3:
                holder.btnTelegram.setVisibility(View.INVISIBLE);
                holder.btnSms.setVisibility(View.INVISIBLE);
                holder.btnZap.setVisibility(View.INVISIBLE);
                holder.btnEditar.setImageResource(R.drawable.ic_restore_24);
                break;
            case 4:
                holder.btnTelegram.setVisibility(View.INVISIBLE);
                holder.btnSms.setVisibility(View.INVISIBLE);
                holder.btnZap.setVisibility(View.INVISIBLE);
                holder.btnLigar.setVisibility(View.INVISIBLE);
                holder.btnEditar.setImageResource(R.drawable.ic_restore_24);
                break;
            case 5:
                holder.btnTelegram.setVisibility(View.INVISIBLE);
                holder.btnSms.setVisibility(View.INVISIBLE);
                holder.btnZap.setVisibility(View.INVISIBLE);
                holder.btnLigar.setVisibility(View.INVISIBLE);
                holder.btnEditar.setVisibility(View.INVISIBLE);
                break;
            case 6:
                holder.btnTelegram.setVisibility(View.INVISIBLE);
                holder.btnSms.setVisibility(View.INVISIBLE);
                holder.btnZap.setVisibility(View.INVISIBLE);
                holder.btnLigar.setVisibility(View.INVISIBLE);
                holder.btnEditar.setVisibility(View.INVISIBLE);
                holder.btnRemover.setVisibility(View.INVISIBLE);
                break;
        }

        final Amigo amigo = amigos.get(posicao);

        holder.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mysql) {

                    final View view = v;
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                    if (amigo.getStatus() == 3) {
                        builder.setTitle("Confirmação de Resgate")
                                .setMessage("Tem certeza de que deseja recuperar seu amigo?")
                                .setPositiveButton("Resgatar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        AmigoDAO dao = new AmigoDAO(view.getContext());

                                        boolean ok = dao.salvar(amigo.getId(), amigo.getNome(), amigo.getCelular(), 0, 1, amigo.getImagemEmbyte());

                                        if (ok) {
                                            deletarAmigo(amigo);
                                            Snackbar.make(view, "Amigo recuperado! :-)) \\o/  \\o/", Snackbar.LENGTH_LONG)
                                                    .setAction("Ação", null).show();
                                        } else {
                                            Snackbar.make(view, "Erro ao recuperar o amigo!", Snackbar.LENGTH_LONG)
                                                    .setAction("Ação", null).show();
                                        }
                                    }
                                })
                                .setNegativeButton("Cancelar", null)
                                .create()
                                .show();
                    }
                } else {

                    final View view = v;
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                    if (amigo.getStatus() == 2) {
                        builder.setTitle("Confirmação de Resgate")
                                .setMessage("Tem certeza de que deseja recuperar seu amigo?")
                                .setPositiveButton("Resgatar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        AmigoDAO dao = new AmigoDAO(view.getContext());

                                        boolean ok = dao.salvar(amigo.getId(), amigo.getNome(), amigo.getCelular(), amigo.getStatus(), amigo.getSincronizado(), amigo.getImagemEmbyte());

                                        if (ok) {
                                            deletarAmigo(amigo);
                                            Snackbar.make(view, "Amigo recuperado! :-))  \\o/   \\o/", Snackbar.LENGTH_LONG)
                                                    .setAction("Ação", null).show();
                                        } else {
                                            Snackbar.make(view, "Erro ao recuperar o amigo!", Snackbar.LENGTH_LONG)
                                                    .setAction("Ação", null).show();
                                        }
                                    }
                                })
                                .setNegativeButton("Cancelar", null)
                                .create()
                                .show();
                    } else {
                        if (amigo.getImagemEmBitmap() != null) {
                            amigo.setImagemEmbyte(Auxilio.getImagemBitmap(amigo.getImagemEmBitmap()));
                            amigo.setImagemEmBitmap(null);
                        }
                        Activity activity = getActivity(view);
                        Intent intent = activity.getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("amigoEditar", amigo);
                        activity.finish();
                        activity.startActivity(intent);
                    }
                }

            }
        });

        holder.btnRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mysql) {
                    final View view = v;
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                    builder.setTitle("Confirmação Exclusão no Mysql")
                            .setMessage("Tem certeza de que deseja excluir seu amigo da nuvem?")
                            .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    AmigoDAO dao = new AmigoDAO(view.getContext());

                                    boolean ok;

                                    String nome = String.valueOf(holder.txvAmigo.getText());
                                    String telefone = String.valueOf(holder.txvAmigoCelular.getText());

                                    ok = dao.deleteMysql(nome, telefone);

                                    if (ok) {
                                        deletarAmigo(amigo);
                                        Snackbar.make(view, "Amigo excluído! :-(((", Snackbar.LENGTH_LONG)
                                                .setAction("Ação", null).show();
                                    } else {
                                        Snackbar.make(view, "Erro ao excluir o amigo!", Snackbar.LENGTH_LONG)
                                                .setAction("Ação", null).show();
                                    }
                                }
                            })
                            .setNegativeButton("Cancelar", null)
                            .create()
                            .show();
                } else {
                    final View view = v;
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                    builder.setTitle("Confirmação de Exclusão")
                            .setMessage("Tem certeza de que deseja excluir seu amigo?")
                            .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    AmigoDAO dao = new AmigoDAO(view.getContext());

                                    boolean ok;
                                    if (amigo.getStatus() == 2) {
                                        ok = dao.deletar(amigo.getId(), 3);
                                    } else {
                                        ok = dao.deletar(amigo.getId(), 2);
                                    }

                                    if (ok) {
                                        deletarAmigo(amigo);
                                        Snackbar.make(view, "Amigo excluído! :-(((", Snackbar.LENGTH_LONG)
                                                .setAction("Ação", null).show();
                                    } else {
                                        Snackbar.make(view, "Erro ao excluir o amigo!", Snackbar.LENGTH_LONG)
                                                .setAction("Ação", null).show();
                                    }
                                }
                            })
                            .setNegativeButton("Cancelar", null)
                            .create()
                            .show();
                }
            }
        });

        holder.btnLigar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numero = holder.txvAmigoCelular.getText().toString();

                Uri uri = Uri.parse("tel:" + numero);

                Activity activity = getActivity(v);
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.finish();
                activity.startActivity(intent);
            }
        });

        holder.btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amigo.setImagemEmBitmap(null);
                Activity activity = getActivity(v);
                Intent intent = activity.getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("mensagens", amigo);
                activity.finish();
                activity.startActivity(intent);
            }
        });

        holder.btnZap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amigo.setImagemEmBitmap(null);
                Activity activity = getActivity(v);
                Intent intent = activity.getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("mensagens", amigo);
                activity.finish();
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return amigos != null ? amigos.size() : 0;
    }

    public void adicionarAmigo(Amigo amigo) {
        amigos.add(amigo);
        notifyItemInserted(getItemCount());
    }

    public void atualizarAmigo(Amigo amigo) {
        amigos.set(amigos.indexOf(amigo), amigo);
        notifyItemChanged(amigos.indexOf(amigo));
    }

    public void deletarAmigo(Amigo amigo) {
        int posicao = amigos.indexOf(amigo);
        amigos.remove(posicao);
        notifyItemRemoved(posicao);
    }
}