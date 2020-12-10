package com.example.meusamigos2;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AmigoHolder extends RecyclerView.ViewHolder{
    public TextView txvAmigo;
    public TextView txvAmigoCelular;
    public ImageButton btnTelegram;
    public ImageButton btnZap;
    public ImageButton btnSms;
    public ImageButton btnLigar;
    public ImageButton btnEditar;
    public ImageButton btnRemover;
    public CircleImageView avatar;

    public AmigoHolder(View itemView)
    {
        super(itemView);
        txvAmigo = (TextView)itemView.findViewById(R.id.txvAmigo);
        txvAmigoCelular = (TextView)itemView.findViewById(R.id.txvAmigoCelular);
        btnTelegram = (ImageButton)itemView.findViewById(R.id.btnTelegran);
        btnSms = (ImageButton)itemView.findViewById(R.id.btnSms);
        btnZap = (ImageButton)itemView.findViewById(R.id.btnZap);
        btnLigar = (ImageButton)itemView.findViewById(R.id.btnLigar);
        btnEditar = (ImageButton)itemView.findViewById(R.id.btnEditar);
        btnRemover = (ImageButton)itemView.findViewById(R.id.btnRemover);
        avatar = (CircleImageView)itemView.findViewById(R.id.imageViewAmigo);
    }
}