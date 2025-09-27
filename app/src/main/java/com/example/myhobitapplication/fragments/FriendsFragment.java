package com.example.myhobitapplication.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.activities.ProfileActivity;
import com.example.myhobitapplication.databinding.FragmentFriendsBinding;
import com.example.myhobitapplication.databinding.FragmentShopBinding;
import com.example.myhobitapplication.dto.UserInfoDTO;
import com.example.myhobitapplication.models.Avatar;
import com.example.myhobitapplication.models.Equipment;
import com.example.myhobitapplication.models.User;
import com.example.myhobitapplication.staticData.AvatarList;
import com.example.myhobitapplication.viewModels.FriendsViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class FriendsFragment extends Fragment {

    private FriendsViewModel mViewModel;
    private FragmentFriendsBinding binding;
    private List<UserInfoDTO> friends;

    public static FriendsFragment newInstance() {
        return new FriendsFragment();
    }
    public FriendsFragment(){}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFriendsBinding.inflate(inflater, container, false);

        mViewModel = new FriendsViewModel(requireContext());
        mViewModel.loadFriends(FirebaseAuth.getInstance().getUid());
        mViewModel.getFriends().observe(getViewLifecycleOwner(), list -> {
            mViewModel.loadAllUsers();
        });
        observeFriends();
        binding.buttonn2.setOnClickListener(v->{
            observeFriends();
        });

        binding.buttonn.setOnClickListener(v->{
            mViewModel.fiterFriends(binding.textView11.getText().toString());
            observeViewModel();
        });

        return  binding.getRoot();
    }

    public void observeViewModel(){
        mViewModel.getUsersFiltered().observe(getViewLifecycleOwner(),users->{
            binding.imgLayout2.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            for (User res : users) {
                View friendView = inflater.inflate(R.layout.item_friend, binding.imgLayout2, false);
                ImageView avatarView = friendView.findViewById(R.id.imageVieww);
                TextView usernameView = friendView.findViewById(R.id.textView);
                TextView addFriendButton = friendView.findViewById(R.id.buytext);

                for (Avatar a : AvatarList.getAvatarList()) {
                    if (a.getName().equals(res.getavatarName())) {
                        avatarView.setImageResource(a.getImage());
                        break;
                    }
                }
                if (friends.stream()
                        .anyMatch(f -> f.getusername().equals(res.getusername()))){
                    addFriendButton.setVisibility(View.INVISIBLE);
                    friendView.findViewById(R.id.button_layoutt).setVisibility(View.INVISIBLE);
                }

                usernameView.setText(res.getusername());

                addFriendButton.setOnClickListener(v -> {
                    Toast.makeText(getContext(), "Add friend: " + res.getusername(), Toast.LENGTH_SHORT).show();
                    mViewModel.addFriend(FirebaseAuth.getInstance().getCurrentUser().getUid(), res.getUid());
                    mViewModel.loadFriends(FirebaseAuth.getInstance().getUid());
                    mViewModel.getFriends().observe(getViewLifecycleOwner(), list -> {
                        mViewModel.loadAllUsers();
                    });
                    observeFriends();
                });

                binding.imgLayout2.addView(friendView);
            }
        });
    }

    public void observeFriends(){
        mViewModel.getFriends().observe(getViewLifecycleOwner(),users->{
            friends = users;
            binding.imgLayout2.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            for (UserInfoDTO res : users) {
                View friendView = inflater.inflate(R.layout.item_friend, binding.imgLayout2, false);
                ImageView avatarView = friendView.findViewById(R.id.imageVieww);
                TextView usernameView = friendView.findViewById(R.id.textView);
                TextView addFriendButton = friendView.findViewById(R.id.buytext);

                for (Avatar a : AvatarList.getAvatarList()) {
                    if (a.getName().equals(res.getavatarName())) {
                        avatarView.setImageResource(a.getImage());
                        break;
                    }
                }


                usernameView.setText(res.getusername());

               addFriendButton.setVisibility(View.INVISIBLE);
               friendView.findViewById(R.id.button_layoutt).setVisibility(View.INVISIBLE);

                binding.imgLayout2.addView(friendView);

                avatarView.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("USER_ID", res.getUid());
                    ProfileActivity profileFragment = new ProfileActivity();
                    profileFragment.setArguments(bundle);

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, profileFragment)
                            .addToBackStack(null)
                            .commit();
                });
            }
        });
    }

  /*  private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    String scannedData = result.getContents();

                    if (scannedData.startsWith("https://myhobitapplication/profil/")) {
                        String scannedUserId = scannedData.substring(scannedData.lastIndexOf("/") + 1);


                        mViewModel.addFriend(FirebaseAuth.getInstance().getCurrentUser().getUid(),scannedUserId);


                        Bundle bundle = new Bundle();
                        bundle.putString("USER_ID", userId);

                        ProfileActivity profileFragment = new ProfileActivity();
                        profileFragment.setArguments(bundle);

                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, profileFragment)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Toast.makeText(requireContext(), "Invalid QR code!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private void startQrScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a QR code");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        barcodeLauncher.launch(options);
    }

*/

}