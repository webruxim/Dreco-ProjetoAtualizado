package com.example.meusamigos2;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.sql.Blob;

public class Amigo implements Serializable
{
    private int id;
    private String nome;
    private String celular;
    private int status;
    private int sincronizado;
    private byte[] imagemEmbyte;
    private Bitmap imagemEmBitmap;

    public Amigo (int id, String nome, String celular, int status, int sincronizado, byte[] imagemEmbyte)
    {
        super();
        this.id = id;
        this.nome = nome;
        this.celular = celular;
        this.status = status;
        this.imagemEmbyte = imagemEmbyte;
        this.sincronizado = sincronizado;
    }

    public Amigo() {
        super();
    }

    public Amigo(int id, String nome, String celular, int status, int sincronizado, Bitmap imagemEmBitmap) {
        super();
        this.id = id;
        this.nome = nome;
        this.celular = celular;
        this.status = status;
        this.sincronizado = sincronizado;
        this.imagemEmBitmap = imagemEmBitmap;
    }

    public int getId()
    {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome()
    {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCelular()
    {
        return this.celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public int getStatus()
    {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }

    public byte[] getImagemEmbyte() {
        return imagemEmbyte;
    }

    public void setImagemEmbyte(byte[] imagemEmbyte) {
        this.imagemEmbyte = imagemEmbyte;
        // if (this.imagemEmbyte != null) {
        //      setImagemEmBitmap(Auxilio.getImagemBytes(this.imagemEmbyte));
        // }
    }

    public Bitmap getImagemEmBitmap() {
        return imagemEmBitmap;
    }

    public void setImagemEmBitmap(Bitmap imagemEmBitmap) {
        this.imagemEmBitmap = imagemEmBitmap;
    }

    @Override
    public boolean equals(Object o)
    {
        return this.id == ((Amigo)o).id;
    }

    @Override
    public int hashCode()
    {
        return this.id;
    }
}