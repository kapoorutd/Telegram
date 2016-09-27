package org.telegram.socialuser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;

import java.util.ArrayList;

/**
 * Created by ram on 3/6/16.
 */
public class CustomGridAdapter extends ArrayAdapter<TLRPC.TelegramUsers> {
    private Context mContext;
    private LayoutInflater inflater;

    public CustomGridAdapter(Context context, ArrayList<TLRPC.TelegramUsers> users) {
        super(context, 0, users);
        mContext = context;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

   public static class ViewHolder {
        TextView textView1;
        BackupImageView avatarImageView1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.grid_single, null);
            viewHolder.textView1 = (TextView) convertView.findViewById(R.id.grid_text);
            viewHolder.avatarImageView1 = (BackupImageView) convertView.findViewById(R.id.grid_image);
            viewHolder.avatarImageView1.setRoundRadius(AndroidUtilities.dp(30));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

             TLRPC.TelegramUsers user = getItem(position);

              viewHolder.avatarImageView1.setImage(user.photo != null ? user.photo.photo_small : null, "50_50", new AvatarDrawable(user, false), true);
             if(user !=null &&  user.getname()!=null ) {
                 viewHolder.textView1.setText(user.getname());

                 if(user.photo !=null && user.photo.photo_id != 0){
            viewHolder.avatarImageView1.setImage(user.photo.photo_small, "50_50",new AvatarDrawable(user,false),true);

                 }
                 else if(user.photo.photo_id == 0){
                     viewHolder.avatarImageView1.setImage(null, "50_50",new AvatarDrawable(user,false),true);
                 }
             }

            //checkAndUpdateAvatar(viewHolder.avatarImageView1, user);
        return convertView;
    }
}
