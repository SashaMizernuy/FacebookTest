package ua.genesis.sasha.facebooktest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MyAdapter extends BaseAdapter {

    private List<Main> arrList;
    private LayoutInflater layoutInflater;
    Context ctx;



     MyAdapter(Context context, List<Main> arrList){
        this.arrList=arrList;
        ctx = context;
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return arrList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        if(view==null){
            view=layoutInflater.inflate(R.layout.list_people,parent,false);
        }

        Main main=getMain(position);


        TextView txtId=(TextView) view.findViewById(R.id.id);
        txtId.setText(String.valueOf(main.getId()));


        TextView txtName=(TextView) view.findViewById(R.id.name);
        txtName.setText(main.getName());

        TextView txtEmail=(TextView) view.findViewById(R.id.email);
        txtEmail.setText(main.getEmail());

        TextView txtPhone=(TextView) view.findViewById(R.id.phone);
        txtPhone.setText(main.getPhone());

        TextView txtWeb=(TextView) view.findViewById(R.id.web);
        txtWeb.setText(main.getWebsite());

        return view;
    }


    private Main getMain(int position){
        return (Main) getItem(position);
    }

}
