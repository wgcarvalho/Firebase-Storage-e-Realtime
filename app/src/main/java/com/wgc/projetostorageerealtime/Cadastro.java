package com.wgc.projetostorageerealtime;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class Cadastro extends AppCompatActivity {
    private ImageView ivSimbolo;
    private EditText editNome, editCH;
    private Button btnAcao, btnCancelar;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private boolean cursoNovo = true;
    private Curso cursoEscolhido;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        getCursoEnviado();
        componentesTela();
        eventosClick();
    }

    private void getCursoEnviado() {
        Intent intent = getIntent();
        cursoEscolhido  = new Curso();
        cursoEscolhido.setNome(intent.getStringExtra("cursoNome"));
        cursoEscolhido.setUid(intent.getStringExtra("cursoUid"));
        cursoEscolhido.setCargaHoraria(intent.getIntExtra("cursoCH",0));

    }

    private void eventosClick() {
        btnAcao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivSimbolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selecionarFoto();
            }
        });
        btnAcao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarDados();
            }
        });
    }

    private void enviarDados() {

        if (!cursoNovo){
            deleteDados(cursoEscolhido.getUid());
        }

        final String uidImagem = UUID.randomUUID().toString();

        Bitmap bitmap = ((BitmapDrawable)ivSimbolo.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG,100, byteArrayOutputStream);
        byte[] imagem = byteArrayOutputStream.toByteArray();

        StorageReference imgRef = storageReference.child(uidImagem+".jpeg");
        UploadTask uploadTask = imgRef.putBytes(imagem);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                salvarDados(uidImagem);
            }
        });
    }

    private void deleteDados(String uid) {
        databaseReference.child("curso").child(uid).removeValue();
        StorageReference imgRef = storageReference.child(uid+".jpeg");
        imgRef.delete();
    }

    private void salvarDados(String uidImagem) {
        Curso c = new Curso();
        c.setUid(uidImagem);
        c.setNome(editNome.getText().toString().trim());
        c.setCargaHoraria(Integer.parseInt(editCH.getText().toString().trim()));
        databaseReference.child("curso").child(c.getUid()).setValue(c);
        finish();
    }


    private void selecionarFoto() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Selecionar Foto"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && requestCode==1){
            Uri uri = data.getData();
            ivSimbolo.setImageURI(uri);
        }
    }

    private void componentesTela() {
        ivSimbolo = (ImageView)findViewById(R.id.ivSimbolo);
        editNome = (EditText) findViewById(R.id.edtiNome);
        editCH = (EditText)findViewById(R.id.editCH);
        btnAcao = (Button) findViewById(R.id.btnAcao);
        btnCancelar = (Button)findViewById(R.id.btnCancelar);

        if (cursoEscolhido.getUid() == null){
            btnAcao.setText("Salvar");
            cursoNovo = true;
        }else{
            btnAcao.setText("Alterar");
            cursoNovo = false;
            preencherTela();
        }
    }

    private void preencherTela() {
        editNome.setText(cursoEscolhido.getNome());
        editCH.setText(cursoEscolhido.getCargaHoraria()+"");

        StorageReference imgRef = storageReference.child(cursoEscolhido.getUid()+".jpeg");
        final long MEGABYTE = 1024 * 1024 * 5;
        imgRef.getBytes(MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivSimbolo.setImageBitmap(bitmap);
            }
        });
    }
}
