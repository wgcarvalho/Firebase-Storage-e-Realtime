package com.wgc.projetostorageerealtime;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CursoAdapter  extends BaseAdapter {

    private Context context;
    private List<Curso> list;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public CursoAdapter(Context context, List<Curso> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = View.inflate(context,R.layout.item_list,null);

        final ImageView ivFoto = (ImageView)v.findViewById(R.id.ivFotoItem);
        TextView txtNome = (TextView) v.findViewById(R.id.txNomeItem);
        TextView txtCH = (TextView)v.findViewById(R.id.txCHItem);

        txtNome.setText(list.get(i).getNome());
        txtCH.setText(list.get(i).getCargaHoraria()+" horas");

        StorageReference imgRef = storageReference.child(list.get(i).getUid()+".jpeg");
        final long MEGAYTE = 1024 * 1024 * 5;
        imgRef.getBytes(MEGAYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivFoto.setImageBitmap(bitmap);
            }
        });

        return v;
    }
}
