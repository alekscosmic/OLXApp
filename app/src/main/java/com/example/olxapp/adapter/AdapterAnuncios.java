package com.example.olxapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olxapp.R;
import com.example.olxapp.model.Anuncio;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterAnuncios extends RecyclerView.Adapter<AdapterAnuncios.MyViewHolder> {

    private List<Anuncio> anuncios;
    private Context context;

    public AdapterAnuncios(List<Anuncio> anuncios, Context context) {
        this.anuncios = anuncios;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_anuncio, parent, false);//Vamos criar o layout
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

       Anuncio anuncio = anuncios.get(position);
        //Recuperar o titulo e valor
        holder.titulo.setText(anuncio.getTitulo());
        holder.valor.setText(anuncio.getValor());

        // Pega a primeira imagem da lista
        List<String> urlFotos = anuncio.getFotos();
        String urlCapa = urlFotos.get(0);// 0 pq sempre vamos pegar a primeira imagem

        //Agora para carregar a imagem
        Picasso.get().load(urlCapa).into(holder.foto);//Com isso já teremos a imagem configurada


    }

    @Override
    public int getItemCount() {
        return anuncios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titulo;
        TextView valor;
        ImageView foto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.textTitulo);
            valor = itemView.findViewById(R.id.textPreco);
            foto = itemView.findViewById(R.id.imageAnuncio);
        }
    }
}
