import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.market.MarketItem;
import myvk.PullUp;
import myvk.utils.ShopParser;

import java.util.List;

public class RemoveItems {

    public static void main(String[] args) throws ClientException, ApiException {

        ShopParser parser = new ShopParser(134278196);
        List<MarketItem> list = (List<MarketItem>) parser.getItems(0)[0];
        for (MarketItem item : list) {
            PullUp.getApp().api().market().delete(PullUp.getApp().user(), 134278196, item.getId()).execute();
        }


    }

}
