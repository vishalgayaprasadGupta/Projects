package com.example.myapplication.ManageRole;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.User;

import java.util.List;

public class updateUserRoleAdapter extends RecyclerView.Adapter<updateUserRoleAdapter.UserViewHolder> {
    private List<User> userList;
    private OnItemClickListener listener;

    public updateUserRoleAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_user_role_list, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());
        holder.status.setText(user.getStatus());
        holder.role.setText(user.getRole());

        holder.updateButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(user.getUid(), position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name, email,status,role;
        Button updateButton;

        public UserViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            updateButton = itemView.findViewById(R.id.updateButton);
            status=itemView.findViewById(R.id.status);
            role=itemView.findViewById(R.id.role);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String uid, int position);
    }
}
