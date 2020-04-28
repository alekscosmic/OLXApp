package com.example.olxapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.olxapp.R;
import com.example.olxapp.helper.Permissoes;
import com.santalu.maskedittext.MaskEditText;

import java.util.Locale;

public class CadastrarAnuncioActivity extends AppCompatActivity {

    private EditText campoTitulo, campoDescricao;
    private CurrencyEditText campoValor;
    private MaskEditText campoTelefone;

    private String [] permissoes = new String[]{
          Manifest.permission.READ_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        //Validar as permissoes
        Permissoes.validarPermissoes(permissoes, this, 1);

        inicializarComponentes();

    }

    public void salvarAnuncio(View view){
        String valor = campoValor.getText().toString();
        String telefone= campoTelefone.getText().toString();
        Log.d("salvar", "salvarAnuncio " +telefone);

    }
    private void inicializarComponentes(){

        campoTitulo = findViewById(R.id.editTitulo);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);
        campoTelefone = findViewById(R.id.editTelefone);

        //Configura localidade para pt -> portugues BR -> Brasil
        Locale locale = new Locale("pt","BR");
        campoValor.setLocale(locale);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Verificando se o usuario deu permissao para acessar a galeria de imagens
        for(int permissaoResultado : grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();

            }
        }
    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as pemrissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
