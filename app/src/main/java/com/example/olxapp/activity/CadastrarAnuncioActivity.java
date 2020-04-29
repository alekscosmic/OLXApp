package com.example.olxapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.olxapp.R;
import com.example.olxapp.helper.ConfiguracaoFirebase;
import com.example.olxapp.helper.Permissoes;
import com.example.olxapp.model.Anuncio;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskedittext.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CadastrarAnuncioActivity extends AppCompatActivity 
implements View.OnClickListener {

    private EditText campoTitulo, campoDescricao;
    private ImageView imagem1, imagem2, imagem3;
    private CurrencyEditText campoValor;
    private MaskEditText campoTelefone;
    private Spinner campoEstado, campoCategoria;
    private Anuncio anuncio;
    private StorageReference storage;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private List<String> listaFotosRecuperadas = new ArrayList<>();//Caminho das fotos dentro do aparelho do usuario
    private List<String> listaURLFotos = new ArrayList<>();//Caminho das imagens já salvas no firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        //Configurações iniciais
       storage = ConfiguracaoFirebase.getFirebaseStorage();

        //Validar as permissoes
        Permissoes.validarPermissoes(permissoes, this, 1);

        inicializarComponentes();
        carregarDadosSpinner();

    }


    public void salvarAnuncio() {

      /*
      *Salvar imagem no Storage
      */
      for(int i = 0; i < listaFotosRecuperadas.size(); i++){
          String urlImagem = listaFotosRecuperadas.get(i);
          int tamanhoLista = listaFotosRecuperadas.size();
          salvarFotoStorage(urlImagem, tamanhoLista, i);

      }

    }

    private void salvarFotoStorage(String urlString, final int totalFotos, int contador){

        //Criar nó no storage
        StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem"+contador);

        //Fazer upload do arquivo
        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                String urlConvertida = firebaseUrl.toString();

                listaURLFotos.add(urlConvertida);
                        if(totalFotos == listaURLFotos.size()){
                            anuncio.setFotos(listaURLFotos);
                            anuncio.salvar();
                        }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                exibirMensagemErro("Falha ao fazer upload !");
                Log.i ("INFO ", "Falha ao fazer upload: "+e.getMessage());
            }
        });
    }

    private Anuncio configurarAnuncio(){

        String estado = campoEstado.getSelectedItem().toString();
        String categoria = campoCategoria.getSelectedItem().toString();
        String titulo = campoTitulo.getText().toString();
        String valor = String.valueOf(campoValor.getRawValue());
        String telefone = campoTelefone.getText().toString();
        String descricao = campoDescricao.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setEstado( estado );
        anuncio.setCategoria(categoria);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setTelefone( telefone );
        anuncio.setDescricao(descricao);

        return anuncio;
    }

    public void validarDadosAnuncio(View view) {
        /*Recuperando dados preenchidos
        String fone = "";
        String estado = campoEstado.getSelectedItem().toString();
        String categoria = campoCategoria.getSelectedItem().toString();
        String titulo = campoTitulo.getText().toString();
        String valor = String.valueOf(campoValor.getRawValue());
        String telefone = campoTelefone.getText().toString();
        if(campoTelefone.getRawText() != null){
            fone = campoTelefone.getRawText().toString();
        }
        String descricao = campoDescricao.getText().toString();
        */
       anuncio = configurarAnuncio();
       /*
        String fone = "";
        if(campoTelefone.getRawText() != null){
            fone = campoTelefone.getRawText().toString();
        }
        */
        //Vrificando se os campos estão preenchidos
        if (listaFotosRecuperadas.size() != 0) {//Usuario precisa pelo menos adicionar uma imagem para salvar o anuncio
            if (!anuncio.getEstado().isEmpty()) {
                if (!anuncio.getCategoria().isEmpty()) {
                    if (!anuncio.getTitulo().isEmpty()) {
                        if (!anuncio.getValor().isEmpty() && !anuncio.getValor().equals("0")) {
                           //if (!anuncio.getTelefone().isEmpty() && fone.length() >= 10) {
                            if (!anuncio.getTelefone().isEmpty()) {

                                if (!anuncio.getDescricao().isEmpty()) {

                                    salvarAnuncio();

                                } else {
                                    exibirMensagemErro("Preencha o campo descrição!");
                                }
                            } else {
                                exibirMensagemErro("Preencha o campo telefone!");
                            }
                        } else {
                            exibirMensagemErro("Preencha o campo valor!");
                        }
                    } else {
                        exibirMensagemErro("Preencha o campo título!");
                    }
                } else {
                    exibirMensagemErro("Preencha o campo categoria!");
                }
            } else {
                exibirMensagemErro("Preencha o campo estado!");
            }

        } else {
            exibirMensagemErro("Selecione ao menos uma foto! ");
        }

    }

    private void exibirMensagemErro(String mensagem) {

        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        // Como isso qualquer item clicado na tela do activity_cadastrar_anuncio saberemos usando parametro v
        switch (v.getId()) {//Agora eu consigo pegar o ID do item q foi clicado.
            case R.id.imageCadastro1:
                escolherImagem(1);
                break;

            case R.id.imageCadastro2:
                escolherImagem(2);
                break;

            case R.id.imageCadastro3:
                escolherImagem(3);
                break;
        }

    }

    public void escolherImagem(int requestCode) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode); //Com isso eu consigo saber qual imagem esta sendo chamada
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            //Recuperar imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //Configura imagem no ImageView
            if (requestCode == 1) {
                imagem1.setImageURI(imagemSelecionada);
            } else if (requestCode == 2) {
                imagem2.setImageURI(imagemSelecionada);
            } else if (requestCode == 3) {
                imagem3.setImageURI(imagemSelecionada);
            }
            listaFotosRecuperadas.add(caminhoImagem);

        }
    }

    private void carregarDadosSpinner() {

       /* String [] estados = new String[]{
          "SP","MT","RJ"
        };
       */
        //Configura spinner de estados
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                estados
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoEstado.setAdapter(adapter);

        //Configura spinner de categorias
        String[] categorias = getResources().getStringArray(R.array.categorias);
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                categorias
        );
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoCategoria.setAdapter(adapterCategoria);

    }

    private void inicializarComponentes() {

        campoTitulo = findViewById(R.id.editTitulo);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);
        campoTelefone = findViewById(R.id.editTelefone);
        campoEstado = findViewById(R.id.spinnerEstado);
        campoCategoria = findViewById(R.id.spinnerCategoria);
        imagem1 = findViewById(R.id.imageCadastro1);
        imagem2 = findViewById(R.id.imageCadastro2);
        imagem3 = findViewById(R.id.imageCadastro3);
        imagem1.setOnClickListener(this);// A propria classe que vai gerenciar o clique
        imagem2.setOnClickListener(this);// A propria classe que vai gerenciar o clique
        imagem3.setOnClickListener(this);// A propria classe que vai gerenciar o clique

        //Configura localidade para pt -> portugues BR -> Brasil
        Locale locale = new Locale("pt", "BR");
        campoValor.setLocale(locale);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Verificando se o usuario deu permissao para acessar a galeria de imagens
        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();

            }
        }
    }

    private void alertaValidacaoPermissao() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as pemrissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
