package com.gmail.yair.gmyaguitarra;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private List<guitarras> listPerson = new ArrayList<guitarras>();
    ArrayAdapter<guitarras> arrayAdapterPersona;

    EditText fechas, marcas, colores;
    Spinner tipos,cuerdas;
    Calendar calendario;

    TextView txtTipo, txtCuerdas;
    Button btnconsulta;
    ListView listV_personas;


    private int nYearIn1, nMonthIn1, nDayIn1, sYearin1, sMonthIn1, sDayIn1;
    static final int DATE_ID = 0;

    Calendar C = Calendar.getInstance();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    guitarras guitarraSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sMonthIn1 = C.get(Calendar.MONTH);
        sDayIn1 = C.get(Calendar.DAY_OF_MONTH);
        sYearin1 = C.get(Calendar.YEAR);

        fechas = findViewById(R.id.fecha);
        marcas = findViewById(R.id.marca);
        colores = findViewById(R.id.color);
        tipos = findViewById(R.id.tipo);
        cuerdas = findViewById(R.id.cuerda);
        btnconsulta=findViewById(R.id.btnconsulta);

        txtTipo=findViewById(R.id.txtcuerdas);
        txtCuerdas=findViewById(R.id.txttipo);

        listV_personas = findViewById(R.id.lv_datosPersonas);
        btnconsulta.setOnClickListener(this);




        fechas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_ID);
            }
        });
        inicializarFirebase();
        listarDatos();

        listV_personas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                guitarraSelected = (guitarras) parent.getItemAtPosition(position);
                marcas.setText(guitarraSelected.getMarca());
                fechas.setText(guitarraSelected.getFecha());
                txtTipo.setText(guitarraSelected.getTipo());
                colores.setText(guitarraSelected.getColor());
                txtCuerdas.setText(guitarraSelected.getCuerdas());
            }
        });
    }
    private void listarDatos() {
        databaseReference.child("Guitarras").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPerson.clear();
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    guitarras p = objSnaptshot.getValue(guitarras.class);
                    listPerson.add(p);

                    arrayAdapterPersona = new ArrayAdapter<guitarras>(MainActivity.this, android.R.layout.simple_list_item_1, listPerson);
                    listV_personas.setAdapter(arrayAdapterPersona);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void onClick(View vista){
        agregar();
    }

    private void colocar_fecha() {
        fechas.setText((nMonthIn1 + 1) + "-" + nDayIn1 + "-" + nYearIn1 + " ");
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            nYearIn1 = year;
            nMonthIn1 = month;
            nDayIn1 = dayOfMonth;
            colocar_fecha();
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_ID:
                return new DatePickerDialog(this, mDateSetListener, sYearin1, sMonthIn1, sDayIn1);
        }
        return null;
    }


    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_guarda: {
                agregar();
                break;
            }

            case R.id.action_modifica:{
                guitarras p = new guitarras();
                p.setGuitarrasid(guitarraSelected.getGuitarrasid());
                p.setMarca(marcas.getText().toString().trim());
                p.setFecha(fechas.getText().toString().trim());
                p.setColor(colores.getText().toString().trim());
                p.setTipo(tipos.getSelectedItem().toString().trim());
                p.setCuerdas(cuerdas.getSelectedItem().toString().trim());
                databaseReference.child("Guitarras").child(p.getGuitarrasid()).setValue(p);
                Toast.makeText(this,"Actualizado", Toast.LENGTH_LONG).show();
                limpiarCajas();
                break;
            }

            case R.id.action_limpia: {
                limpiarCajas();
                break;
            }
            case R.id.action_elimina:{
                guitarras p = new guitarras();
                p.setGuitarrasid(guitarraSelected.getGuitarrasid());
                databaseReference.child("Guitarras").child(p.getGuitarrasid()).removeValue();
                Toast.makeText(this,"Eliminado", Toast.LENGTH_LONG).show();
                limpiarCajas();
                break;
            }
            default:
                break;
        }
        return true;
    }
    private void agregar(){

        String fecha = fechas.getText().toString();
        String marca = marcas.getText().toString();
        String tipo = tipos.getSelectedItem().toString();
        String color = colores.getText().toString();
        String cuerda = cuerdas.getSelectedItem().toString();
        if (fecha.equals("") || marca.equals("") || tipo.equals("") || color.equals("")) {
            validacion();
        } else {
            guitarras p = new guitarras();
            p.setGuitarrasid(UUID.randomUUID().toString());
            p.setFecha(fecha);
            p.setMarca(marca);
            p.setTipo(tipo);
            p.setColor(color);
            p.setCuerdas(cuerda);
            databaseReference.child("Guitarras").child(p.getGuitarrasid()).setValue(p);
            Toast.makeText(this, "Agregado", Toast.LENGTH_LONG).show();
            limpiarCajas();
        }
    }

    private void limpiarCajas() {
        fechas.setText("");
        marcas.setText("");
        tipos.setSelection(0);
        cuerdas.setSelection(0);
        colores.setText("");
        txtCuerdas.setText("");
        txtTipo.setText("");
    }

    private void validacion() {
        String fecha = fechas.getText().toString();
        String marca = marcas.getText().toString();
        String tipo = tipos.getSelectedItem().toString();
        String color = colores.getText().toString();
        String cuerda = cuerdas.getSelectedItem().toString();
        if (fecha.equals("")){
            fechas.setError("Ingresar fecha");
        }
        else if (marca.equals("")){
            marcas.setError("Ingresar marca");
        }
        else if (color.equals("")){
            colores.setError("Ingresar color");
        }
    }

}
    /*

    FirebaseFirestore objectFirebaseFirestore;

    EditText fechas, marcas, colores;
    Calendar calendario;
    int currentHour;
    int currentMinute;
    String ampm;
    Button btnconsulta;
    TextView txtValores,txttipo,txtcuerdas;
    RadioButton racustica, relectrica, relectroacustica,rseis,rdoce;



    private int nYearIn1, nMonthIn1, nDayIn1, sYearin1, sMonthIn1, sDayIn1;
    static final int DATE_ID = 0;

    Calendar C = Calendar.getInstance();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    DocumentReference objectDocumentReference;

    private int dia, mes, anio;
    private String fechaDeNacimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{

            objectFirebaseFirestore = FirebaseFirestore.getInstance();

            sMonthIn1 = C.get(Calendar.MONTH);
            sDayIn1 = C.get(Calendar.DAY_OF_MONTH);
            sYearin1 = C.get(Calendar.YEAR);

            racustica=findViewById(R.id.racustica);
            relectrica=findViewById(R.id.relectrica);
            relectroacustica=findViewById(R.id.relectro_acustica);
            rseis=findViewById(R.id.rseis);
            rdoce=findViewById(R.id.rdoce);

            fechas = findViewById(R.id.fecha);
            marcas = findViewById(R.id.marca);
            colores = findViewById(R.id.color);
            txttipo = findViewById(R.id.tipos);
            txtcuerdas = findViewById(R.id.cuerdas);
            txtValores = findViewById(R.id.valores);
            btnconsulta=findViewById(R.id.btnconsulta);

            btnconsulta.setOnClickListener(this);


        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onClick(View v) {

agregarFireStore();
    }

    private void agregarFireStore() {
        String color= colores.getText().toString();
        String fecha= fechas.getText().toString();
        String marca= marcas.getText().toString();
        try{
            if (racustica.isChecked()){
                txttipo.setText("Acustica");
            }else if(relectrica.isChecked()){
                txttipo.setText("Electica");
            }else if(relectroacustica.isChecked()){
                txttipo.setText("Electro acustica");
            }

            if(rseis.isChecked()){
                txtcuerdas.setText("6");
            }else{
                txtcuerdas.setText("12");
            }

            if (!marcas.getText().toString().isEmpty()
                    && !fechas.getText().toString().isEmpty()
                    && !colores.getText().toString().isEmpty())

            {

                Map<String, Object > objectMap = new HashMap<>();
                objectMap.put("Colores",color);
                objectMap.put("Cuerdas", txtcuerdas.getText().toString());
                objectMap.put("Fechas", fecha);
                objectMap.put("Marca", marca);
                objectMap.put("Tipos", txttipo.getText().toString());

                objectFirebaseFirestore.collection("guitarras").add(objectMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                  @Override
                                                  public void onSuccess(DocumentReference documentReference) {
                                                      Toast.makeText(MainActivity.this, "Los Datos fueron almacenados", Toast.LENGTH_SHORT).show();
                                                  }
                                              }
                        )
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error al agregar los Datos", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else {
                Toast.makeText(this, "Favor de llenar todos los campos", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void obtenerValores(View view)
    {

        try{

            if (!marcas.getText().toString().isEmpty()){
                objectDocumentReference = objectFirebaseFirestore.collection("Guitarras").document(
                        marcas.getText().toString()
                );

                objectDocumentReference.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                String marcas = documentSnapshot.getId();
                                String fechas = documentSnapshot.getString("Fechas");
                                String tipos = documentSnapshot.getString("Tipos");
                                String colores = documentSnapshot.getString("Colores");
                                String cuerdas = documentSnapshot.getString("Cuerdas");

                                txtValores.setText(


                                        "Marcas: "+marcas+"\n"+
                                                "Fecha: "+fechas+"\n"+
                                                "Tipo: "+tipos+"\n"+
                                                "Color: "+colores+"\n"+
                                                "Cuerdas: "+cuerdas
                                );
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }




}
*/


