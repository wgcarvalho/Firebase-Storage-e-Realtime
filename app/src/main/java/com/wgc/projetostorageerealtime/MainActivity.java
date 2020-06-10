package com.wgc.projetostorageerealtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button btnNovo;
    private ListView lvMain;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private List<Curso> cursoList;
    private CursoAdapter cursoAdapter;

    private Curso cursoEscolhido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        componentesTela();
        bancoDeDados();
        eventosclicks();
        registerForContextMenu(lvMain);
    }

    private void bancoDeDados() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        cursoList = new ArrayList<>();

        databaseReference.child("curso").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cursoList.clear();
                for (DataSnapshot obj: dataSnapshot.getChildren()){
                    Curso c = obj.getValue(Curso.class);
                    cursoList.add(c);
                }
                cursoAdapter = new CursoAdapter(getApplicationContext(),cursoList);
                lvMain.setAdapter(cursoAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void eventosclicks() {
        btnNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Cadastro.class);
                startActivity(intent);
            }
        });

        lvMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                cursoEscolhido = (Curso) parent.getItemAtPosition(position);
                return false;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem mAlterar = menu.add("Alterar Curso");
        MenuItem mDelete = menu.add("Deletar Curso");

        mAlterar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                alterarCurso();
                return false;
            }
        });

        mDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                deleteCurso();
                return false;
            }
        });

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    private void deleteCurso() {
        databaseReference.child("curso").child(cursoEscolhido.getUid()).removeValue();
        StorageReference imgRef = storageReference.child(cursoEscolhido.getUid()+".jpeg");
        imgRef.delete();
    }

    private void alterarCurso() {
        Intent intent = new Intent(getApplicationContext(), Cadastro.class);
        intent.putExtra("cursoUid",cursoEscolhido.getUid());
        intent.putExtra("cursoNome",cursoEscolhido.getNome());
        intent.putExtra("cursoCH",cursoEscolhido.getCargaHoraria());
        startActivity(intent);
    }

    private void componentesTela() {
        btnNovo = (Button)findViewById(R.id.btnNovo);
        lvMain = (ListView)findViewById(R.id.lvMain);
    }
}
