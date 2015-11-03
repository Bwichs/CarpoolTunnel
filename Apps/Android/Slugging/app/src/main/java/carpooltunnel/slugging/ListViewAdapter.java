package carpooltunnel.slugging;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<PassengerRouteClass> PassengerRouteClasslist = null;
    private ArrayList<PassengerRouteClass> arraylist;

    public ListViewAdapter(Context context,
                           List<PassengerRouteClass> PassengerRouteClasslist) {
        mContext = context;
        this.PassengerRouteClasslist = PassengerRouteClasslist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<PassengerRouteClass>();
        this.arraylist.addAll(PassengerRouteClasslist);
    }

    public class ViewHolder {
        TextView depDay;
        TextView depTime;
        TextView from;
        TextView numPass;
        TextView to;
        TextView driverUser;
        TextView createdAt;
        TextView updatedAt;
    }

    @Override
    public int getCount() {
        return PassengerRouteClasslist.size();
    }

    @Override
    public PassengerRouteClass getItem(int position) {
        return PassengerRouteClasslist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_item, null);
            // Locate the TextViews in listview_item.xml
            holder.depDay = (TextView) view.findViewById(R.id.depDay);
            holder.depTime = (TextView) view.findViewById(R.id.depTime);
            holder.from = (TextView) view.findViewById(R.id.from);
            holder.numPass = (TextView) view.findViewById(R.id.numPass);
            holder.to = (TextView) view.findViewById(R.id.to);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.depDay.setText(PassengerRouteClasslist.get(position).getDepDay());
        holder.depTime.setText(PassengerRouteClasslist.get(position).getDepTime());
        holder.from.setText(PassengerRouteClasslist.get(position).getFrom());
        holder.numPass.setText(PassengerRouteClasslist.get(position).getNumPass());
        holder.to.setText(PassengerRouteClasslist.get(position).getTo());

        // Listen for ListView Item Click
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Send single item click data to SingleItemView Class
                Intent intent = new Intent(mContext, SingleItemView.class);
                // Pass all data rank
                intent.putExtra("depDay",
                        (PassengerRouteClasslist.get(position).getDepDay()));
                // Pass all data country
                intent.putExtra("depTime",
                        (PassengerRouteClasslist.get(position).getDepTime()));
                // Pass all data population
                intent.putExtra("from",
                        (PassengerRouteClasslist.get(position).getFrom()));
                intent.putExtra("numPass",
                        (PassengerRouteClasslist.get(position).getNumPass()));
                intent.putExtra("to",
                        (PassengerRouteClasslist.get(position).getTo()));
                intent.putExtra("driverUser",
                        (PassengerRouteClasslist.get(position).getDriverUser()));
                intent.putExtra("createdAt",
                        (PassengerRouteClasslist.get(position).getCreatedAt()));
                intent.putExtra("updatedAt",
                        (PassengerRouteClasslist.get(position).getUpdatedAt()));
                intent.putExtra("routeId",
                        (PassengerRouteClasslist.get(position).getRouteId()));
                // Start SingleItemView Class
                mContext.startActivity(intent);
            }
        });

        return view;
    }
}
