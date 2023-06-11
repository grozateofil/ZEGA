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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gt.zega.R;
import com.gt.zega.entity.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdapterForPersonalReports extends BaseAdapter {

    private ArrayList<String> arrayList;
    private Context context;
    private StorageReference storageRef;
    private String currentUidUser;
    private View rowView;

    private int position;
    private String type;
    private User currentUser;

    public AdapterForPersonalReports(String uid, User currentUser, ArrayList<String> arrayList, Context context, String type) {
        this.arrayList = arrayList;
        this.context = context;
        this.currentUidUser = uid;
        this.position = -1;
        this.type = type;
        this.currentUser = currentUser;

    }


    public AdapterForPersonalReports(int position, String uid, User currentUser, ArrayList<String> arrayList, Context context, String type) {
        this.arrayList = arrayList;
        this.context = context;
        this.currentUidUser = uid;
        this.position = position;
        this.type = type;
        this.currentUser = currentUser;
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
        rowView = convertView;
        int newPosition = this.position != -1 ? this.position : position;

        if (rowView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item_layout, parent, false);
        }

        TextView textViewFileName = rowView.findViewById(R.id.fileName);
        ImageView resolvedIcon = rowView.findViewById(R.id.resolved);
        ImageButton menuButton = rowView.findViewById(R.id.menu);

        try {
            textViewFileName.setText(arrayList.get(newPosition));
            if (arrayList.get(newPosition).contains("_REZ")) {
                resolvedIcon.setVisibility(View.VISIBLE);
                resolvedIcon.setImageResource(R.drawable.checkbox);
            } else if (arrayList.get(newPosition).contains("_ANULAT")) {
                resolvedIcon.setVisibility(View.VISIBLE);
                resolvedIcon.setImageResource(R.drawable.cancel_icon);
            } else {
                resolvedIcon.setVisibility(View.GONE);
            }
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
                        System.out.println("FILE URI: " + uri);
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
                            Toast.makeText(context, "Fisierul nu exita.\nVa rugam dati refresh paginii", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        if (!arrayList.get(newPosition).contains("_REZ") && (currentUser.getRole().equals("admin") || currentUser.getRole().equals("inginer"))) {
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                    bottomSheetDialog.setContentView(R.layout.report_menu);

                    TextView cancel = bottomSheetDialog.findViewById(R.id.bottom_sheet_dialog_cancel);
                    ImageView cancelIcon = bottomSheetDialog.findViewById(R.id.cancelIcon);
                    if (!arrayList.get(newPosition).contains("_ANULAT") && !arrayList.get(newPosition).contains("_REZ")) {
                        cancel.setVisibility(View.VISIBLE);
                        cancelIcon.setVisibility(View.VISIBLE);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                markAsCancelled(context, arrayList, newPosition);
                                bottomSheetDialog.dismiss();

                            }
                        });
                    } else {
                        cancel.setVisibility(View.GONE);
                        cancelIcon.setVisibility(View.GONE);
                    }

                    TextView noCancel = bottomSheetDialog.findViewById(R.id.bottom_sheet_dialog_noCancel);
                    ImageView noCancelIcon = bottomSheetDialog.findViewById(R.id.noCancelIcon);
                    if (arrayList.get(newPosition).contains("_ANULAT") && !arrayList.get(newPosition).contains("_REZ")) {
                        noCancel.setVisibility(View.VISIBLE);
                        noCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                markAsNoCancelled(context, arrayList, newPosition);
                                bottomSheetDialog.dismiss();

                            }
                        });
                    } else {
                        noCancelIcon.setVisibility(View.GONE);
                        noCancel.setVisibility(View.GONE);
                    }

                    TextView markAsResolvedButton = bottomSheetDialog.findViewById(R.id.bottom_sheet_dialog_mark_as_resolved);
                    ImageView resolvedIcon = bottomSheetDialog.findViewById(R.id.resolvedIcon);
                    if (arrayList.get(newPosition).contains("_REZ") || arrayList.get(newPosition).contains("_ANULAT")) {
//                    editButton.setEnabled(false);
                        markAsResolvedButton.setVisibility(View.GONE);
                        resolvedIcon.setVisibility(View.GONE);
                    } else {
                        markAsResolvedButton.setVisibility(View.VISIBLE);
                        resolvedIcon.setVisibility(View.VISIBLE);
                        markAsResolvedButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                markAsResolved(context, arrayList, newPosition);
                                bottomSheetDialog.dismiss();
                            }
                        });
                    }

                    bottomSheetDialog.show();
                }
            });
        } else {
            menuButton.setVisibility(View.GONE);
        }

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
        question.setText(context.getString(R.string.delete_file_confirmation, adapter.get(newPosition)));

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

    public void markAsCancelled(Context context, ArrayList<String> adapter, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.delete_file_confirmation, null);
        builder.setView(view);

        TextView question = view.findViewById(R.id.deleteFileDescription);
        Button cancelButton = view.findViewById(R.id.btn_Cancel);
        Button okButton = view.findViewById(R.id.btn_Y);
        question.setText(context.getString(R.string.anulare_raport, adapter.get(position)));

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);

        cancelButton.setOnClickListener(view1 -> dialog.cancel());

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("users").child(currentUidUser).child(type).child(adapter.get(position));
                int dot = adapter.get(position).lastIndexOf(".");
                String newFileName = adapter.get(position).substring(0, dot) + "_ANULAT" + adapter.get(position).substring(dot);
                String oldFileName = storageReference.getName();

                StorageReference newFileNameRef = FirebaseStorage.getInstance().getReference().child("users").child(currentUidUser).child(type).child(newFileName);

                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        File localFile = new File(context.getExternalFilesDir("Pdf"), oldFileName);
                        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                newFileNameRef.putFile(Uri.fromFile(localFile)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // Delete the original file
                                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Numele fisierului a fost schimbat
                                                notifyDataSetChanged();
                                            }
                                        });

                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        localFile.delete();
                                        Toast.makeText(context, "Va rugam să dați refresh paginii!", Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                    }
                                });
                                notifyDataSetChanged();
                            }
                        });
                        dialog.cancel();
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public void markAsNoCancelled(Context context, ArrayList<String> adapter, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.delete_file_confirmation, null);
        builder.setView(view);

        TextView question = view.findViewById(R.id.deleteFileDescription);
        Button cancelButton = view.findViewById(R.id.btn_Cancel);
        Button okButton = view.findViewById(R.id.btn_Y);
        question.setText(context.getString(R.string.inlaturare_anulare_raport, adapter.get(position)));

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);

        cancelButton.setOnClickListener(view1 -> dialog.cancel());

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("users").child(currentUidUser).child(type).child(adapter.get(position));
                String newFileName = adapter.get(position).replace("_ANULAT", "");
                String oldFileName = storageReference.getName();

                StorageReference newFileNameRef = FirebaseStorage.getInstance().getReference().child("users").child(currentUidUser).child(type).child(newFileName);

                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        File localFile = new File(context.getExternalFilesDir("Pdf"), oldFileName);
                        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                newFileNameRef.putFile(Uri.fromFile(localFile)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // Delete the original file
                                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // The file was successfully renamed
                                                // You can perform any additional operations here
                                                notifyDataSetChanged();
                                            }
                                        });

                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        localFile.delete();
                                        Toast.makeText(context, "Va rugam să dați refresh paginii!", Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                    }
                                });
                                notifyDataSetChanged();
                            }
                        });
                        dialog.cancel();
                        notifyDataSetChanged();
                    }
                });
            }
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

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("users").child(currentUidUser).child(type).child(adapter.get(position));
                int dot = adapter.get(position).lastIndexOf(".");
                String newFileName = adapter.get(position).substring(0, dot) + "_REZ" + adapter.get(position).substring(dot);
                String oldFileName = storageReference.getName();

                StorageReference newFileNameRef = FirebaseStorage.getInstance().getReference().child("users").child(currentUidUser).child(type).child(newFileName);

                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        File localFile = new File(context.getExternalFilesDir("Pdf"), oldFileName);

                        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                String time = new SimpleDateFormat("HH:mm:ss", Locale.forLanguageTag("ro")).format(new Date());
                                String date = new SimpleDateFormat("dd.MM.yyyy", Locale.forLanguageTag("ro")).format(new Date());

                                try {
                                    Document document = Jsoup.parse(localFile, "UTF-8");
                                    Element lastRow = document.select("table tr:last-child").first();

                                    Element numeInginer = document.createElement("tr");
                                    numeInginer.append("<th>Problema rezolvata de</th><td>" + currentUser.fullName() + "</td>");
                                    lastRow.after(numeInginer);

                                    lastRow = document.select("table tr:last-child").first();

                                    Element dataRezolvariiRaportului = document.createElement("tr");
                                    dataRezolvariiRaportului.append("<th>Data rezolvarii problemei</th><td>" + date + "</td>");
                                    lastRow.after(dataRezolvariiRaportului);

                                    lastRow = document.select("table tr:last-child").first();

                                    Element oraRezolvariiRaportului = document.createElement("tr");
                                    oraRezolvariiRaportului.append("<th>Ora rezolvarii problemei</th><td>" + time + "</td>");
                                    lastRow.after(oraRezolvariiRaportului);

                                    BufferedWriter writer = new BufferedWriter(new FileWriter(localFile));
                                    writer.write(document.outerHtml());
                                    writer.close();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                newFileNameRef.putFile(Uri.fromFile(localFile)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // Delete the original file
                                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // The file was successfully renamed
                                                // You can perform any additional operations here
                                                notifyDataSetChanged();
                                            }
                                        });

                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        localFile.delete();
                                        Toast.makeText(context, "Va rugam să dați refresh paginii!", Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                    }
                                });
                                notifyDataSetChanged();
                            }
                        });
                        dialog.cancel();
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

}
