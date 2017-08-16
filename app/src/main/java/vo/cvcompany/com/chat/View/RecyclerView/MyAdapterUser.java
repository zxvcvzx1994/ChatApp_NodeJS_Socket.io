package vo.cvcompany.com.chat.View.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import vo.cvcompany.com.chat.R;

/**
 * Created by kh on 8/15/2017.
 */

public class MyAdapterUser extends RecyclerView.Adapter<MyAdapterUser.MyViewHolder> {
    private Context context;
    private ArrayList<String> arrayList =new ArrayList<String>() ;

    public MyAdapterUser(Context context, ArrayList<String> arrayList){
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.txtUser.setText(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return (arrayList==null)?0:arrayList.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtUser)
         TextView txtUser;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
