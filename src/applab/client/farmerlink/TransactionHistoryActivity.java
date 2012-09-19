package applab.client.farmerlink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import applab.client.farmerlink.provider.TransactionProviderAPI;

public class TransactionHistoryActivity extends ListActivity {
    
    private final static String BUYING_OPTION = "Buying";
    private final static String SELLING_OPTION = "Selling";    
    private Button sellingButton;
    private Button buyingButton;
    private String selectedOption;
    private ArrayList<Transaction> transactions;
    ArrayAdapter<Transaction> transactionAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_history);
        selectedOption = BUYING_OPTION;
        getTransactions(selectedOption);
        transactionAdapter = new TransactionAdapter();
        setListAdapter(transactionAdapter);
        
        sellingButton = (Button)findViewById(R.id.selling);
        sellingButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                selectedOption = SELLING_OPTION;
                getTransactions(selectedOption);
                transactionAdapter.notifyDataSetChanged();
            }        
        });
        
        buyingButton = (Button)findViewById(R.id.buying);
        buyingButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                selectedOption = BUYING_OPTION;
                getTransactions(selectedOption);
                transactionAdapter.notifyDataSetChanged();
            }            
        });
        
    }
    
    private void getTransactions(String option) {
        
        String selection = TransactionProviderAPI.TransactionColumns.TRANSACTION_TYPE
                + "=?";
        String[] selectionArgs = null;
        if (option.equals(BUYING_OPTION)) {
            selectionArgs = new String [] { "buy" };
        }
        else {
            selectionArgs = new String [] { "marketSale", "buyerSale" }; 
        }
        
        Cursor transactionCursor = MarketLinkApplication
                .getInstance()
                .getContentResolver()
                .query(TransactionProviderAPI.TransactionColumns.CONTENT_URI,
                        null, selection, selectionArgs, null);
        transactionCursor.moveToFirst();
        List<Transaction> transactions = new ArrayList<Transaction>();
        for (int i = 0; i < transactionCursor.getCount(); i++) {
            Transaction transaction = new Transaction(
                    transactionCursor
                            .getString(transactionCursor
                                    .getColumnIndex(TransactionProviderAPI.TransactionColumns._ID)),
                    transactionCursor.getString(transactionCursor
                            .getColumnIndex(TransactionProviderAPI.TransactionColumns.TRANSACTION_TYPE)),
                    transactionCursor.getString(transactionCursor
                            .getColumnIndex(TransactionProviderAPI.TransactionColumns.TRANSACTION_DATE)),
                    transactionCursor.getString(transactionCursor
                            .getColumnIndex(TransactionProviderAPI.TransactionColumns.DISTRICT)),
                    transactionCursor.getString(transactionCursor
                            .getColumnIndex(TransactionProviderAPI.TransactionColumns.CROP)),
                    transactionCursor.getString(transactionCursor
                            .getColumnIndex(TransactionProviderAPI.TransactionColumns.QUANTITY)),
                    transactionCursor.getString(transactionCursor
                            .getColumnIndex(TransactionProviderAPI.TransactionColumns.TRANSPORT_FEE)),
                    transactionCursor.getString(transactionCursor
                            .getColumnIndex(TransactionProviderAPI.TransactionColumns.TRANSACTION_FEE)),
                    transactionCursor.getString(transactionCursor
                            .getColumnIndex(TransactionProviderAPI.TransactionColumns.UNITPRICE)),
                    transactionCursor.getString(transactionCursor
                            .getColumnIndex(TransactionProviderAPI.TransactionColumns.BUYER_NAME)));

            transactions.add(transaction);
        }
        transactionCursor.close();
    }

    class TransactionAdapter extends ArrayAdapter<Transaction> {
        
        private int[] colors = new int[] { 0x00000000, 0x30A9A9A9 };

        TransactionAdapter() {
            
            super(TransactionHistoryActivity.this, R.layout.transaction_history_list, R.id.name_text, transactions);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();            
            int colorPos = position % colors.length;
            
            View row = inflater.inflate(R.layout.transaction_history_list, parent, false);
            TextView nameTextView = (TextView)row.findViewById(R.id.name_text);
            nameTextView.setBackgroundColor(colors[colorPos]);
            nameTextView.setText("Buyer Name : " + transactions.get(position).buyer);
            

            TextView cropTextView = (TextView)row.findViewById(R.id.crop_name_text);
            cropTextView.setBackgroundColor(colors[colorPos]);
            cropTextView.setText("Crop : " + transactions.get(position).crop);
            
            TextView quantityView = (TextView)row.findViewById(R.id.quantity_text);
            quantityView.setBackgroundColor(colors[colorPos]);
            quantityView.setText("Quantity : " + transactions.get(position).quantity + " Kg");
            
            TextView priceTextView = (TextView)row.findViewById(R.id.price_text);
            priceTextView.setBackgroundColor(colors[colorPos]);
            priceTextView.setText("Price : " + transactions.get(position).unitPrice + " Shs.");
            
            TextView dateTextView = (TextView)row.findViewById(R.id.Date_text);
            dateTextView.setBackgroundColor(colors[colorPos]);
            dateTextView.setText("Date : " +  transactions.get(position).transactionDate);
            
            return row;
        }
    }
    
    private class Transaction {
        String transactionId;
        String transactionType;
        String transactionDate;
        String district;
        String crop;
        String quantity;
        String transportFee;
        String transactionFee;
        String unitPrice;
        String buyer;

        HashMap<String, String> attributes;
        
        Transaction(String transactionId, String transactionType,
                String transactionDate, String district, String crop,
                String quantity, String transportFee, String transactionFee,
                String unitPrice, String buyer) {
            this.transactionId = transactionId;
            this.transactionType = transactionType;
            this.transactionDate = transactionDate;
            this.district = district;
            this.crop = crop;
            this.quantity = quantity;
            this.transportFee = transportFee;
            this.transactionFee = transactionFee;
            this.unitPrice = unitPrice;
            this.buyer = buyer;
        }
    }
}