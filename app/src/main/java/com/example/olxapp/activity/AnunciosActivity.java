package com.example.olxapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.olxapp.R;
import com.example.olxapp.adapter.AdapterAnuncios;
import com.example.olxapp.helper.ConfiguracaoFirebase;
import com.example.olxapp.model.Anuncio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerAnunciosPublicos;
    private Button buttonRegiao, buttonCategoria;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> listaAnuncios = new ArrayList<>();
    private DatabaseReference anunciosPublicosRef;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        //Inicializar Componentes
        inicializarComponentes();

        //Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase().child("anuncios");


        //Configurando Recyclerview
        recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnunciosPublicos.setHasFixedSize(true);
        adapterAnuncios =  new AdapterAnuncios(listaAnuncios, this);
        recyclerAnunciosPublicos.setAdapter(adapterAnuncios);

        recuperarAnunciosPublicos();

    }

    public void recuperarAnunciosPublicos(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando Anúncio")
                .setCancelable(false)
                .build();
        dialog.show();

        listaAnuncios.clear();
        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Recuperando todos os itens

                for(DataSnapshot estados : dataSnapshot.getChildren()){
                    for(DataSnapshot categorias: estados.getChildren()){
                        for(DataSnapshot anuncios: categorias.getChildren()){

                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            listaAnuncios.add(anuncio);
                        }
                    }
                }
                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {// Esse onPrepareOptionsMenu é chamado antes do menu ser exibido, com isso eu posso mudar

        if(autenticacao.getCurrentUser() == null){//Usuario deslogado
               menu.setGroupVisible(R.id.group_deslogado, true);

        }else{//Usuario logado
            menu.setGroupVisible(R.id.group_logado, true);

        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_cadastrar :
                startActivity(new Intent(getApplicationContext(),CadastroActivity.class));
                break;

            case R.id.menu_sair:
                 autenticacao.signOut();
                 invalidateOptionsMenu();
                 break;

            case R.id.menu_anuncios:
                startActivity(new Intent(getApplicationContext(),MeusAnunciosActivity.class));
                break;


        }

        return super.onOptionsItemSelected(item);
    }
    public void inicializarComponentes(){

        recyclerAnunciosPublicos = findViewById(R.id.recyclerAnunciosPublicos);
       // buttonRegiao = findViewById(R.id.buttonRegiao);
       // buttonCategoria = findViewById(R.id.buttonCategoria);

    }
}
