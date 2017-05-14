package gor.gettplaces.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gor.gettplaces.R;
import gor.gettplaces.network.pojo.address.Prediction;

/**
 * Created by gilad on 14/05/17.
 */

public class AddressSuggestionAdapter extends RecyclerView.Adapter<AddressSuggestionAdapter.SuggestionViewHolder> implements Filterable {

    private static final String TAG = AddressSuggestionAdapter.class.getSimpleName();
    private SuggestionClickListener mSuggestionClickListener;
    private Context mContext;
    private List<Prediction> mData = new ArrayList<>();

    public AddressSuggestionAdapter(Context context, SuggestionClickListener suggestionClickListener) {
        super();
        mContext = context;
        mSuggestionClickListener = suggestionClickListener;
    }

    @Override
    public SuggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.address_suggestion, parent, false);

        SuggestionViewHolder viewHolder = new SuggestionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SuggestionViewHolder holder, final int position) {

        holder.title.setText(mData.get(position).getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSuggestionClickListener.OnSuggestionClick(mData.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<Prediction> suggestions) {
        mData.clear();
        mData.addAll(suggestions);
        notifyDataSetChanged();
    }

    //=============================================================================================
    //                               SuggestionViewHolder
    //=============================================================================================

    static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public SuggestionViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
        }
    }

    //=============================================================================================
    //                               public interface
    //=============================================================================================
    public interface SuggestionClickListener {
        void OnSuggestionClick(Prediction predictionSuggestion);
    }


    @Override
    public Filter getFilter() {
        Log.d(TAG, "getFilter()");
        return null;
    }

}
