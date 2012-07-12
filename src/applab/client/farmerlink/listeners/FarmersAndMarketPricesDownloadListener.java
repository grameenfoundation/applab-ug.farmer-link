package applab.client.farmerlink.listeners;

import java.util.List;

public interface FarmersAndMarketPricesDownloadListener {
	void farmersAndMarketPricesDownloadingComplete(List<String> result);
}
