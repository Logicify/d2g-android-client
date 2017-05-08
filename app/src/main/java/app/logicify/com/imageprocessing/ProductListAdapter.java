package app.logicify.com.imageprocessing;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Vadim on 11.04.2017.
 */

public class ProductListAdapter extends BaseAdapter
{
    private Context mContext;
    private List<Product> mProductList;

    public ProductListAdapter(Context mContext, List<Product> mProductList) {
        this.mContext = mContext;
        this.mProductList = mProductList;
    }

    @Override
    public int getCount() {
        return mProductList.size();
    }

    @Override
    public Object getItem(int position) {
        return mProductList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.item_product_list, null);

        TextView tvName = (TextView) v.findViewById(R.id.item_name);
        TextView tvPrice = (TextView) v.findViewById(R.id.item_price);
        TextView tvCategory = (TextView) v.findViewById(R.id.item_category);

        tvName.setText(mProductList.get(position).getName());
        tvPrice.setText(String.valueOf(mProductList.get(position).getPrice()) + " грн");
        tvCategory.setText(mProductList.get(position).getCategory());

        v.setTag(mProductList.get(position).getId());
        return v;
    }

    public void updateList(List<Product> mProductList) {
        this.mProductList = mProductList;
        notifyDataSetChanged();
    }
}
