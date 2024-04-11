package mx.udg.alumnos.miviaje.ui.cards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import mx.udg.alumnos.miviaje.databinding.FragmentCardsBinding;

public class CardsFragment extends Fragment {

    private FragmentCardsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CardsViewModel cardsViewModel =
                new ViewModelProvider(this).get(CardsViewModel.class);

        binding = FragmentCardsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textCards;
        cardsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}