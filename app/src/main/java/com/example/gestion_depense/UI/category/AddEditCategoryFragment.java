// AddEditCategoryFragment.java
package com.example.gestion_depense.UI.category;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.gestion_depense.Data.Model.Category;
import com.example.gestion_depense.R;
import com.example.gestion_depense.ViewModel.CategoryViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AddEditCategoryFragment extends DialogFragment {

    private EditText edtName;
    private Category category;
    private CategoryViewModel viewModel;

    public static AddEditCategoryFragment newInstance(Category c) {
        AddEditCategoryFragment f = new AddEditCategoryFragment();
        if (c != null) {
            Bundle b = new Bundle();
            b.putString("id", c.getId());
            b.putString("name", c.getName());
            f.setArguments(b);
        }
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(requireActivity())
                .get(CategoryViewModel.class);

        View v = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_edit_category, null);

        edtName = v.findViewById(R.id.edtCategoryName);

        if (getArguments() != null) {
            edtName.setText(getArguments().getString("name"));
        }

        return new AlertDialog.Builder(getContext())
                .setTitle(getArguments() == null ?
                        getString(R.string.add_category) :
                        getString(R.string.edit_category))
                .setView(v)
                .setPositiveButton(getString(R.string.save), (d, w) -> {
                    String name = edtName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(getContext(),
                                getString(R.string.error_empty_name),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (getArguments() == null) {
                        // ajout → catégorie personnalisée
                        viewModel.addCategory(new Category(name, false));
                    } else {
                        // modification
                        Category c = new Category(name, false);
                        c.setId(getArguments().getString("id"));
                        viewModel.updateCategory(c);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .create();
    }
}