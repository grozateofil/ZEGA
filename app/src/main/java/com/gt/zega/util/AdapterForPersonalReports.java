package com.gt.zega.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gt.zega.R;

import java.util.ArrayList;

public class AdapterForPersonalReports extends BaseAdapter {

    private ArrayList<String> arrayList;
    private Context context;
    private ImageView resolvedMark;
    private StorageReference storageRef;
    private String currentUidUser;

    private int position;
    private String type;

    public AdapterForPersonalReports(String uid, ArrayList<String> arrayList, Context context, String type) {
        this.arrayList = arrayList;
        this.context = context;
        this.currentUidUser = uid;
        this.position = -1;
        this.type = type;
    }


    public AdapterForPersonalReports(int position, String uid, ArrayList<String> arrayList, Context context, String type) {
        this.arrayList = arrayList;
        this.context = context;
        this.currentUidUser = uid;
        this.position = position;
        this.type = type;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }


    @Override
    public long getItemId(int i) {
        return i;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = convertView;
        int newPosition = this.position != -1 ? this.position : position;

        if (rowView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item_layout, parent, false);
        }

        TextView textViewFileName = rowView.findViewById(R.id.fileName);
        ImageButton menuButton = rowView.findViewById(R.id.menu);

        try {
            textViewFileName.setText(arrayList.get(newPosition));
        } catch (Exception e) {
            e.printStackTrace();
        }

        textViewFileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storageRef = FirebaseStorage.getInstance().getReference().child("users").child(currentUidUser).child(type).child(arrayList.get(newPosition));
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        System.out.println("FILE URI--------------------> " + uri);
                        try {
                            context.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e.getMessage().equals("Object does not exist at location.")) {
                            Toast.makeText(context, "Fisierul nu exita. Va rugam dati refresh paginii", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                bottomSheetDialog.setContentView(R.layout.report_menu);

                TextView deleteButton = bottomSheetDialog.findViewById(R.id.bottom_sheet_dialog_delete);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmDeleteFile(context, arrayList, position);
                        bottomSheetDialog.dismiss();

                    }
                });

                TextView editButton = bottomSheetDialog.findViewById(R.id.bottom_sheet_dialog_mark_as_resolved);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        markAsResolved(context, arrayList, position);
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.show();
            }
        });

        return rowView;
    }


    public void confirmDeleteFile(Context context, ArrayList<String> adapter, int position) {
        int newPosition = this.position != -1 ? this.position : position;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.delete_file_confirmation, null);
        builder.setView(view);

        TextView question = view.findViewById(R.id.deleteFileDescription);
        Button cancelButton = view.findViewById(R.id.btn_Cancel);
        Button okButton = view.findViewById(R.id.btn_Y);
        question.setText("Stergeti fisierul \"" + adapter.get(newPosition) + "\"?");

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);

        cancelButton.setOnClickListener(view1 -> dialog.cancel());


        okButton.setOnClickListener(view2 -> {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("users").child(currentUidUser).child(type).child(adapter.get(newPosition));
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    adapter.remove(adapter.get(newPosition));
                    notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
            dialog.cancel();
        });
    }

    public void markAsResolved(Context context, ArrayList<String> adapter, int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.delete_file_confirmation, null);
        builder.setView(view);

        TextView question = view.findViewById(R.id.deleteFileDescription);
        Button cancelButton = view.findViewById(R.id.btn_Cancel);
        Button okButton = view.findViewById(R.id.btn_Y);
        question.setText("Problema a fost rezolvata?");

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);

        cancelButton.setOnClickListener(view1 -> dialog.cancel());

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolvedMark = LayoutInflater.from(context).inflate(R.layout.list_item_layout, null).findViewById(R.id.resolved);
                resolvedMark.setImageResource(R.drawable.checkbox);
                notifyDataSetChanged();
//                Glide.with(context)
//                        .load(R.drawable.checkbox)
//                        .into(resolvedMark);
                dialog.cancel();
            }
        });


    }

}
