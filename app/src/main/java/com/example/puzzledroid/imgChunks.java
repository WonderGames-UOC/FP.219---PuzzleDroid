package com.example.puzzledroid;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.puzzledroid.databinding.ActivityImgChunksBinding;

public class imgChunks extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityImgChunksBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityImgChunksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

}