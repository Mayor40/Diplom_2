import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


@Data
@NoArgsConstructor

public class Order {

    private ArrayList<String> ingredients = new ArrayList<>();

    public Order(ArrayList<String> ingredients) {
        this.ingredients = ingredients;

    }
}
