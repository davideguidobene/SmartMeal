package it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.menu;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.model.Menu;
import it.unive.quadcore.smartmeal.model.Money;
import it.unive.quadcore.smartmeal.model.Product;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ProductViewHolder> {
    public static final class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView productNameTextView;
        private TextView ingredientsTextView;
        private TextView priceTextView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            this.productNameTextView = itemView.findViewById(R.id.product_name_text_view);
            this.ingredientsTextView = itemView.findViewById(R.id.ingredients_text_view);
            this.priceTextView = itemView.findViewById(R.id.price_text_view);
        }
    }

    @NonNull
    private final Activity activity;

    @NonNull
    private final Menu menu;

    @NonNull
    private final List<Product> productList;

    public MenuAdapter(@NonNull Activity activity, @NonNull Menu menu) {
        this.activity = activity;
        this.menu = menu;
        this.productList = new ArrayList<>(menu.getProducts());
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.product_row_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productNameTextView.setText(product.getName());
        holder.ingredientsTextView.setText(product.getDescription());

        Money price = product.getPrice();
        String decimalSeparator = activity.getString(R.string.decimal_separator);
        holder.priceTextView.setText(String.format("€ %s%s%s", price.getEuroString(), decimalSeparator, price.getCentString()));
    }

    @Override
    public int getItemCount() {
        return menu.numberOfProducts();
    }
}
