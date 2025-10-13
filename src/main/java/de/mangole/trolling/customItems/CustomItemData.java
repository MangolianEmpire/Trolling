package de.mangole.trolling.customItems;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.customItems.items.Glasses;

public class CustomItemData {

    private final Trolling trolling;

    private Glasses glasses;

    public CustomItemData(final Trolling trolling) {
        this.trolling = trolling;

        createCustomItems();
    }

    private void createCustomItems() {
        this.glasses = new Glasses(trolling);
    }

    public Glasses getGlasses() {
        return glasses;
    }
}


