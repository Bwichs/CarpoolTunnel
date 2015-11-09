package carpooltunnel.slugging;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
public class DriverPendingAdapter extends BaseAdapter {
    public final String TAG = "PendingAdaptar";
    Context mContext;
    LayoutInflater inflater;
    private List<PassengerRouteClass> PassengerRouteClasslist = null;
    private ArrayList<PassengerRouteClass> arraylist;
    private String routeId;
    ParseObject route;

    public DriverPendingAdapter(Context context,
                                List<PassengerRouteClass> PassengerRouteClasslist) {
        mContext = context;
        this.PassengerRouteClasslist = PassengerRouteClasslist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<PassengerRouteClass>();
        this.arraylist.addAll(PassengerRouteClasslist);
    }
    public class ViewHolder {
        TextView booker;
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

    public void refresh(){
        notifyDataSetChanged();
    }
    View vew = null;
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.pendingview_item, null);
            vew = view;
            holder.booker = (TextView) view.findViewById(R.id.booker);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.booker.setText(PassengerRouteClasslist.get(position).getBooker());
        routeId = PassengerRouteClasslist.get(position).getRouteId();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseRoute");
        query.include("user");
        query.getInBackground(routeId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // object will be your game score
                    route = object;
                } else {
                    // something went wrong
                }
            }
        });
        // Listen for ListView Item Click
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                new AlertDialog.Builder(vew.getRootView().getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Sign up for route?")
                        .setMessage("Do you want to book this route?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(mContext.getApplicationContext(),
                                            "Accepted Passenger",
                                            Toast.LENGTH_LONG).show();
                                            int x = Integer.parseInt(route.getString("numPass")) - 1;
                                            route.put("numPass", String.valueOf(x));
                                            route.add("passengers", PassengerRouteClasslist.get(position).getBooker());
                                            List<String> temp = new ArrayList<String>();
                                            temp.add(PassengerRouteClasslist.get(position).getBooker());
                                            route.removeAll("bookers", temp);
                                            try{
                                                route.save();

                                            }catch(ParseException e){ Log.e(TAG, "error saving " + e.toString()); }
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });

        return view;
    }
}
