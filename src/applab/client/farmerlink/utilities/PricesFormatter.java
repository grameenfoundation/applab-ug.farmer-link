package applab.client.farmerlink.utilities;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class PricesFormatter {
	public static String formatPrice(Double price) {
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("en_US"));
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator(' ');
		return formatter.format(price);
	}
}
