package com.example.myhobitapplication.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.activities.ProfileActivity;
import com.example.myhobitapplication.databinding.FragmentAllianceBinding;
import com.example.myhobitapplication.databinding.FragmentFriendsBinding;
import com.example.myhobitapplication.dto.UserInfoDTO;
import com.example.myhobitapplication.models.Alliance;
import com.example.myhobitapplication.models.Avatar;
import com.example.myhobitapplication.models.User;
import com.example.myhobitapplication.staticData.AvatarList;
import com.example.myhobitapplication.viewModels.AllianceViewModel;
import com.example.myhobitapplication.viewModels.FriendsViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


public class AllianceFragment extends Fragment {

    public AllianceFragment() {

    }
    private FriendsViewModel friendsViewModel;
    private AllianceViewModel allianceViewModel;
    private FragmentAllianceBinding binding;
    private Alliance alliance;
    private List<User> members;
    private boolean isMembersViewActive = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAllianceBinding.inflate(inflater, container, false);
        allianceViewModel = new ViewModelProvider(this).get(AllianceViewModel.class);
        friendsViewModel = new ViewModelProvider(requireActivity()).get(FriendsViewModel.class);
        allianceViewModel.getAlliance(FirebaseAuth.getInstance().getCurrentUser().getUid());
        friendsViewModel.loadFriends(FirebaseAuth.getInstance().getCurrentUser().getUid());

        observeViewModel();

        binding.buttonn3.setOnClickListener(v->{
            if (isMembersViewActive) {
                binding.memb.setText("Members");
                binding.button3text.setText("Invite friends");
                observeViewModel();
            } else {
                binding.memb.setText("Invite friends");
                binding.button3text.setText("Finish inviting");
                observeFriends();
            }
            isMembersViewActive = !isMembersViewActive;
        });


        return  binding.getRoot();
    }

    public void observeViewModel(){
        allianceViewModel.getUserAlliance().observe(getViewLifecycleOwner(),alliance1 -> {
            if(alliance1!=null) {
                alliance = alliance1;
                binding.allianceName.setText(alliance1.getName());
                allianceViewModel.getUsersInAlliance();
            }
        });
        allianceViewModel.getOwner().observe(getViewLifecycleOwner(),owner->{
            binding.allianceNameLeader.setText("Leader : " + owner.getusername());
        });

        binding.openChat.setOnClickListener(v -> {

            MessageFragment messageFragment = new MessageFragment();


            Bundle args = new Bundle();
            args.putString("ALLIANCE_ID", alliance.getId());
            messageFragment.setArguments(args);


            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, messageFragment)
                    .addToBackStack(null)
                    .commit();
        });

        allianceViewModel.getMembers().observe(getViewLifecycleOwner(),members->{
            binding.imgLayout2.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            this.members = members;
            for (User res : members) {
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

                friendView.findViewById(R.id.buytext).setVisibility(View.INVISIBLE);
                friendView.findViewById(R.id.button_layoutt).setVisibility(View.INVISIBLE);

                binding.imgLayout2.addView(friendView);
            }
        });
    }

   public void  observeFriends(){
        friendsViewModel.getFriends().observe(getViewLifecycleOwner(),users->{
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

                if (members.stream()
                        .anyMatch(f -> f.getusername().equals(res.getusername()))){
                    addFriendButton.setVisibility(View.INVISIBLE);
                    friendView.findViewById(R.id.button_layoutt).setVisibility(View.INVISIBLE);
                }
                usernameView.setText(res.getusername());

                addFriendButton.setText("Invite Member");
                friendView.findViewById(R.id.buttonnConfi).setOnClickListener(v->{
                    String invitedUserUid = res.getUid();
                    String inviterName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    String allianceName = alliance.getName();

                    allianceViewModel.sendInvite(invitedUserUid, inviterName, allianceName);
                    allianceViewModel.getCreatedREsponse().observe(getViewLifecycleOwner(),response->{
                        Toast.makeText(requireContext(),response,Toast.LENGTH_SHORT).show();
                    });
                });

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


}