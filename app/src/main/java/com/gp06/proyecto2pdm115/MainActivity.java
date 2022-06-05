package com.gp06.proyecto2pdm115;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.common.SignInButton;

public class MainActivity extends AppCompatActivity {
    /* url del servidor web */
    public static final String url_servidor_web = "https://proyecto2pdm115.000webhostapp.com/";

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;

    private EditText editTextCorreo;
    private EditText editTextPassword;
    private Button buttonIniciarSesion;
    private SignInButton buttonIniciarSesionGoogle;

    //private SignInClient oneTapClient;
    //private BeginSignInRequest signInRequest;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String CORREO = "nameKey";
    public static final String PASSWORD = "passwordKey";

    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Take instance of Action Bar
        // using getSupportActionBar and
        // if it is not Null
        // then call hide function
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        editTextCorreo = (EditText) findViewById(R.id.editTextCorreo);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonIniciarSesion = (Button) findViewById(R.id.buttonIniciarSesion);

        // Boton de inicio de sesion con Google
        buttonIniciarSesionGoogle = (SignInButton) findViewById(R.id.buttonLoginGoogle);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // agregar un escuchador de evento al boton de iniciar sesion normal.
        buttonIniciarSesion.setOnClickListener(view -> {
            String correo = editTextCorreo.getText().toString();
            String password = editTextPassword.getText().toString();

            if (TextUtils.isEmpty(correo) && TextUtils.isEmpty(password)) {
                // Cuando el campo de correo o contraseña están vacios.
                Snackbar.make(findViewById(R.id.buttonIniciarSesion),
                              "Por favor, introduzca su correo y contraseña",
                              Snackbar.LENGTH_SHORT).show();
            }
            else {
                String url = url_servidor_web + "verificar_login.php" + "?correo=" + correo + "&clave=" + password;

                // Aquí se debería verificar las credenciales: correo electrónico y contraseña
                String respuestaString = ControladorServicio.obtenerRespuestaPeticion(url, this);

                int respuesta = ControladorServicio.verificarCorreoYClave(respuestaString, this);

                if (respuesta == 1) {
                    // mostrar un snackbar con el correo con que se inició.
                    Snackbar.make(findViewById(R.id.buttonIniciarSesion),
                            "Inicio de sesión con " + correo, Snackbar.LENGTH_SHORT).show();

                    // y después guardar el correo en el archivo de texto con SharedPreferences.
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(CORREO, correo);
                    editor.commit();

                    Intent intent = new Intent(MainActivity.this, InicioActivity.class);
                    startActivity(intent);

                    // Esto es para que cuando presione el boton atrás no regrese a la pantalla anterior.
                    finish();
                }
                else {
                    // mostrar un snackbar indicando qué el correo o contraseña son incorrectos.
                    Snackbar.make(findViewById(R.id.buttonIniciarSesion),
                            "El correo o la cotraseña son incorrectos.", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });

        buttonIniciarSesionGoogle.setOnClickListener(v -> signIn());

        // [START config_signin] --------------
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END config_signin]

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

    }

    // [START on_start_check_user]
    @Override
    protected void onStart() {
        super.onStart();

        String correo = sharedpreferences.getString(CORREO,null);

        if (correo != null) {
            Intent i = new Intent(MainActivity.this, InicioActivity.class);
            startActivity(i);
            finish();
        }
    }
    // [END on_start_check_user]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());

                // Guardo el correo en un archivo de texto para acceder a este con SharedPreferences.
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(CORREO, account.getEmail());
                editor.commit();

                // mostrar un snackbar con el correo con que se inició.
                Snackbar.make(findViewById(R.id.buttonIniciarSesion),
                        "Inicio de sesión con " + account.getEmail(), Snackbar.LENGTH_SHORT).show();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }

    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void updateUI(FirebaseUser user) {
        FirebaseUser user1 =FirebaseAuth.getInstance().getCurrentUser();

        if (user1 != null) {
            Intent i = new Intent(MainActivity.this, InicioActivity.class);
            startActivity(i);
            finish();
        }
    }
}